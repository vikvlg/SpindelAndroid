package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.designsystem.UiText

data class RawDataState(
    val metrics: MetricsState = MetricsState(),
    val brewPicker: BrewPickerState = BrewPickerState(),
    val brewSelector: BrewSelectorState = BrewSelectorState(),
)

data class MetricsState(
    /** Флаг загрузки списка метрик. **/
    val isLoading: Boolean = false,
    /** Список метрик. **/
    val list: List<MetricUi> = emptyList(),
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

data class BrewSelectorState(
    /** Основной заголовок селектора. **/
    val title: UiText = UiText.DynamicString(""),
    /** Подзаголовок селектора. **/
    val subtitle: String? = null,
    /** THINKING [2026.11.16]: Проверить необходимость поля и удалить, если в нем больше нет смысла. **/
    val isPlaceholder: Boolean = false,
)
