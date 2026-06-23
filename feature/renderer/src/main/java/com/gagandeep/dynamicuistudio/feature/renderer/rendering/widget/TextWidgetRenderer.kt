package com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextStyleToken
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.RenderContext
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.WidgetRenderer

class TextWidgetRenderer : WidgetRenderer {
    override val type: String = TextWidget.TYPE

    @Composable
    override fun Render(
        widget: UiWidget,
        context: RenderContext
    ) {
        val textWidget = widget as TextWidget
        Text(
            text = textWidget.text,
            style = when (textWidget.style) {
                TextStyleToken.Headline -> MaterialTheme.typography.headlineMedium
                TextStyleToken.Title -> MaterialTheme.typography.titleLarge
                TextStyleToken.Body -> MaterialTheme.typography.bodyLarge
                TextStyleToken.Caption -> MaterialTheme.typography.labelLarge
            }
        )
    }
}
