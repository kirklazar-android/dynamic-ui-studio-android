package com.gagandeep.dynamicuistudio.core.dynamicui.ai

import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema

interface AiSchemaGenerator {
    suspend fun generateSchema(prompt: String): Result<DynamicScreenSchema>

    suspend fun improveSchema(
        schemaJson: String,
        instruction: String
    ): Result<DynamicScreenSchema>
}
