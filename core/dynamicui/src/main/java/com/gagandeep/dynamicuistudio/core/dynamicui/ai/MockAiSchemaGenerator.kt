package com.gagandeep.dynamicuistudio.core.dynamicui.ai

import com.gagandeep.dynamicuistudio.core.dynamicui.model.AnalyticsSpec
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ButtonWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.CardWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DividerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicLayout
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ListWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextStyleToken
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.parser.DynamicJsonParser

class MockAiSchemaGenerator(
    private val parser: DynamicJsonParser = DynamicJsonParser()
) : AiSchemaGenerator {
    override suspend fun generateSchema(prompt: String): Result<DynamicScreenSchema> = runCatching {
        val normalizedPrompt = prompt.ifBlank { "Dynamic onboarding screen" }
        DynamicScreenSchema(
            screenId = normalizedPrompt.toScreenId(),
            title = normalizedPrompt.toTitle(),
            analytics = AnalyticsSpec(screenView = "${normalizedPrompt.toScreenId()}_viewed"),
            layout = DynamicLayout(
                widgets = listOf(
                    TextWidget(
                        id = "generated_title",
                        text = normalizedPrompt.toTitle(),
                        style = TextStyleToken.Headline
                    ),
                    CardWidget(
                        id = "generated_summary_card",
                        children = listOf(
                            TextWidget(
                                id = "generated_summary",
                                text = "Generated from prompt: $normalizedPrompt",
                                style = TextStyleToken.Body
                            ),
                            ListWidget(
                                id = "generated_highlights",
                                items = listOf(
                                    "JSON schema candidate",
                                    "Validation-ready widget IDs",
                                    "Analytics event placeholder"
                                )
                            )
                        )
                    ),
                    DividerWidget(id = "generated_divider"),
                    ButtonWidget(
                        id = "generated_cta",
                        text = "Preview Generated Screen",
                        analytics = AnalyticsSpec(click = "${normalizedPrompt.toScreenId()}_cta_clicked"),
                        action = ShowSnackbarAction("Generated schema previewed")
                    )
                )
            )
        )
    }

    override suspend fun improveSchema(
        schemaJson: String,
        instruction: String
    ): Result<DynamicScreenSchema> {
        return parser.parse(schemaJson).map { schema ->
            schema.copy(
                title = schema.title.ifBlank { "Improved Dynamic Screen" },
                analytics = schema.analytics ?: AnalyticsSpec(
                    screenView = "${schema.screenId}_viewed"
                )
            )
        }
    }

    private fun String.toScreenId(): String = lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .ifBlank { "generated_screen" }

    private fun String.toTitle(): String = trim()
        .replaceFirstChar { char -> char.uppercase() }
}
