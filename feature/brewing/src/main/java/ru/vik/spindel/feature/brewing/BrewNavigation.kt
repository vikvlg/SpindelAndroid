package ru.vik.spindel.feature.brewing

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.vik.spindel.core.navigation.SharedRoutes
import ru.vik.spindel.feature.brewing.chart.BrewingChartScreen
import ru.vik.spindel.feature.brewing.details.BrewDetailsScreen
import ru.vik.spindel.feature.brewing.list.BrewListScreen


sealed interface BrewingRoutes {
    @Serializable object List : BrewingRoutes

    @Serializable data class Details(val batchId: Long) : BrewingRoutes

    @Serializable data class Chart(val batchId: Long) : BrewingRoutes
}

///**
// * Extension для NavGraphBuilder — позволяет добавлять навигацию
// * из любого места графа.
// */
//fun NavGraphBuilder.brewListScreen(
//    onBrewClick: (Long) -> Unit
//) {
//    composable(BrewRoutes.BREW_LIST) {
//        BrewListScreen()
//    }
//}

// Находится внутри модуля :features:brewing
fun NavGraphBuilder.brewingGraph(navController: NavController) {
    // navigation объединяет экраны в изолированную группу
    navigation<SharedRoutes.BrewingRoot>(
        startDestination = BrewingRoutes.List::class
    ) {
        composable<BrewingRoutes.List> {
            BrewListScreen(onSubItemClick = { batchId ->
                navController.navigateSafe(BrewingRoutes.Details(batchId))
            })
        }

        composable<BrewingRoutes.Details> { backStackEntry ->
            val details = backStackEntry.toRoute<BrewingRoutes.Details>()
            BrewDetailsScreen(
                brewId = details.batchId,
                onShowChart = {
                    navController.navigateSafe(BrewingRoutes.Chart(details.batchId))
                }
            )
        }

        composable<BrewingRoutes.Chart> { backStackEntry ->
            val details = backStackEntry.toRoute<BrewingRoutes.Chart>()
            BrewingChartScreen( batchId = details.batchId )
        }

    }
}

fun NavController.navigateSafe(route: Any) {
    // Проверяем, что текущий экран полностью активен (RESUMED)
    // Если статус STARTED или другой — значит, мы уже куда-то переходим
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route)
    }
}