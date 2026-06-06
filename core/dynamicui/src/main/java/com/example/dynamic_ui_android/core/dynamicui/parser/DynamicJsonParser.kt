package com.example.dynamic_ui_android.core.dynamicui.parser

import com.example.dynamic_ui_android.core.dynamicui.model.ButtonComponent
import com.example.dynamic_ui_android.core.dynamicui.model.DynamicAction
import com.example.dynamic_ui_android.core.dynamicui.model.DynamicComponent
import com.example.dynamic_ui_android.core.dynamicui.model.DynamicScreen
import com.example.dynamic_ui_android.core.dynamicui.model.HeroBannerComponent
import com.example.dynamic_ui_android.core.dynamicui.model.NavigateAction
import com.example.dynamic_ui_android.core.dynamicui.model.OpenUrlAction
import com.example.dynamic_ui_android.core.dynamicui.model.ScreenMetadata
import com.example.dynamic_ui_android.core.dynamicui.model.SnackbarAction
import com.example.dynamic_ui_android.core.dynamicui.model.TextComponent
import com.example.dynamic_ui_android.core.dynamicui.model.TextStyleToken
import com.example.dynamic_ui_android.core.dynamicui.model.UnknownAction
import com.example.dynamic_ui_android.core.dynamicui.model.UnknownComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DynamicJsonParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) {
    fun parse(rawJson: String): Result<DynamicScreen> = runCatching {
        val root = json.parseToJsonElement(rawJson).jsonObject
        DynamicScreen(
            screenId = root.string("screenId"),
            version = root.int("version") ?: 1,
            metadata = root.objectOrNull("metadata")?.let(::parseMetadata),
            components = root["components"]?.jsonArray?.map(::parseComponent).orEmpty()
        )
    }

    private fun parseMetadata(metadata: JsonObject): ScreenMetadata = ScreenMetadata(
        analyticsScreenName = metadata.stringOrNull("analyticsScreenName"),
        experimentId = metadata.stringOrNull("experimentId")
    )

    private fun parseComponent(element: kotlinx.serialization.json.JsonElement): DynamicComponent {
        val component = element.jsonObject
        val id = component.stringOrNull("id").orEmpty()
        val type = component.stringOrNull("type").orEmpty()
        return when (type) {
            "text" -> TextComponent(
                id = id,
                value = component.stringOrNull("value").orEmpty(),
                style = component.stringOrNull("style")?.let(::parseTextStyle) ?: TextStyleToken.Body
            )
            "button" -> ButtonComponent(
                id = id,
                text = component.stringOrNull("text").orEmpty(),
                action = component.objectOrNull("action")?.let(::parseAction)
            )
            "hero_banner" -> HeroBannerComponent(
                id = id,
                title = component.stringOrNull("title").orEmpty(),
                subtitle = component.stringOrNull("subtitle"),
                imageUrl = component.stringOrNull("imageUrl"),
                action = component.objectOrNull("action")?.let(::parseAction)
            )
            else -> UnknownComponent(
                id = id.ifBlank { "unknown_${component.hashCode()}" },
                type = type.ifBlank { "unknown" },
                reason = "Unsupported component type"
            )
        }
    }

    private fun parseAction(action: JsonObject): DynamicAction {
        return when (val type = action.stringOrNull("type").orEmpty()) {
            "navigate" -> NavigateAction(destination = action.stringOrNull("destination").orEmpty())
            "open_url" -> OpenUrlAction(url = action.stringOrNull("url").orEmpty())
            "snackbar" -> SnackbarAction(message = action.stringOrNull("message").orEmpty())
            else -> UnknownAction(type = type.ifBlank { "unknown" })
        }
    }

    private fun parseTextStyle(value: String): TextStyleToken = when (value.lowercase()) {
        "title" -> TextStyleToken.Title
        "caption" -> TextStyleToken.Caption
        else -> TextStyleToken.Body
    }
}

private fun JsonObject.string(key: String): String = stringOrNull(key).orEmpty()

private fun JsonObject.stringOrNull(key: String): String? =
    (get(key) as? JsonPrimitive)?.contentOrNull

private fun JsonObject.int(key: String): Int? =
    (get(key) as? JsonPrimitive)?.intOrNull

private fun JsonObject.objectOrNull(key: String): JsonObject? =
    get(key)?.jsonObject
