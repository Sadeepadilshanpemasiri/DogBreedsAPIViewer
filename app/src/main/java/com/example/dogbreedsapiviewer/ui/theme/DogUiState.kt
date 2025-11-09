package com.example.dogbreedsapiviewer.viewmodel

// This data class represents all the possible states of your UI
data class DogBreedsUiState(
    val isLoading: Boolean = false,
    val imageUrls: List<String> = emptyList(),
    val error: String? = null
)
