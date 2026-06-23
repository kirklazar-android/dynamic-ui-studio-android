package com.gagandeep.dynamicuistudio.feature.renderer.rendering

import androidx.compose.runtime.Composable
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget

interface WidgetRenderer {
    val type: String

    @Composable
    fun Render(
        widget: UiWidget,
        context: RenderContext
    )
}
