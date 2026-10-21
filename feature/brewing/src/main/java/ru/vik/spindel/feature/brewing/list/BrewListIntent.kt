package ru.vik.spindel.feature.brewing.list

/** Намерения пользователя при работе со списокм варок. **/
sealed interface BrewListIntent {
    /** Обновление списка варок. **/
    object RefreshClicked : BrewListIntent
    //data class UpdateBio(val newBio: String) : ListBrewIntent
}