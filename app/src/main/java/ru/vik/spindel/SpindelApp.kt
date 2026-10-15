package ru.vik.spindel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.vik.spindel.core.navigation.BottomTab
import ru.vik.spindel.feature.brewing.BrewListScreen
import ru.vik.spindel.feature.devices.DeviceListScreen

@Composable
fun SpindelApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    // Список вкладок, которые мы хотим показать в меню
    val tabs = listOf(
        BottomTab.BrewingTab,
        BottomTab.DevicesTab,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Получаем текущий открытый маршрут для подсветки активной вкладки
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = currentRoute == tab.route,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    // Очищаем стек до стартового экрана, чтобы избежать накопления вкладок
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Избегаем повторного создания одного и того же экрана при множественном клике
                                    launchSingleTop = true
                                    // Восстанавливаем состояние экрана (скролл, ввод данных) при возврате на вкладку
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Наш NavHost управляет переключением фич внутри области контента
        NavHost(
            navController = navController,
            startDestination = BottomTab.BrewingTab.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.BrewingTab.route) {
                BrewListScreen("brewing")
            }
            composable(BottomTab.DevicesTab.route) {
                DeviceListScreen("devices")
            }
        }
    }
}