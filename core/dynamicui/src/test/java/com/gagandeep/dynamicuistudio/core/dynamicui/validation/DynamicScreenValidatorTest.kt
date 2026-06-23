package com.gagandeep.dynamicuistudio.core.dynamicui.validation

import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicLayout
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicScreenValidatorTest {
    private val validator = DynamicScreenValidator()

    @Test
    fun validate_returnsValidForSupportedSchema() {
        val result = validator.validate(
            DynamicScreenSchema(
                screenId = "home",
                title = "Home",
                layout = DynamicLayout(
                    widgets = listOf(TextWidget(id = "title", text = "Welcome"))
                )
            )
        )

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun validate_returnsDuplicateWidgetIdErrorAcrossNestedWidgets() {
        val result = validator.validate(
            DynamicScreenSchema(
                screenId = "home",
                title = "Home",
                layout = DynamicLayout(
                    widgets = listOf(
                        TextWidget(id = "title", text = "Welcome"),
                        TextWidget(id = "title", text = "Again")
                    )
                )
            )
        )

        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.message == "Duplicate widget id: title" })
    }
}
