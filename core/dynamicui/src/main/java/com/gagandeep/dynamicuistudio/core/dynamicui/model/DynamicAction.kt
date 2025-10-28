package com.gagandeep.dynamicuistudio.core.dynamicui.model

sealed interface DynamicAction {
    val type: String
}

data class NavigateAction(
    val destination: String,
    override val type: String = "navigate"
) : DynamicAction

data class OpenUrlAction(
    val url: String,
    override val type: String = "open_url"
) : DynamicAction

data class SnackbarAction(
    val message: String,
    override val type: String = "snackbar"
) : DynamicAction

data class UnknownAction(
    override val type: String
) : DynamicAction
