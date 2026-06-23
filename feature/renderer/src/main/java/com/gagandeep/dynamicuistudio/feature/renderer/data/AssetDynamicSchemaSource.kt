package com.gagandeep.dynamicuistudio.feature.renderer.data

import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetDynamicSchemaSource(
    private val assetManager: AssetManager
) : DynamicSchemaSource {
    override suspend fun loadSchema(screenId: String): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            assetManager.open("screens/$screenId.json")
                .bufferedReader()
                .use { it.readText() }
        }
    }
}
