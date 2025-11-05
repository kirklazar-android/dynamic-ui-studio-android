package com.gagandeep.dynamicuistudio.feature.renderer.rendering

import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicAction

data class RenderContext(
    val onAction: (DynamicAction) -> Unit
)
