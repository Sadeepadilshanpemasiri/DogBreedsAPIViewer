package com.example.dogbreedsapiviewer

// --- IMPORTS FOR A CLEAN, WORKING APP ---
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dogbreedsapiviewer.ui.theme.DogBreedsAPIViewerTheme
import com.example.dogbreedsapiviewer.viewmodel.DogBreedsUiState
import com.example.dogbreedsapiviewer.viewmodel.DogBreedsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogBreedsAPIViewerTheme {
                DogBreedsApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogBreedsApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dog Breeds Gallery") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        DogBreedsScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// --- Simple screen (no PullToRefreshBox) ---
@Composable
fun DogBreedsScreen(
    modifier: Modifier = Modifier,
    viewModel: DogBreedsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DogBreedsContent(
            uiState = uiState,
            loadMoreAction = { viewModel.loadMoreImages() },
            retryAction = { viewModel.retry() }
        )
    }
}

@Composable
fun DogBreedsContent(
    uiState: DogBreedsUiState,
    loadMoreAction: () -> Unit,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        // Initial loading state
        uiState.isLoading && uiState.imageUrls.isEmpty() -> {
            CircularProgressIndicator()
        }
        // Error state
        !uiState.error.isNullOrEmpty() -> {
            ErrorScreen(
                errorMessage = uiState.error,
                onRetry = retryAction
            )
        }
        // Success state (with or without images)
        else -> {
            DogBreedsList(
                imageUrls = uiState.imageUrls,
                isLoadingMore = uiState.isLoading,
                onLoadMore = loadMoreAction,
                onReload = retryAction
            )
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun DogBreedsList(
    imageUrls: List<String>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reload button (top)
        if (!isLoadingMore && imageUrls.isNotEmpty()) {
            item {
                Button(
                    onClick = onReload,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load New Images")
                }
            }
        }

        if (imageUrls.isEmpty()) {
            item {
                Text(
                    text = "No images found. Try loading new images!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        } else {
            items(
                items = imageUrls,
                key = { it }
            ) { imageUrl ->
                DogImageCard(
                    imageUrl = imageUrl
                )
            }
        }

        // Loading indicator at the bottom of the list
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Infinite-scroll detection
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            if (lastVisibleItem == null) {
                false
            } else {
                lastVisibleItem.index >= layoutInfo.totalItemsCount - 5
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }
}

@Composable
fun DogImageCard(imageUrl: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "A beautiful dog",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            error = painterResource(id = R.drawable.ic_launcher_background)
        )
    }
}
