package com.gagandeep.dynamicuistudio.core.dynamicui.model

sealed interface DynamicAction {
    val type: String
}

data class NavigateAction(
    val destination: String,
    override val type: String = TYPE
) : DynamicAction {
    companion object {
        const val TYPE = "navigate"
    }
}

data class OpenUrlAction(
    val url: String,
    override val type: String = TYPE
) : DynamicAction {
    companion object {
        const val TYPE = "open_url"
    }
}

data class ShowSnackbarAction(
    val message: String,
    override val type: String = TYPE
) : DynamicAction {
    companion object {
        const val TYPE = "show_snackbar"
    }
}

data class TrackAnalyticsAction(
    val eventName: String,
    override val type: String = TYPE
) : DynamicAction {
    companion object {
        const val TYPE = "track_analytics"
    }
}

data class UnknownAction(
    override val type: String,
    val reason: String = "Unsupported action type"
) : DynamicAction
