package com.gagandeep.dynamicuistudio.feature.renderer

import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.ValidationError

sealed interface RendererUiState {
    data object Loading : RendererUiState

    data class Success(
        val schema: DynamicScreenSchema,
        val actionLogs: List<String> = emptyList()
    ) : RendererUiState

    data class Error(
        val message: String,
        val validationErrors: List<ValidationError> = emptyList()
    ) : RendererUiState
}
