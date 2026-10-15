package ru.vik.spindel

import androidx.navigation.NavController
import ru.vik.spindel.core.navigation.AppNavigator
import ru.vik.spindel.core.navigation.SharedRoutes

class ComposeAppNavigator(
    private val navController: NavController
) : AppNavigator {

    override fun navigateTo(destination: SharedRoutes) {
        navController.navigate(destination)
    }

    override fun navigateBack() {
        navController.popBackStack()
    }
}