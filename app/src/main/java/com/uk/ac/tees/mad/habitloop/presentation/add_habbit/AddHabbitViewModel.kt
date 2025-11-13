package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AddHabbitViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddHabbitState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddHabbitState()
        )

    fun onAction(action: AddHabbitAction) {
        when (action) {
            is AddHabbitAction.OnTitleChange -> _state.update { it.copy(habitTitle = action.title) }
            is AddHabbitAction.OnDescriptionChange -> _state.update { it.copy(description = action.description) }
            is AddHabbitAction.OnCategoryChange -> _state.update { it.copy(selectedCategory = action.category) }
            is AddHabbitAction.OnFrequencyChange -> _state.update { it.copy(selectedFrequency = action.frequency) }
            is AddHabbitAction.OnReminderToggle -> _state.update { it.copy(isReminderEnabled = action.isEnabled) }
            AddHabbitAction.OnSaveClick -> {
                // TODO: Implement save logic
            }
        }
    }
}
