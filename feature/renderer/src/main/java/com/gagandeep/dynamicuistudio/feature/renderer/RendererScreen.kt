package com.gagandeep.dynamicuistudio.feature.renderer

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ButtonWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.CardWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DividerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ListWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.SpacerWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextStyleToken
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UiWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UnknownWidget
import com.gagandeep.dynamicuistudio.core.dynamicui.parser.DynamicJsonParser
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.DynamicScreenValidator
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.ValidationResult
import kotlinx.coroutines.launch

@Composable
fun RendererScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var uiState by remember { mutableStateOf(RendererUiState(isLoading = true)) }

    LaunchedEffect(Unit) {
        uiState = loadRendererState(context)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage.orEmpty(),
                modifier = Modifier.padding(innerPadding)
            )
            uiState.schema != null -> DynamicScreenRenderer(
                schema = requireNotNull(uiState.schema),
                onAction = { action ->
                    if (action is ShowSnackbarAction) {
                        scope.launch {
                            snackbarHostState.showSnackbar(action.message)
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun DynamicScreenRenderer(
    schema: DynamicScreenSchema,
    onAction: (DynamicAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(schema.layout.padding.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = schema.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = schema.analytics?.screenView ?: schema.screenId,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(
            items = schema.layout.widgets,
            key = { it.id }
        ) { widget ->
            DynamicWidgetView(
                widget = widget,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DynamicWidgetView(
    widget: UiWidget,
    onAction: (DynamicAction) -> Unit
) {
    when (widget) {
        is TextWidget -> TextWidgetView(widget)
        is ButtonWidget -> ButtonWidgetView(widget, onAction)
        is CardWidget -> CardWidgetView(widget, onAction)
        is ListWidget -> ListWidgetView(widget)
        is SpacerWidget -> Spacer(Modifier.height(widget.height.dp))
        is DividerWidget -> HorizontalDivider()
        is UnknownWidget -> UnknownWidgetView(widget)
    }
}

@Composable
private fun TextWidgetView(widget: TextWidget) {
    Text(
        text = widget.text,
        style = when (widget.style) {
            TextStyleToken.Headline -> MaterialTheme.typography.headlineMedium
            TextStyleToken.Title -> MaterialTheme.typography.titleLarge
            TextStyleToken.Body -> MaterialTheme.typography.bodyLarge
            TextStyleToken.Caption -> MaterialTheme.typography.labelLarge
        }
    )
}

@Composable
private fun ButtonWidgetView(
    widget: ButtonWidget,
    onAction: (DynamicAction) -> Unit
) {
    Button(
        onClick = { widget.action?.let(onAction) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(widget.text)
    }
}

@Composable
private fun CardWidgetView(
    widget: CardWidget,
    onAction: (DynamicAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            widget.children.forEach { child ->
                DynamicWidgetView(child, onAction)
            }
        }
    }
}

@Composable
private fun ListWidgetView(widget: ListWidget) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        widget.items.forEach { item ->
            Text(
                text = "- $item",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun UnknownWidgetView(widget: UnknownWidget) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = "Unsupported widget: ${widget.type}",
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )
    }
}

private data class RendererUiState(
    val isLoading: Boolean = false,
    val schema: DynamicScreenSchema? = null,
    val errorMessage: String? = null
)

private fun loadRendererState(context: Context): RendererUiState {
    val parser = DynamicJsonParser()
    val validator = DynamicScreenValidator()
    val rawJson = context.assets.open("screens/home.json")
        .bufferedReader()
        .use { it.readText() }

    val schema = parser.parse(rawJson).getOrElse { error ->
        return RendererUiState(errorMessage = error.message ?: "Unable to parse dynamic screen")
    }
    return when (val validation = validator.validate(schema)) {
        ValidationResult.Valid -> RendererUiState(schema = schema)
        is ValidationResult.Invalid -> RendererUiState(
            errorMessage = validation.errors.joinToString { "${it.path}: ${it.message}" }
        )
    }
}
