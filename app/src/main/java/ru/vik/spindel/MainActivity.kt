package ru.vik.spindel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.vik.spindel.ui.theme.SpindelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpindelTheme {
                    SpindelApp() // modifier = Modifier.padding(innerPadding)
                }
        }
    }
}
