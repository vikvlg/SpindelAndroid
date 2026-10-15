package ru.vik.spindel.feature.brewing

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Навигационные роуты для feature:brews.
 *
 * Каждый роут — константа, которую используют другие модули
 * для навигации.
 */
object BrewRoutes {
    const val BREW_LIST = "brew_list"
    const val BREW_DETAIL = "brew_detail/{brewId}"
    const val BREW_EDIT = "brew_edit/{brewId}"

//    val BREW_LIST_DEEP_LINK = ru.vik.spindel.core.navigation.NavRoute.DeepLink(
//        uriPattern = "spindel://brews/list"
//    )
}

/**
 * Extension для NavGraphBuilder — позволяет добавлять навигацию
 * из любого места графа.
 */
fun NavGraphBuilder.brewListScreen(
    onBrewClick: (Long) -> Unit
) {
//    composable(BrewRoutes.BREW_LIST) {
//        BrewListScreen()
//    }
}
