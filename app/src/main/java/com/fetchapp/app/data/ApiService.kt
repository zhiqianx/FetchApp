package com.fetchapp.app.data

import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit interface defining API endpoints for the Fetch Rewards service.
 *
 * This interface follows the Repository pattern where the ApiService handles
 * only the network communication layer. Retrofit automatically implements
 * this interface at runtime to make HTTP requests.
 */
interface ApiService {
    @GET("hiring.json")
    suspend fun getItems(): Response<List<Item>>
}