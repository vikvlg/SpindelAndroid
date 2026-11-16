package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.metrics.api.Metric

data class RawDataState(
    val metrics: MetricsState = MetricsState(),
    val brewPicker: BrewPickerState = BrewPickerState(),
)

data class MetricsState(
    /** Флаг загрузки списка метрик. **/
    val isLoading: Boolean = false,
    /** Список метрик. **/
    val list: List<Metric> = emptyList(),
    /** Список идентификаторов выделенных строк. **/
    val selected: Set<Long> = emptySet(),
)

data class BrewPickerState(
    /** Список варок, доступных в селекторе. **/
    val brews: List<BrewUi> = emptyList(),
    /** Текущий вариант селектора варок. **/
    val selection: BrewSelection = BrewSelection.NotSelected,
    /** Выбранная варка. **/
    val selectedBrew: BrewUi? = null,
    /** Количество метрик без привязки к конкретной варке. **/
    val unassignedCount: Int = 0,
)
