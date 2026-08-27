package az.simplesoft.efficientmachine.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF79F2D0),
    secondary = Color(0xFF7DC7FF),
    tertiary = Color(0xFFFFD66B),
    background = Color(0xFF071018),
    surface = Color(0xFF0D1721),
    surfaceVariant = Color(0xFF132432),
    onPrimary = Color(0xFF002019),
    onBackground = Color(0xFFEAF7FF),
    onSurface = Color(0xFFEAF7FF),
    onSurfaceVariant = Color(0xFF9DB1C0),
    error = Color(0xFFFF7E79),
)

@Composable
fun EfficientMachineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
