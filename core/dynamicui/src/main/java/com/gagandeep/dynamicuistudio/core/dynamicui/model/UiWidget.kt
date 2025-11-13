package com.gagandeep.dynamicuistudio.core.dynamicui.model

sealed interface UiWidget {
    val id: String
    val type: String
    val analytics: AnalyticsSpec?
}

data class TextWidget(
    override val id: String,
    val text: String,
    val style: TextStyleToken = TextStyleToken.Body,
    override val analytics: AnalyticsSpec? = null,
    override val type: String = TYPE
) : UiWidget {
    companion object {
        const val TYPE = "text"
    }
}

data class ButtonWidget(
    override val id: String,
    val text: String,
    val action: DynamicAction? = null,
    override val analytics: AnalyticsSpec? = null,
    override val type: String = TYPE
) : UiWidget {
    companion object {
        const val TYPE = "button"
    }
}

data class CardWidget(
    override val id: String,
    val children: List<UiWidget>,
    override val analytics: AnalyticsSpec? = null,
    override val type: String = TYPE
) : UiWidget {
    companion object {
        const val TYPE = "card"
    }
}

data class ListWidget(
    override val id: String,
    val items: List<String>,
    override val analytics: AnalyticsSpec? = null,
    override val type: String = TYPE
) : UiWidget {
    companion object {
        const val TYPE = "list"
    }
}

data class SpacerWidget(
    override val id: String,
    val height: Int = 16,
    override val analytics: AnalyticsSpec? = null,
    override val type: String = TYPE
) : UiWidget {
    companion object {
        const val TYPE = "spacer"
    }
}

data class DividerWidget(
    override val id: String,
    override val analytics: AnalyticsSpec? = null,
    override val type: String = TYPE
) : UiWidget {
    companion object {
        const val TYPE = "divider"
    }
}

data class UnknownWidget(
    override val id: String,
    override val type: String,
    val reason: String,
    override val analytics: AnalyticsSpec? = null
) : UiWidget

enum class TextStyleToken {
    Headline,
    Title,
    Body,
    Caption
}
