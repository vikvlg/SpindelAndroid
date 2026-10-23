package ru.vik.spindel.feature.brewing.list

import ru.vik.spindel.core.brews.api.Brew

/** Единое UI-состояние экрана со списком варок. **/
data class BrewListState(
    /** Флаг первой загрузки данных. **/
    val isFirstLoading: Boolean = true,
    /** Флаг подгрузки данных. **/
    val isLoading: Boolean = false,
    /** Список варок. **/
    val brewList: List<Brew> = emptyList(),
)