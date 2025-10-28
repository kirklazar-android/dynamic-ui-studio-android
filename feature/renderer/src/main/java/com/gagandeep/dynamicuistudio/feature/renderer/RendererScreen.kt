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
import com.gagandeep.dynamicuistudio.core.dynamicui.model.ButtonComponent
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicComponent
import com.gagandeep.dynamicuistudio.core.dynamicui.model.DynamicScreen
import com.gagandeep.dynamicuistudio.core.dynamicui.model.HeroBannerComponent
import com.gagandeep.dynamicuistudio.core.dynamicui.model.SnackbarAction
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextComponent
import com.gagandeep.dynamicuistudio.core.dynamicui.model.TextStyleToken
import com.gagandeep.dynamicuistudio.core.dynamicui.model.UnknownComponent
import com.gagandeep.dynamicuistudio.core.dynamicui.parser.DynamicJsonParser
import com.gagandeep.dynamicuistudio.core.dynamicui.validation.DynamicScreenValidator
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
            uiState.screen != null -> DynamicScreenRenderer(
                screen = requireNotNull(uiState.screen),
                onAction = { action ->
                    if (action is SnackbarAction) {
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
    screen: DynamicScreen,
    onAction: (DynamicAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DynamicUI Studio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = screen.metadata?.experimentId ?: screen.screenId,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(
            items = screen.components,
            key = { it.id }
        ) { component ->
            DynamicComponentRenderer(
                component = component,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DynamicComponentRenderer(
    component: DynamicComponent,
    onAction: (DynamicAction) -> Unit
) {
    when (component) {
        is TextComponent -> TextComponentView(component)
        is HeroBannerComponent -> HeroBannerComponentView(component, onAction)
        is ButtonComponent -> ButtonComponentView(component, onAction)
        is UnknownComponent -> UnknownComponentView(component)
    }
}

@Composable
private fun TextComponentView(component: TextComponent) {
    Text(
        text = component.value,
        style = when (component.style) {
            TextStyleToken.Title -> MaterialTheme.typography.titleLarge
            TextStyleToken.Body -> MaterialTheme.typography.bodyLarge
            TextStyleToken.Caption -> MaterialTheme.typography.labelLarge
        }
    )
}

@Composable
private fun HeroBannerComponentView(
    component: HeroBannerComponent,
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
            Text(
                text = component.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            component.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            component.action?.let { action ->
                Spacer(Modifier.height(4.dp))
                Button(onClick = { onAction(action) }) {
                    Text("View details")
                }
            }
        }
    }
}

@Composable
private fun ButtonComponentView(
    component: ButtonComponent,
    onAction: (DynamicAction) -> Unit
) {
    Button(
        onClick = { component.action?.let(onAction) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(component.text)
    }
}

@Composable
private fun UnknownComponentView(component: UnknownComponent) {
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
            text = "Unsupported component: ${component.type}",
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
    val screen: DynamicScreen? = null,
    val errorMessage: String? = null
)

private fun loadRendererState(context: Context): RendererUiState {
    val parser = DynamicJsonParser()
    val validator = DynamicScreenValidator()
    val rawJson = context.assets.open("screens/home.json")
        .bufferedReader()
        .use { it.readText() }

    val screen = parser.parse(rawJson).getOrElse { error ->
        return RendererUiState(errorMessage = error.message ?: "Unable to parse dynamic screen")
    }
    val validationErrors = validator.validate(screen)
    return if (validationErrors.isEmpty()) {
        RendererUiState(screen = screen)
    } else {
        RendererUiState(errorMessage = validationErrors.joinToString())
    }
}
