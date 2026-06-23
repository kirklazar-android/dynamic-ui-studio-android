package com.gagandeep.dynamicuistudio.feature.renderer.rendering.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gagandeep.dynamicuistudio.core.dynamicui.model.HeroBannerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.RenderContext
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.WidgetRenderer

class HeroBannerWidgetRenderer : WidgetRenderer {
    override val type: String = HeroBannerWidget.TYPE

    @Composable
    override fun Render(
        widget: UiWidget,
        context: RenderContext
    ) {
        val heroWidget = widget as HeroBannerWidget
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = heroWidget.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                heroWidget.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                heroWidget.action?.let { action ->
                    Spacer(Modifier.height(4.dp))
                    Button(onClick = { context.onAction(action) }) {
                        Text(heroWidget.actionText)
                    }
                }
            }
        }
    }
}
