package ru.vik.spindel

import androidx.navigation.NavController
import ru.vik.spindel.core.navigation.AppNavigator
import ru.vik.spindel.core.navigation.Screen
import ru.vik.spindel.feature.brewing.BrewRoutes
import ru.vik.spindel.feature.devices.DeviceRoutes

class ComposeAppNavigator(
    private val navController: NavController
) : AppNavigator {

    override fun navigateTo(screen: Screen) {
        when (screen) {
            is Screen.Brewing -> navController.navigate(BrewRoutes.BREW_LIST)
            is Screen.Devices -> navController.navigate(DeviceRoutes.DEVICE_LIST)
//            is Screen.Details -> navController.navigate("details/${screen.itemId}")
        }
    }

    override fun navigateBack() {
        navController.popBackStack()
    }
}