package ru.vik.spindel.feature.brewing

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

    @Serializable data class Details(val batchId: Int) : BrewingRoutes

    @Serializable data class Chart(val batchId: Int) : BrewingRoutes
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
                navController.navigate(BrewingRoutes.Details(batchId))
            })
        }

        composable<BrewingRoutes.Details> { backStackEntry ->
            val details = backStackEntry.toRoute<BrewingRoutes.Details>()
            BrewDetailsScreen(
                batchId = details.batchId,
                onShowChart = {
                    navController.navigate(BrewingRoutes.Chart(details.batchId))
                }
            )
        }

        composable<BrewingRoutes.Chart> { backStackEntry ->
            val details = backStackEntry.toRoute<BrewingRoutes.Chart>()
            BrewingChartScreen( batchId = details.batchId )
        }

    }
}
