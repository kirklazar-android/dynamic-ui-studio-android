package com.gagandeep.dynamicuistudio.feature.renderer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreenSchema
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ShowSnackbarAction
import com.gagandeep.dynamicuistudio.feature.renderer.data.AssetDynamicSchemaSource
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.RenderContext
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.WidgetRendererRegistry
import com.gagandeep.dynamicuistudio.feature.renderer.rendering.defaultWidgetRendererRegistry
import kotlinx.coroutines.launch

@Composable
fun RendererScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: RendererViewModel = viewModel(
        factory = remember(context) {
            RendererViewModelFactory(
                schemaSource = AssetDynamicSchemaSource(context.assets)
            )
        }
    )
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    RendererContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = { action ->
            viewModel.recordAction(action)
            if (action is ShowSnackbarAction) {
                scope.launch {
                    snackbarHostState.showSnackbar(action.message)
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun RendererContent(
    uiState: RendererUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (DynamicAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (uiState) {
            RendererUiState.Loading -> LoadingState(Modifier.padding(innerPadding))
            is RendererUiState.Error -> ErrorState(
                state = uiState,
                modifier = Modifier.padding(innerPadding)
            )
            is RendererUiState.Success -> DynamicScreenRenderer(
                schema = uiState.schema,
                actionLogs = uiState.actionLogs,
                onAction = onAction,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun DynamicScreenRenderer(
    schema: DynamicScreenSchema,
    actionLogs: List<String>,
    onAction: (DynamicAction) -> Unit,
    modifier: Modifier = Modifier,
    registry: WidgetRendererRegistry = remember { defaultWidgetRendererRegistry() }
) {
    val renderContext = remember(onAction) {
        RenderContext(onAction = onAction)
    }
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
            registry.RenderWidget(
                widget = widget,
                context = renderContext
            )
        }

        if (actionLogs.isNotEmpty()) {
            item(key = "action_logs") {
                ActionLogPanel(actionLogs)
            }
        }
    }
}

@Composable
private fun ActionLogPanel(actionLogs: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Action Log",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        actionLogs.takeLast(3).forEach { log ->
            Text(
                text = log,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
    state: RendererUiState.Error,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
            state.validationErrors.forEach { error ->
                Text(
                    text = "${error.path}: ${error.message}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
