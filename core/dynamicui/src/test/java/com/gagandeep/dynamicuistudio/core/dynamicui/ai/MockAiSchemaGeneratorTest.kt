package com.gagandeep.dynamicuistudio.core.dynamicui.ai

import com.gagandeep.dynamicuistudio.core.dynamicui.validation.DynamicScreenValidator
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.ValidationResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockAiSchemaGeneratorTest {
    private val generator = MockAiSchemaGenerator()
    private val validator = DynamicScreenValidator()

    @Test
    fun generateSchema_returnsValidationReadySchema() = runBlocking {
        val schema = generator.generateSchema("fintech rewards screen").getOrThrow()

        assertEquals("fintech_rewards_screen", schema.screenId)
        assertTrue(validator.validate(schema) is ValidationResult.Valid)
    }
}
