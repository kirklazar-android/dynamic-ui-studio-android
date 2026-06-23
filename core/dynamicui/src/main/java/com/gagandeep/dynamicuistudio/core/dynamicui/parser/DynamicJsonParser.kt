package com.gagandeep.dynamicuistudio.core.dynamicui.parser

import com.gagandeep.dynamicuistudio.core.dynamicui.model.AnalyticsSpec
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ButtonWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.CardWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DividerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicLayout
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.model.HeroBannerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.LayoutType
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ListWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.NavigateAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.OpenUrlAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.SpacerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextStyleToken
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TrackAnalyticsAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UnknownAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UnknownWidget
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

class DynamicJsonParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) {
    fun parse(rawJson: String): Result<DynamicScreenSchema> = runCatching {
        val root = json.parseToJsonElement(rawJson).jsonObject
        val layout = root.objectOrNull("layout") ?: error("layout is required")
        DynamicScreenSchema(
            screenId = root.string("screenId"),
            title = root.stringOrNull("title") ?: root.string("screenId"),
            version = root.int("version") ?: DynamicScreenSchema.SUPPORTED_SCHEMA_VERSION,
            analytics = root.objectOrNull("analytics")?.let(::parseAnalytics),
            layout = parseLayout(layout)
        )
    }

    private fun parseLayout(layout: JsonObject): DynamicLayout = DynamicLayout(
        type = when (layout.stringOrNull("type")?.lowercase()) {
            null, "vertical" -> LayoutType.Vertical
            else -> LayoutType.Vertical
        },
        padding = layout.int("padding") ?: 16,
        widgets = layout.arrayOrNull("widgets")?.map(::parseWidget).orEmpty()
    )

    private fun parseWidget(element: JsonElement): UiWidget {
        val widget = element.jsonObject
        val id = widget.stringOrNull("id").orEmpty()
        val type = widget.stringOrNull("type").orEmpty()
        val analytics = widget.objectOrNull("analytics")?.let(::parseAnalytics)
        return when (type) {
            TextWidget.TYPE -> TextWidget(
                id = id,
                text = widget.stringOrNull("text").orEmpty(),
                style = widget.stringOrNull("style")?.let(::parseTextStyle) ?: TextStyleToken.Body,
                analytics = analytics
            )
            ButtonWidget.TYPE -> ButtonWidget(
                id = id,
                text = widget.stringOrNull("text").orEmpty(),
                action = widget.objectOrNull("action")?.let(::parseAction),
                analytics = analytics
            )
            HeroBannerWidget.TYPE -> HeroBannerWidget(
                id = id,
                title = widget.stringOrNull("title").orEmpty(),
                subtitle = widget.stringOrNull("subtitle"),
                actionText = widget.stringOrNull("actionText") ?: "View details",
                action = widget.objectOrNull("action")?.let(::parseAction),
                analytics = analytics
            )
            CardWidget.TYPE -> CardWidget(
                id = id,
                children = widget.arrayOrNull("children")?.map(::parseWidget).orEmpty(),
                analytics = analytics
            )
            ListWidget.TYPE -> ListWidget(
                id = id,
                items = widget.arrayOrNull("items")?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }.orEmpty(),
                analytics = analytics
            )
            SpacerWidget.TYPE -> SpacerWidget(
                id = id,
                height = widget.int("height") ?: 16,
                analytics = analytics
            )
            DividerWidget.TYPE -> DividerWidget(
                id = id,
                analytics = analytics
            )
            else -> UnknownWidget(
                id = id.ifBlank { "unknown_${widget.hashCode()}" },
                type = type.ifBlank { "unknown" },
                reason = "Unsupported widget type",
                analytics = analytics
            )
        }
    }

    private fun parseAction(action: JsonObject): DynamicAction {
        return when (val type = action.stringOrNull("type").orEmpty()) {
            NavigateAction.TYPE -> NavigateAction(destination = action.stringOrNull("destination").orEmpty())
            OpenUrlAction.TYPE -> OpenUrlAction(url = action.stringOrNull("url").orEmpty())
            ShowSnackbarAction.TYPE -> ShowSnackbarAction(message = action.stringOrNull("message").orEmpty())
            "snackbar" -> ShowSnackbarAction(message = action.stringOrNull("message").orEmpty())
            TrackAnalyticsAction.TYPE -> TrackAnalyticsAction(eventName = action.stringOrNull("eventName").orEmpty())
            else -> UnknownAction(type = type.ifBlank { "unknown" })
        }
    }

    private fun parseAnalytics(analytics: JsonObject): AnalyticsSpec = AnalyticsSpec(
        screenView = analytics.stringOrNull("screenView"),
        click = analytics.stringOrNull("click")
    )

    private fun parseTextStyle(value: String): TextStyleToken = when (value.lowercase()) {
        "headlinelarge", "headline" -> TextStyleToken.Headline
        "titlemedium", "title" -> TextStyleToken.Title
        "caption", "label" -> TextStyleToken.Caption
        else -> TextStyleToken.Body
    }
}

private fun JsonObject.string(key: String): String = stringOrNull(key).orEmpty()

private fun JsonObject.stringOrNull(key: String): String? =
    (get(key) as? JsonPrimitive)?.contentOrNull

private fun JsonObject.int(key: String): Int? =
    (get(key) as? JsonPrimitive)?.intOrNull

private fun JsonObject.objectOrNull(key: String): JsonObject? =
    get(key) as? JsonObject

private fun JsonObject.arrayOrNull(key: String): JsonArray? =
    get(key) as? JsonArray
