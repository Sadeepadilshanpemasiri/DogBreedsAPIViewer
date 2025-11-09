package com.example.dogbreedsapiviewer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsapiviewer.data.model.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DogBreedsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DogBreedsUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitClient.apiService

    companion object {
        private const val IMAGE_BATCH_SIZE = 15
    }

    init {
        // Fetch the very first batch of images
        fetchDogImages(isInitialLoad = true)
    }


    fun loadMoreImages() {
        // Prevent multiple simultaneous loads
        if (_uiState.value.isLoading) return

        fetchDogImages(isInitialLoad = false)
    }

    private fun fetchDogImages(isInitialLoad: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Call the API with our defined batch size
                val response = apiService.getRandomDogImages(IMAGE_BATCH_SIZE)

                _uiState.update { currentState ->
                    // If it's the first load, the list is empty. Otherwise, use the current list.
                    val existingImages = if (isInitialLoad) emptyList() else currentState.imageUrls
                    // Add the newly fetched images to the end of the existing list
                    val newImageList = existingImages + response.imageUrls

                    currentState.copy(
                        isLoading = false,
                        imageUrls = newImageList
                    )
                }
            } catch (e: Exception) {
                // Handle network errors
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }

    fun retry() {
        // Retry will always start from a fresh list
        fetchDogImages(isInitialLoad = true)
    }
}
