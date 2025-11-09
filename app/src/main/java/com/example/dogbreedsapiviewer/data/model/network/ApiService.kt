package com.example.dogbreedsapiviewer.data.model.network

import com.example.dogbreedsapiviewer.data.model.DogApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("breeds/image/random/{count}")
    suspend fun getRandomDogImages(@Path("count") count: Int): DogApiResponse
}