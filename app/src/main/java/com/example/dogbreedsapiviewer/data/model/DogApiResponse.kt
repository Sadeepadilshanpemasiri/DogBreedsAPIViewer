package com.example.dogbreedsapiviewer.data.model

import com.google.gson.annotations.SerializedName

data class DogApiResponse(
    @SerializedName("message")
    val imageUrls: List<String>,

    @SerializedName("status")
    val status: String
)