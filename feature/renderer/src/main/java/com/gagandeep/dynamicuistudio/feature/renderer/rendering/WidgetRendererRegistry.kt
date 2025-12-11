package com.gagandeep.dynamicuistudio.feature.renderer.rendering

import androidx.compose.runtime.Composable
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.ButtonWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.CardWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.DividerWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.HeroBannerWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.ListWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.SpacerWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.TextWidgetRenderer
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget.UnknownWidgetRenderer

class WidgetRendererRegistry(
    renderers: List<WidgetRenderer>,
    private val fallbackRenderer: WidgetRenderer = UnknownWidgetRenderer()
) {
    private val renderersByType = renderers.associateBy { it.type }

    @Composable
    fun RenderWidget(
        widget: UiWidget,
        context: RenderContext
    ) {
        val renderer = renderersByType[widget.type] ?: fallbackRenderer
        renderer.Render(widget, context)
    }
}

fun defaultWidgetRendererRegistry(): WidgetRendererRegistry {
    val fallbackRenderer = UnknownWidgetRenderer()
    lateinit var registry: WidgetRendererRegistry
    registry = WidgetRendererRegistry(
        renderers = listOf(
            TextWidgetRenderer(),
            ButtonWidgetRenderer(),
            HeroBannerWidgetRenderer(),
            CardWidgetRenderer { widget, context -> registry.RenderWidget(widget, context) },
            ListWidgetRenderer(),
            SpacerWidgetRenderer(),
            DividerWidgetRenderer()
        ),
        fallbackRenderer = fallbackRenderer
    )
    return registry
}
