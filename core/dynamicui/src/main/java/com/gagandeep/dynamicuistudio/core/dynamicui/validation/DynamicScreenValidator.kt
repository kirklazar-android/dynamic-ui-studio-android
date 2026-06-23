package com.gagandeep.dynamicuistudio.core.dynamicui.validation

import com.gagandeep.dynamicuistudio.core.dynamicui.model.ButtonWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.CardWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ListWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.NavigateAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.OpenUrlAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TrackAnalyticsAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UnknownAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UnknownWidget

class DynamicScreenValidator {
    fun validate(schema: DynamicScreenSchema): ValidationResult {
        val errors = buildList {
            if (schema.screenId.isBlank()) add(ValidationError("screenId", "screenId must not be blank"))
            if (schema.title.isBlank()) add(ValidationError("title", "title must not be blank"))
            if (schema.version != DynamicScreenSchema.SUPPORTED_SCHEMA_VERSION) {
                add(ValidationError("version", "Unsupported schema version: ${schema.version}"))
            }
            if (schema.layout.widgets.isEmpty()) add(ValidationError("layout.widgets", "layout must contain widgets"))

            val seenIds = mutableSetOf<String>()
            schema.layout.widgets.forEachIndexed { index, widget ->
                validateWidget(
                    widget = widget,
                    path = "layout.widgets[$index]",
                    seenIds = seenIds
                )
            }
        }
        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    private fun MutableList<ValidationError>.validateWidget(
        widget: UiWidget,
        path: String,
        seenIds: MutableSet<String>
    ) {
        if (widget.id.isBlank()) add(ValidationError("$path.id", "widget id must not be blank"))
        if (!seenIds.add(widget.id)) add(ValidationError("$path.id", "Duplicate widget id: ${widget.id}"))

        when (widget) {
            is TextWidget -> if (widget.text.isBlank()) {
                add(ValidationError("$path.text", "text widget requires text"))
            }
            is ButtonWidget -> {
                if (widget.text.isBlank()) add(ValidationError("$path.text", "button widget requires text"))
                widget.action?.let { action ->
                    when (action) {
                        is NavigateAction -> if (action.destination.isBlank()) {
                            add(ValidationError("$path.action.destination", "navigate action requires destination"))
                        }
                        is OpenUrlAction -> if (action.url.isBlank()) {
                            add(ValidationError("$path.action.url", "open_url action requires url"))
                        }
                        is ShowSnackbarAction -> if (action.message.isBlank()) {
                            add(ValidationError("$path.action.message", "show_snackbar action requires message"))
                        }
                        is TrackAnalyticsAction -> if (action.eventName.isBlank()) {
                            add(ValidationError("$path.action.eventName", "track_analytics action requires eventName"))
                        }
                        is UnknownAction -> add(ValidationError("$path.action.type", action.reason))
                    }
                }
            }
            is CardWidget -> {
                if (widget.children.isEmpty()) add(ValidationError("$path.children", "card widget requires children"))
                widget.children.forEachIndexed { index, child ->
                    validateWidget(child, "$path.children[$index]", seenIds)
                }
            }
            is ListWidget -> if (widget.items.isEmpty()) {
                add(ValidationError("$path.items", "list widget requires at least one item"))
            }
            is UnknownWidget -> add(ValidationError("$path.type", widget.reason))
            else -> Unit
        }
    }
}

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val errors: List<ValidationError>) : ValidationResult
}

data class ValidationError(
    val path: String,
    val message: String
)
