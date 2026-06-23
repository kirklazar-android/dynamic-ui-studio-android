package com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gagandeep.dynamicuistudio.core.dynamicui.model.SpacerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.RenderContext
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.WidgetRenderer

class SpacerWidgetRenderer : WidgetRenderer {
    override val type: String = SpacerWidget.TYPE

    @Composable
    override fun Render(
        widget: UiWidget,
        context: RenderContext
    ) {
        val spacerWidget = widget as SpacerWidget
        Spacer(Modifier.height(spacerWidget.height.dp))
    }
}
