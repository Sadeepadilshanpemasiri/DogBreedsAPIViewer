package com.example.dogbreedsapiviewer.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response from the Dog CEO API.
 * This matches the JSON structure returned by the API.
 *
 * JSON Response Example:
 * {
 *   "message": [
 *     "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
 *     "https://images.dog.ceo/breeds/hound-basset/n02088238_10052.jpg"
 *   ],
 *   "status": "success"
 * }
 */
data class DogApiResponse(
    @SerializedName("message")
    val imageUrls: List<String>,

    @SerializedName("status")
    val status: String
)