package az.simplesoft.efficientmachine.simulation

data class Cell(val col: Int, val row: Int) {
    fun neighbor(direction: Direction): Cell = Cell(col + direction.dx, row + direction.dy)
}

enum class Direction(val dx: Int, val dy: Int) {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    fun rotateClockwise(): Direction = entries[(ordinal + 1) % entries.size]
    fun rotateCounterClockwise(): Direction = entries[(ordinal + entries.size - 1) % entries.size]
}

enum class ModuleType(
    val displayName: String,
    val symbol: String,
    val powerCost: Int,
) {
    SOURCE("Source", "S", 0),
    RECEIVER("Receiver", "R", 0),
    TURN("Turn", "↳", 1),
    BOOST("Boost", ">>", 5),
    MULTIPLIER("×2", "×2", 8),
    SPLITTER("Split", "Y", 7),
}

data class PlacedModule(
    val id: Long,
    val type: ModuleType,
    val cell: Cell,
    val rotation: Direction = Direction.EAST,
)

data class Particle(
    val id: Long,
    val cell: Cell,
    val direction: Direction,
    val progress: Double,
    val speedCellsPerSecond: Double,
    val flow: Double,
    val value: Double,
    val processedModuleIds: Set<Long> = emptySet(),
)

data class ParticleSnapshot(
    val id: Long,
    val cell: Cell,
    val direction: Direction,
    val progress: Double,
    val flow: Double,
    val value: Double,
)

data class MachineMetrics(
    val flowPerSecond: Double = 0.0,
    val averageValue: Double = 0.0,
    val outputPerSecond: Double = 0.0,
    val wastedPerSecond: Double = 0.0,
    val powerUsed: Int = 0,
    val powerCapacity: Int = 28,
    val activeParticles: Int = 0,
)

data class MachineSnapshot(
    val tick: Long,
    val columns: Int,
    val rows: Int,
    val modules: List<PlacedModule>,
    val particles: List<ParticleSnapshot>,
    val metrics: MachineMetrics,
)

data class MachineLayout(
    val modules: List<PlacedModule>,
)

sealed interface MachineCommand {
    data class Place(
        val type: ModuleType,
        val cell: Cell,
        val rotation: Direction = Direction.EAST,
    ) : MachineCommand

    data class Rotate(val cell: Cell) : MachineCommand
    data class Remove(val cell: Cell) : MachineCommand
    data object Reset : MachineCommand
}
