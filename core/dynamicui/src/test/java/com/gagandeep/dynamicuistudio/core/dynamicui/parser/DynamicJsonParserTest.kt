package com.gagandeep.dynamicuistudio.core.dynamicui.parser

import com.gagandeep.dynamicuistudio.core.dynamicui.model.ButtonWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.CardWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextStyleToken
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicJsonParserTest {
    private val parser = DynamicJsonParser()

    @Test
    fun parse_mapsSchemaLayoutAndNestedWidgets() {
        val schema = parser.parse(
            """
            {
              "screenId": "home",
              "title": "Home",
              "version": 1,
              "analytics": { "screenView": "home_viewed" },
              "layout": {
                "type": "vertical",
                "padding": 24,
                "widgets": [
                  {
                    "id": "title",
                    "type": "text",
                    "text": "Welcome",
                    "style": "headline"
                  },
                  {
                    "id": "card",
                    "type": "card",
                    "children": [
                      {
                        "id": "cta",
                        "type": "button",
                        "text": "Continue",
                        "action": {
                          "type": "show_snackbar",
                          "message": "Clicked"
                        }
                      }
                    ]
                  }
                ]
              }
            }
            """.trimIndent()
        ).getOrThrow()

        assertEquals("home", schema.screenId)
        assertEquals("home_viewed", schema.analytics?.screenView)
        assertEquals(24, schema.layout.padding)
        val title = schema.layout.widgets.first() as TextWidget
        assertEquals(TextStyleToken.Headline, title.style)
        val card = schema.layout.widgets[1] as CardWidget
        val cta = card.children.single() as ButtonWidget
        assertTrue(cta.action is ShowSnackbarAction)
    }
}
