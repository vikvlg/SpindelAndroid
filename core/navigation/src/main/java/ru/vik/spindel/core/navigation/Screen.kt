package ru.vik.spindel.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PropaneTank
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface Screen {
    object Brewing : Screen
    object Devices : Screen

//    data class Brews(val list: List<String>) : Screen
//    data class Chart(val list: List<String>) : Screen
}

// Описание вкладок нижнего меню
sealed class BottomTab(
    val screen: Screen,
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object BrewingTab : BottomTab(Screen.Brewing, "brew", "Варки", Icons.Filled.PropaneTank)
    object DevicesTab : BottomTab(Screen.Devices, "device", "Устройства", Icons.Filled.Devices)
}

// Интерфейс для управления навигацией, который можно внедрять в ViewModel
interface AppNavigator {
    fun navigateTo(screen: Screen)
    fun navigateBack()
}