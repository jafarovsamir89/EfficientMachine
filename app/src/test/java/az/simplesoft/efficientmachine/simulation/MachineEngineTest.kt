package az.simplesoft.efficientmachine.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MachineEngineTest {
    @Test
    fun baselineMachineProducesTwoOutputPerSecond() {
        val engine = MachineEngine()
        runToSteadyState(engine)

        assertEquals(2.0, engine.snapshot().metrics.outputPerSecond, 0.0001)
    }

    @Test
    fun multiplierRaisesValueWhileReducingFlow() {
        val engine = MachineEngine()
        assertTrue(engine.applyNow(MachineCommand.Place(ModuleType.MULTIPLIER, Cell(3, 5))))
        runToSteadyState(engine)

        val metrics = engine.snapshot().metrics
        assertEquals(2.0, metrics.averageValue, 0.0001)
        assertEquals(1.44, metrics.flowPerSecond, 0.0001)
        assertEquals(2.88, metrics.outputPerSecond, 0.0001)
    }

    @Test
    fun powerLimitRejectsPlacementThatWouldExceedCapacity() {
        val engine = MachineEngine()
        assertTrue(engine.applyNow(MachineCommand.Place(ModuleType.MULTIPLIER, Cell(1, 5))))
        assertTrue(engine.applyNow(MachineCommand.Place(ModuleType.MULTIPLIER, Cell(2, 5))))
        assertTrue(engine.applyNow(MachineCommand.Place(ModuleType.MULTIPLIER, Cell(3, 5))))
        assertFalse(engine.applyNow(MachineCommand.Place(ModuleType.MULTIPLIER, Cell(4, 5))))
        assertEquals(24, engine.powerUsed())
    }

    @Test
    fun identicalCommandsProduceIdenticalSnapshots() {
        val first = MachineEngine()
        val second = MachineEngine()
        val commands = listOf(
            MachineCommand.Place(ModuleType.BOOST, Cell(2, 5)),
            MachineCommand.Place(ModuleType.MULTIPLIER, Cell(4, 5)),
            MachineCommand.Place(ModuleType.TURN, Cell(3, 4), Direction.SOUTH),
            MachineCommand.Rotate(Cell(3, 4)),
        )

        commands.forEach {
            assertEquals(first.applyNow(it), second.applyNow(it))
        }
        repeat(180) {
            first.step()
            second.step()
        }

        assertEquals(first.snapshot(), second.snapshot())
    }

    @Test
    fun undoRestoresPreviousLayout() {
        val engine = MachineEngine()
        val before = engine.currentLayout()
        assertTrue(engine.applyNow(MachineCommand.Place(ModuleType.BOOST, Cell(3, 5))))
        assertTrue(engine.undo())

        assertEquals(before, engine.currentLayout())
    }

    private fun runToSteadyState(engine: MachineEngine) {
        repeat(300) { engine.step() }
    }
}
