package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

sealed interface AddHabbitAction {
    data class OnTitleChange(val title: String) : AddHabbitAction
    data class OnCategoryChange(val category: String) : AddHabbitAction
    data class OnFrequencyChange(val frequency: String) : AddHabbitAction
    data class OnDescriptionChange(val description: String) : AddHabbitAction
    data class OnReminderToggle(val isEnabled: Boolean) : AddHabbitAction
    object OnSaveClick : AddHabbitAction
    object OnCustomFrequencyClick : AddHabbitAction
    object OnCustomFrequencyDialogDismiss : AddHabbitAction
    data class OnCustomFrequencyDaySelected(val day: String) : AddHabbitAction
}
