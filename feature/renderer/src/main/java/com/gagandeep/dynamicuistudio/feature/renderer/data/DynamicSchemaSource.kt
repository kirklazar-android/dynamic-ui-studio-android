package com.gagandeep.dynamicuistudio.feature.renderer.data

interface DynamicSchemaSource {
    suspend fun loadSchema(screenId: String): Result<String>
}
