package com.example.dogbreedsapiviewer.viewmodel


data class DogBreedsUiState(
    val isLoading: Boolean = false,
    val imageUrls: List<String> = emptyList(),
    val error: String? = null
)
