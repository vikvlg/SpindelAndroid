package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.designsystem.UiText

data class RawDataState(
    val metrics: MetricsState = MetricsState(),
    val brewPicker: BrewPickerState = BrewPickerState(),
    val brewSelector: BrewSelectorState = BrewSelection.NotSelected.toSelectorState(),
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

// THINKING: Подходящее ли место для подобной функции?
fun BrewSelection.toSelectorState(): BrewSelectorState {
    val (title, subtitle, isPlaceholder) = when (this) {
        is BrewSelection.NotSelected -> Triple(UiText.StringResource(R.string.rawdata_brew_not_selected), null, true)
        is BrewSelection.Unassigned -> Triple(UiText.StringResource(R.string.rawdata_brew_unassigned), null, false)
        is BrewSelection.Brew -> Triple(UiText.DynamicString(brew.name), brew.dateText, false)
    }

    return BrewSelectorState(title, subtitle, isPlaceholder)
}
