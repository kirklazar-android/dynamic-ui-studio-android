package com.gagandeep.dynamicuistudio.core.dynamicui.model

sealed interface DynamicComponent {
    val id: String
    val type: String
}

data class TextComponent(
    override val id: String,
    val value: String,
    val style: TextStyleToken = TextStyleToken.Body,
    override val type: String = "text"
) : DynamicComponent

data class ButtonComponent(
    override val id: String,
    val text: String,
    val action: DynamicAction? = null,
    override val type: String = "button"
) : DynamicComponent

data class HeroBannerComponent(
    override val id: String,
    val title: String,
    val subtitle: String? = null,
    val imageUrl: String? = null,
    val action: DynamicAction? = null,
    override val type: String = "hero_banner"
) : DynamicComponent

data class UnknownComponent(
    override val id: String,
    override val type: String,
    val reason: String
) : DynamicComponent

enum class TextStyleToken {
    Title,
    Body,
    Caption
}
