package az.simplesoft.efficientmachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import az.simplesoft.efficientmachine.ui.EfficientMachineApp
import az.simplesoft.efficientmachine.ui.theme.EfficientMachineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EfficientMachineTheme {
                EfficientMachineApp()
            }
        }
    }
}
