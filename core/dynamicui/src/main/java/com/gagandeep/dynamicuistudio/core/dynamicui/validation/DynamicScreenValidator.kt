package com.gagandeep.dynamicuistudio.core.dynamicui.validation

import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreen

class DynamicScreenValidator {
    fun validate(screen: DynamicScreen): List<String> = buildList {
        if (screen.screenId.isBlank()) add("screenId must not be blank")
        if (screen.components.isEmpty()) add("components must not be empty")
        screen.components.forEach { component ->
            if (component.id.isBlank()) add("component id must not be blank")
            if (component.type.isBlank()) add("component type must not be blank")
        }
    }
}
