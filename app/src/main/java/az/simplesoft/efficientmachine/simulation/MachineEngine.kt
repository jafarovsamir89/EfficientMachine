package az.simplesoft.efficientmachine.simulation

import java.util.ArrayDeque
import kotlin.math.max
import kotlin.math.min

class MachineEngine(
    val columns: Int = 8,
    val rows: Int = 10,
    val ticksPerSecond: Int = 20,
    val powerCapacity: Int = 28,
) {
    private data class Delivery(
        val tick: Long,
        val flow: Double,
        val output: Double,
    )

    private data class Waste(val tick: Long, val flow: Double)

    private val modules = linkedMapOf<Cell, PlacedModule>()
    private val particles = mutableListOf<Particle>()
    private val deliveries = ArrayDeque<Delivery>()
    private val wastes = ArrayDeque<Waste>()
    private val pendingCommands = ArrayDeque<MachineCommand>()
    private val undoLayouts = ArrayDeque<MachineLayout>()

    private var nextModuleId = 1L
    private var nextParticleId = 1L
    private var tick = 0L

    init {
        installFixedEndpoints()
    }

    fun enqueue(command: MachineCommand) {
        pendingCommands.addLast(command)
    }

    fun applyNow(command: MachineCommand): Boolean = applyCommand(command, recordUndo = true)

    fun step(steps: Int = 1): MachineSnapshot {
        repeat(steps.coerceAtLeast(1)) { stepOnce() }
        return snapshot()
    }

    fun snapshot(): MachineSnapshot {
        val metrics = calculateMetrics()
        return MachineSnapshot(
            tick = tick,
            columns = columns,
            rows = rows,
            modules = modules.values.sortedWith(compareBy<PlacedModule> { it.cell.row }.thenBy { it.cell.col }),
            particles = particles.map {
                ParticleSnapshot(
                    id = it.id,
                    cell = it.cell,
                    direction = it.direction,
                    progress = it.progress,
                    flow = it.flow,
                    value = it.value,
                )
            },
            metrics = metrics,
        )
    }

    fun currentLayout(): MachineLayout = MachineLayout(modules.values.toList())

    fun restoreLayout(layout: MachineLayout) {
        modules.clear()
        layout.modules.forEach { modules[it.cell] = it }
        nextModuleId = (layout.modules.maxOfOrNull { it.id } ?: 0L) + 1L
        resetTransientSimulation()
    }

    fun undo(): Boolean {
        if (undoLayouts.isEmpty()) return false
        restoreLayout(undoLayouts.removeLast())
        return true
    }

    fun powerUsed(): Int = modules.values.sumOf { it.type.powerCost }

    fun canPlace(type: ModuleType, cell: Cell): Boolean {
        if (type == ModuleType.SOURCE || type == ModuleType.RECEIVER) return false
        if (!isInside(cell) || modules.containsKey(cell)) return false
        return powerUsed() + type.powerCost <= powerCapacity
    }

    private fun stepOnce() {
        while (pendingCommands.isNotEmpty()) {
            applyCommand(pendingCommands.removeFirst(), recordUndo = true)
        }

        spawnFromSources()
        advanceParticles()
        tick++
        pruneRollingWindows()
    }

    private fun applyCommand(command: MachineCommand, recordUndo: Boolean): Boolean {
        return when (command) {
            is MachineCommand.Place -> {
                if (!canPlace(command.type, command.cell)) return false
                if (recordUndo) rememberUndo()
                modules[command.cell] = PlacedModule(
                    id = nextModuleId++,
                    type = command.type,
                    cell = command.cell,
                    rotation = command.rotation,
                )
                resetTransientSimulation()
                true
            }

            is MachineCommand.Rotate -> {
                val existing = modules[command.cell] ?: return false
                if (existing.type == ModuleType.SOURCE || existing.type == ModuleType.RECEIVER) return false
                if (recordUndo) rememberUndo()
                modules[command.cell] = existing.copy(rotation = existing.rotation.rotateClockwise())
                resetTransientSimulation()
                true
            }

            is MachineCommand.Remove -> {
                val existing = modules[command.cell] ?: return false
                if (existing.type == ModuleType.SOURCE || existing.type == ModuleType.RECEIVER) return false
                if (recordUndo) rememberUndo()
                modules.remove(command.cell)
                resetTransientSimulation()
                true
            }

            MachineCommand.Reset -> {
                if (recordUndo) rememberUndo()
                modules.clear()
                installFixedEndpoints()
                resetTransientSimulation()
                true
            }
        }
    }

    private fun rememberUndo() {
        if (undoLayouts.size >= 32) undoLayouts.removeFirst()
        undoLayouts.addLast(currentLayout())
    }

    private fun installFixedEndpoints() {
        val row = rows / 2
        val sourceCell = Cell(0, row)
        val receiverCell = Cell(columns - 1, row)
        modules[sourceCell] = PlacedModule(
            id = nextModuleId++,
            type = ModuleType.SOURCE,
            cell = sourceCell,
            rotation = Direction.EAST,
        )
        modules[receiverCell] = PlacedModule(
            id = nextModuleId++,
            type = ModuleType.RECEIVER,
            cell = receiverCell,
            rotation = Direction.WEST,
        )
    }

    private fun resetTransientSimulation() {
        particles.clear()
        deliveries.clear()
        wastes.clear()
    }

    private fun spawnFromSources() {
        val cadenceTicks = max(1, ticksPerSecond / 2) // 2 packets/sec baseline.
        if (tick % cadenceTicks.toLong() != 0L) return

        modules.values
            .asSequence()
            .filter { it.type == ModuleType.SOURCE }
            .sortedBy { it.id }
            .forEach { source ->
                particles += Particle(
                    id = nextParticleId++,
                    cell = source.cell,
                    direction = source.rotation,
                    progress = 0.05,
                    speedCellsPerSecond = 3.5,
                    flow = 1.0,
                    value = 1.0,
                    processedModuleIds = setOf(source.id),
                )
            }
    }

    private fun advanceParticles() {
        if (particles.isEmpty()) return

        val survivors = ArrayList<Particle>(particles.size)
        val spawned = ArrayList<Particle>()
        val stableOrder = particles.sortedBy { it.id }

        for (original in stableOrder) {
            var particle = original.copy(
                progress = original.progress + original.speedCellsPerSecond / ticksPerSecond.toDouble(),
            )
            var alive = true
            var crossings = 0

            while (alive && particle.progress >= 1.0 && crossings < 4) {
                crossings++
                val nextCell = particle.cell.neighbor(particle.direction)
                particle = particle.copy(cell = nextCell, progress = particle.progress - 1.0)

                if (!isInside(nextCell)) {
                    wastes.addLast(Waste(tick, particle.flow))
                    alive = false
                    break
                }

                val module = modules[nextCell]
                if (module != null && module.id !in particle.processedModuleIds) {
                    val processed = processModule(particle, module)
                    particle = processed.primary
                    spawned += processed.spawned
                    alive = !processed.consumed
                }
            }

            if (alive) survivors += particle
        }

        // Safety cap protects accidental splitter loops while keeping deterministic ordering.
        val capacity = 2_000
        particles.clear()
        particles += (survivors + spawned).sortedBy { it.id }.take(capacity)
    }

    private data class ModuleResult(
        val primary: Particle,
        val spawned: List<Particle> = emptyList(),
        val consumed: Boolean = false,
    )

    private fun processModule(particle: Particle, module: PlacedModule): ModuleResult {
        val processedIds = particle.processedModuleIds + module.id
        val marked = particle.copy(processedModuleIds = processedIds)

        return when (module.type) {
            ModuleType.SOURCE -> ModuleResult(marked)

            ModuleType.RECEIVER -> {
                val output = marked.flow * marked.value
                deliveries.addLast(Delivery(tick, marked.flow, output))
                ModuleResult(marked, consumed = true)
            }

            ModuleType.TURN -> ModuleResult(marked.copy(direction = module.rotation))

            ModuleType.BOOST -> ModuleResult(
                marked.copy(
                    speedCellsPerSecond = min(8.0, marked.speedCellsPerSecond * 1.35),
                    flow = marked.flow * 1.30,
                    value = marked.value * 0.96,
                ),
            )

            ModuleType.MULTIPLIER -> ModuleResult(
                marked.copy(
                    speedCellsPerSecond = max(1.2, marked.speedCellsPerSecond * 0.78),
                    flow = marked.flow * 0.72,
                    value = marked.value * 2.0,
                ),
            )

            ModuleType.SPLITTER -> {
                val primaryDirection = module.rotation
                val secondaryDirection = module.rotation.rotateClockwise()
                val branchFlow = marked.flow * 0.56
                val primary = marked.copy(
                    direction = primaryDirection,
                    flow = branchFlow,
                )
                val secondary = marked.copy(
                    id = nextParticleId++,
                    direction = secondaryDirection,
                    progress = 0.0,
                    flow = branchFlow,
                )
                ModuleResult(primary, spawned = listOf(secondary))
            }
        }
    }

    private fun calculateMetrics(): MachineMetrics {
        val seconds = 1.0
        val deliveredFlow = deliveries.sumOf { it.flow } / seconds
        val output = deliveries.sumOf { it.output } / seconds
        val averageValue = if (deliveredFlow > 0.0) output / deliveredFlow else 0.0
        val wasted = wastes.sumOf { it.flow } / seconds

        return MachineMetrics(
            flowPerSecond = deliveredFlow,
            averageValue = averageValue,
            outputPerSecond = output,
            wastedPerSecond = wasted,
            powerUsed = powerUsed(),
            powerCapacity = powerCapacity,
            activeParticles = particles.size,
        )
    }

    private fun pruneRollingWindows() {
        val oldestTick = tick - ticksPerSecond
        while (deliveries.isNotEmpty() && deliveries.first().tick < oldestTick) deliveries.removeFirst()
        while (wastes.isNotEmpty() && wastes.first().tick < oldestTick) wastes.removeFirst()
    }

    private fun isInside(cell: Cell): Boolean =
        cell.col in 0 until columns && cell.row in 0 until rows
}
