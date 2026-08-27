package az.simplesoft.efficientmachine.simulation

import org.junit.Assert.assertTrue
import org.junit.Test

class AlphaContractsTest {
    @Test
    fun everyHandcraftedAlphaContractHasAValidSolution() {
        val recipes = listOf<(MachineEngine) -> Unit>(
            { e -> place(e, ModuleType.MULTIPLIER, 3) },
            { e -> place(e, ModuleType.BOOST, 2); place(e, ModuleType.MULTIPLIER, 4) },
            { e -> place(e, ModuleType.BOOST, 2); place(e, ModuleType.BOOST, 4) },
            { e -> place(e, ModuleType.BOOST, 2); place(e, ModuleType.MULTIPLIER, 4) },
            { e ->
                place(e, ModuleType.BOOST, 1)
                place(e, ModuleType.MULTIPLIER, 2)
                place(e, ModuleType.SPLITTER, 3, direction = Direction.EAST)
                place(e, ModuleType.TURN, 3, row = 6, direction = Direction.EAST)
                place(e, ModuleType.TURN, 6, row = 6, direction = Direction.NORTH)
                place(e, ModuleType.TURN, 6, row = 5, direction = Direction.EAST)
            },
            { e -> place(e, ModuleType.MULTIPLIER, 3) },
            { e -> place(e, ModuleType.MULTIPLIER, 2); place(e, ModuleType.MULTIPLIER, 4) },
            { e -> place(e, ModuleType.BOOST, 2); place(e, ModuleType.BOOST, 4) },
            { e ->
                place(e, ModuleType.BOOST, 1)
                place(e, ModuleType.BOOST, 2)
                place(e, ModuleType.BOOST, 3)
                place(e, ModuleType.MULTIPLIER, 4)
            },
            { e ->
                place(e, ModuleType.MULTIPLIER, 1)
                place(e, ModuleType.MULTIPLIER, 3)
                place(e, ModuleType.MULTIPLIER, 5)
            },
        )

        AlphaContracts.all.forEachIndexed { index, contract ->
            val engine = MachineEngine()
            recipes[index](engine)
            repeat(300) { engine.step() }
            val snapshot = engine.snapshot()

            assertTrue(
                "Contract #${contract.id} has no valid regression solution; metrics=${snapshot.metrics}",
                contract.isSatisfied(snapshot.metrics, snapshot.modules),
            )
        }
    }

    private fun place(
        engine: MachineEngine,
        type: ModuleType,
        col: Int,
        row: Int = 5,
        direction: Direction = Direction.EAST,
    ) {
        check(engine.applyNow(MachineCommand.Place(type, Cell(col, row), direction)))
    }
}
