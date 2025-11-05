package com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ListWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.RenderContext
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.WidgetRenderer

class ListWidgetRenderer : WidgetRenderer {
    override val type: String = ListWidget.TYPE

    @Composable
    override fun Render(
        widget: UiWidget,
        context: RenderContext
    ) {
        val listWidget = widget as ListWidget
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listWidget.items.forEach { item ->
                Text(
                    text = "- $item",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
