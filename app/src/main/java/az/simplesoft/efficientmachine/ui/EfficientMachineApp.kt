package az.simplesoft.efficientmachine.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import az.simplesoft.efficientmachine.simulation.Cell
import az.simplesoft.efficientmachine.simulation.MachineMetrics
import az.simplesoft.efficientmachine.simulation.MachineSnapshot
import az.simplesoft.efficientmachine.simulation.ModuleType
import az.simplesoft.efficientmachine.simulation.PlacedModule
import java.util.Locale
import kotlin.math.min

@Composable
fun EfficientMachineApp(viewModel: MachineViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Header(state)
            MetricsRow(state.snapshot.metrics)
            ContractCard(state)

            MachineBoard(
                snapshot = state.snapshot,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onCellTap = viewModel::onCellTapped,
            )

            ModuleTray(
                selected = state.selectedModule,
                editMode = state.editMode,
                onSelect = viewModel::selectModule,
            )

            Controls(
                removeMode = state.editMode == EditMode.REMOVE,
                onRemove = viewModel::toggleRemoveMode,
                onUndo = viewModel::undo,
                onReset = viewModel::reset,
            )

            Text(
                text = state.message ?: "Pick a module → tap empty cell. Tap a placed module to rotate.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = if (state.message == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.tertiary,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun Header(state: MachineUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("EFFICIENT MACHINE", fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
            Text("ANDROID CORE ALPHA", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
        }
        Text(
            text = "TICK ${state.snapshot.tick}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun MetricsRow(metrics: MachineMetrics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        MetricCard("FLOW", format(metrics.flowPerSecond), Modifier.weight(1f))
        MetricCard("VALUE", format(metrics.averageValue), Modifier.weight(1f))
        MetricCard("OUTPUT", format(metrics.outputPerSecond), Modifier.weight(1f))
        MetricCard("POWER", "${metrics.powerUsed}/${metrics.powerCapacity}", Modifier.weight(1f))
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 7.dp)) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
        }
    }
}

@Composable
private fun ContractCard(state: MachineUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("CONTRACT #${state.contract.id}  ${state.contract.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${(state.contractProgress * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
            }
            Text(state.contract.description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            LinearProgressIndicator(
                progress = { state.contractProgress.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ModuleTray(
    selected: ModuleType,
    editMode: EditMode,
    onSelect: (ModuleType) -> Unit,
) {
    val modules = listOf(ModuleType.TURN, ModuleType.BOOST, ModuleType.MULTIPLIER, ModuleType.SPLITTER)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        modules.forEach { type ->
            val active = editMode == EditMode.PLACE && selected == type
            if (active) {
                Button(onClick = { onSelect(type) }, contentPadding = ButtonDefaults.ContentPadding) {
                    Text("${type.symbol}  ${type.displayName}  ⚡${type.powerCost}", fontSize = 11.sp)
                }
            } else {
                OutlinedButton(onClick = { onSelect(type) }) {
                    Text("${type.symbol}  ${type.displayName}  ⚡${type.powerCost}", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun Controls(
    removeMode: Boolean,
    onRemove: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(onClick = onUndo, modifier = Modifier.weight(1f)) { Text("UNDO") }
        if (removeMode) {
            Button(
                onClick = onRemove,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) { Text("DELETE ON") }
        } else {
            OutlinedButton(onClick = onRemove, modifier = Modifier.weight(1f)) { Text("DELETE") }
        }
        OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) { Text("RESET") }
    }
}

@Composable
private fun MachineBoard(
    snapshot: MachineSnapshot,
    modifier: Modifier,
    onCellTap: (Cell) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF09151E),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(snapshot.columns.toFloat() / snapshot.rows.toFloat())
                    .border(1.dp, Color(0xFF173141), RoundedCornerShape(14.dp))
                    .semantics { contentDescription = "Machine grid" }
                    .pointerInput(snapshot.columns, snapshot.rows) {
                        detectTapGestures { offset ->
                            val cellW = size.width.toFloat() / snapshot.columns
                            val cellH = size.height.toFloat() / snapshot.rows
                            val col = (offset.x / cellW).toInt().coerceIn(0, snapshot.columns - 1)
                            val row = (offset.y / cellH).toInt().coerceIn(0, snapshot.rows - 1)
                            onCellTap(Cell(col, row))
                        }
                    },
            ) {
                val cellW = size.width / snapshot.columns
                val cellH = size.height / snapshot.rows

                for (c in 0..snapshot.columns) {
                    val x = c * cellW
                    drawLine(Color(0xFF18303E), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                }
                for (r in 0..snapshot.rows) {
                    val y = r * cellH
                    drawLine(Color(0xFF18303E), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                }

                snapshot.modules.forEach { drawModule(it, cellW, cellH) }

                snapshot.particles.forEach { p ->
                    val center = Offset(
                        x = (p.cell.col + 0.5f) * cellW + p.direction.dx * p.progress.toFloat() * cellW,
                        y = (p.cell.row + 0.5f) * cellH + p.direction.dy * p.progress.toFloat() * cellH,
                    )
                    if (center.x in 0f..size.width && center.y in 0f..size.height) {
                        val radius = min(cellW, cellH) * (0.07f + (p.value.coerceAtMost(5.0).toFloat() * 0.008f))
                        drawCircle(Color(0x3379F2D0), radius * 2.2f, center)
                        drawCircle(Color(0xFF79F2D0), radius, center)
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawModule(
    module: PlacedModule,
    cellW: Float,
    cellH: Float,
) {
    val left = module.cell.col * cellW
    val top = module.cell.row * cellH
    val inset = min(cellW, cellH) * 0.12f
    val rectTopLeft = Offset(left + inset, top + inset)
    val rectSize = Size(cellW - inset * 2, cellH - inset * 2)

    val color = when (module.type) {
        ModuleType.SOURCE -> Color(0xFF79F2D0)
        ModuleType.RECEIVER -> Color(0xFFFFD66B)
        ModuleType.TURN -> Color(0xFF7DC7FF)
        ModuleType.BOOST -> Color(0xFF65E6A5)
        ModuleType.MULTIPLIER -> Color(0xFFC69CFF)
        ModuleType.SPLITTER -> Color(0xFFFF9F7D)
    }

    drawRoundRect(
        color = color.copy(alpha = 0.15f),
        topLeft = rectTopLeft,
        size = rectSize,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(inset),
    )
    drawRoundRect(
        color = color,
        topLeft = rectTopLeft,
        size = rectSize,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(inset),
        style = Stroke(width = 2.2f),
    )

    val center = Offset((module.cell.col + 0.5f) * cellW, (module.cell.row + 0.5f) * cellH)
    val arrowLength = min(cellW, cellH) * 0.28f
    val dir = Offset(module.rotation.dx.toFloat(), module.rotation.dy.toFloat())
    val arrowEnd = center + dir * arrowLength
    drawLine(color, center, arrowEnd, strokeWidth = 3f, cap = StrokeCap.Round)
    drawCircle(color, 3.5f, arrowEnd)

    drawContext.canvas.nativeCanvas.drawText(
        module.type.symbol,
        center.x,
        center.y + min(cellW, cellH) * 0.08f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = min(cellW, cellH) * 0.24f
            typeface = Typeface.DEFAULT_BOLD
        },
    )
}

private fun format(value: Double): String = when {
    value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
    value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000)
    else -> String.format(Locale.US, "%.2f", value)
}
