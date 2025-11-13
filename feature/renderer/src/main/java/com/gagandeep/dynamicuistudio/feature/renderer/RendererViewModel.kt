package com.gagandeep.dynamicuistudio.feature.renderer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.parser.DynamicJsonParser
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.DynamicScreenValidator
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.ValidationResult
import com.gagandeep.dynamicuistudio.feature.renderer.data.DynamicSchemaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RendererViewModel(
    private val schemaSource: DynamicSchemaSource,
    private val parser: DynamicJsonParser = DynamicJsonParser(),
    private val validator: DynamicScreenValidator = DynamicScreenValidator()
) : ViewModel() {
    private val _uiState = MutableStateFlow<RendererUiState>(RendererUiState.Loading)
    val uiState: StateFlow<RendererUiState> = _uiState.asStateFlow()

    init {
        loadScreen("home")
    }

    fun loadScreen(screenId: String) {
        viewModelScope.launch {
            _uiState.value = RendererUiState.Loading
            val rawSchema = schemaSource.loadSchema(screenId).getOrElse { error ->
                _uiState.value = RendererUiState.Error(
                    message = error.message ?: "Unable to load dynamic screen"
                )
                return@launch
            }
            val schema = parser.parse(rawSchema).getOrElse { error ->
                _uiState.value = RendererUiState.Error(
                    message = error.message ?: "Unable to parse dynamic screen"
                )
                return@launch
            }
            when (val validation = validator.validate(schema)) {
                ValidationResult.Valid -> _uiState.value = RendererUiState.Success(schema = schema)
                is ValidationResult.Invalid -> _uiState.value = RendererUiState.Error(
                    message = "Schema validation failed",
                    validationErrors = validation.errors
                )
            }
        }
    }

    fun recordAction(action: DynamicAction) {
        _uiState.update { current ->
            if (current is RendererUiState.Success) {
                current.copy(actionLogs = current.actionLogs + action.toLogMessage())
            } else {
                current
            }
        }
    }

    private fun DynamicAction.toLogMessage(): String = when (this) {
        is ShowSnackbarAction -> "show_snackbar: $message"
        else -> type
    }
}

class RendererViewModelFactory(
    private val schemaSource: DynamicSchemaSource
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(RendererViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return RendererViewModel(schemaSource) as T
    }
}
