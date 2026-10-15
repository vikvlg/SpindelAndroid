package ru.vik.spindel.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PropaneTank
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Публичные маршруты, доступные любому модулю
sealed interface SharedRoutes {

    // Вход в фичу пивоварения (только корень)
    @Serializable
    object BrewingRoot : SharedRoutes

    @Serializable
    object DeviceRoot : SharedRoutes
}

// Описание вкладок нижнего меню
sealed class BottomTab(
    val route: Any,
    val title: String,
    val icon: ImageVector
) {
    object BrewingTab : BottomTab(SharedRoutes.BrewingRoot, "Варки", Icons.Filled.PropaneTank)
    object DevicesTab : BottomTab(SharedRoutes.DeviceRoot, "Устройства", Icons.Filled.Devices)
}

// Интерфейс для управления навигацией, который можно внедрять в ViewModel
interface AppNavigator {
    fun navigateTo(destination: SharedRoutes)
    fun navigateBack()
}