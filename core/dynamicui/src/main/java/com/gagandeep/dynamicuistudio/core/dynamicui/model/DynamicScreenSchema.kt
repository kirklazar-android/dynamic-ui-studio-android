package com.gagandeep.dynamicuistudio.core.dynamicui.model

data class DynamicScreenSchema(
    val screenId: String,
    val title: String,
    val version: Int = SUPPORTED_SCHEMA_VERSION,
    val analytics: AnalyticsSpec? = null,
    val layout: DynamicLayout
) {
    companion object {
        const val SUPPORTED_SCHEMA_VERSION = 1
    }
}

data class DynamicLayout(
    val type: LayoutType = LayoutType.Vertical,
    val padding: Int = 16,
    val widgets: List<UiWidget>
)

enum class LayoutType {
    Vertical
}

data class AnalyticsSpec(
    val screenView: String? = null,
    val click: String? = null
)
