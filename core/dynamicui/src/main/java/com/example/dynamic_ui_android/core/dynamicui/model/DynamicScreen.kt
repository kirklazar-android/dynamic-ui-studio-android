package com.example.dynamic_ui_android.core.dynamicui.model

import kotlinx.serialization.Serializable

@Serializable
data class DynamicScreen(
    val screenId: String,
    val version: Int = 1,
    val metadata: ScreenMetadata? = null,
    val components: List<DynamicComponent>
)

@Serializable
data class ScreenMetadata(
    val analyticsScreenName: String? = null,
    val experimentId: String? = null
)
