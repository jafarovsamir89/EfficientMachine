package az.simplesoft.efficientmachine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.simplesoft.efficientmachine.simulation.AlphaContracts
import az.simplesoft.efficientmachine.simulation.Cell
import az.simplesoft.efficientmachine.simulation.Contract
import az.simplesoft.efficientmachine.simulation.MachineCommand
import az.simplesoft.efficientmachine.simulation.MachineEngine
import az.simplesoft.efficientmachine.simulation.MachineSnapshot
import az.simplesoft.efficientmachine.simulation.ModuleType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class EditMode { PLACE, REMOVE }

data class MachineUiState(
    val snapshot: MachineSnapshot,
    val selectedModule: ModuleType = ModuleType.TURN,
    val editMode: EditMode = EditMode.PLACE,
    val contractIndex: Int = 0,
    val contract: Contract = AlphaContracts.all.first(),
    val contractProgress: Double = 0.0,
    val stableTicks: Int = 0,
    val message: String? = null,
)

class MachineViewModel : ViewModel() {
    private val engine = MachineEngine()
    private val _uiState = MutableStateFlow(MachineUiState(snapshot = engine.snapshot()))
    val uiState: StateFlow<MachineUiState> = _uiState.asStateFlow()

    private val requiredStableTicks = engine.ticksPerSecond
    private var messageTicks = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                val snapshot = engine.step()
                updateContract(snapshot)
                if (messageTicks > 0) {
                    messageTicks--
                    if (messageTicks == 0) _uiState.value = _uiState.value.copy(message = null)
                }
                delay(1_000L / engine.ticksPerSecond)
            }
        }
    }

    fun selectModule(type: ModuleType) {
        if (type == ModuleType.SOURCE || type == ModuleType.RECEIVER) return
        _uiState.value = _uiState.value.copy(
            selectedModule = type,
            editMode = EditMode.PLACE,
            message = null,
        )
    }

    fun toggleRemoveMode() {
        _uiState.value = _uiState.value.copy(
            editMode = if (_uiState.value.editMode == EditMode.REMOVE) EditMode.PLACE else EditMode.REMOVE,
            message = null,
        )
    }

    fun onCellTapped(cell: Cell) {
        val module = engine.snapshot().modules.firstOrNull { it.cell == cell }
        val state = _uiState.value

        val changed = when {
            state.editMode == EditMode.REMOVE -> engine.applyNow(MachineCommand.Remove(cell))
            module != null -> engine.applyNow(MachineCommand.Rotate(cell))
            else -> engine.applyNow(MachineCommand.Place(state.selectedModule, cell))
        }

        if (!changed) {
            val message = when {
                module?.type == ModuleType.SOURCE || module?.type == ModuleType.RECEIVER -> "SOURCE and RECEIVER are fixed"
                state.editMode == EditMode.PLACE &&
                    engine.powerUsed() + state.selectedModule.powerCost > engine.powerCapacity -> "Not enough POWER"
                else -> "Can't edit this cell"
            }
            showMessage(message)
        } else {
            _uiState.value = _uiState.value.copy(snapshot = engine.snapshot(), stableTicks = 0)
        }
    }

    fun undo() {
        if (engine.undo()) {
            _uiState.value = _uiState.value.copy(snapshot = engine.snapshot(), stableTicks = 0)
        } else {
            showMessage("Nothing to undo")
        }
    }

    fun reset() {
        engine.applyNow(MachineCommand.Reset)
        _uiState.value = _uiState.value.copy(
            snapshot = engine.snapshot(),
            stableTicks = 0,
            editMode = EditMode.PLACE,
        )
    }

    private fun updateContract(snapshot: MachineSnapshot) {
        val old = _uiState.value
        val contract = old.contract
        val satisfied = contract.isSatisfied(snapshot.metrics, snapshot.modules)
        val stable = if (satisfied) old.stableTicks + 1 else 0

        if (stable >= requiredStableTicks) {
            val nextIndex = (old.contractIndex + 1).coerceAtMost(AlphaContracts.all.lastIndex)
            val completedLast = old.contractIndex == AlphaContracts.all.lastIndex
            _uiState.value = old.copy(
                snapshot = snapshot,
                contractIndex = nextIndex,
                contract = AlphaContracts.all[nextIndex],
                contractProgress = if (completedLast) 1.0 else 0.0,
                stableTicks = 0,
                message = if (completedLast) "ALPHA MASTER — keep optimizing" else "CONTRACT COMPLETE → #${nextIndex + 1}",
            )
            messageTicks = engine.ticksPerSecond * 2
            return
        }

        _uiState.value = old.copy(
            snapshot = snapshot,
            contractProgress = contract.progress(snapshot.metrics),
            stableTicks = stable,
        )
    }

    private fun showMessage(text: String) {
        _uiState.value = _uiState.value.copy(message = text)
        messageTicks = engine.ticksPerSecond * 2
    }
}
