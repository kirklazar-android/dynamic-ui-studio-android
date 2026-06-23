package com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DividerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.RenderContext
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.WidgetRenderer

class DividerWidgetRenderer : WidgetRenderer {
    override val type: String = DividerWidget.TYPE

    @Composable
    override fun Render(
        widget: UiWidget,
        context: RenderContext
    ) {
        HorizontalDivider()
    }
}
