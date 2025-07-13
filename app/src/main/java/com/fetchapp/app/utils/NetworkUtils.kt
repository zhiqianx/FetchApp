package com.fetchapp.app.utils

import com.fetchapp.app.data.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * A singleton object that sets up and manages all network configuration.
 *
 * This utility centralizes everything related to networking, including:
 * - Configuring the HTTP client (e.g., timeouts, logging)
 * - Creating and setting up Retrofit with converters
 * - Ensuring consistent network behavior across the app
 * - Making it easy to switch base URLs for testing or staging
 *
 * Using an object ensures there is only one network setup used throughout the app.
 */
object NetworkUtils {

    // Base URL for the Fetch Rewards API as specified in the requirements
    private const val BASE_URL = "https://hiring.fetch.com/"

    /**
     * HTTP logging interceptor for debugging network requests.
     *
     * This interceptor logs all HTTP request and response details to the console,
     * for debug use.
     *
     * Level.BODY logs the complete request/response including headers and body content.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Returns a configured OkHttpClient with logging and timeout settings.
     *
     * This client handles all HTTP communication and includes:
     * - A logging interceptor for easier debugging
     * - Sensible timeout values to support slow or unstable networks
     * - Built-in connection pooling and performance optimizations from OkHttp
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // Timeout for establishing initial connection
        .connectTimeout(30, TimeUnit.SECONDS)
        // Timeout for waiting for data to be received
        .readTimeout(30, TimeUnit.SECONDS)
        // Timeout for sending data to server
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Creates and returns a configured Retrofit instance with JSON support and a custom HTTP client.
     *
     * Retrofit handles converting Kotlin interface methods into HTTP calls. This setup includes:
     * - The base URL for the Fetch Rewards API
     * - A custom OkHttpClient with logging and timeout settings
     * - A Gson converter to automatically handle JSON ↔ Kotlin object mapping
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Creates and returns an instance of ApiService.
     *
     * Uses Retrofit’s `create()` function to generate an implementation of the ApiService interface.
     * Retrofit reads the interface annotations and builds the corresponding HTTP request logic at runtime.
     *
     * @return A ready-to-use ApiService for making requests to the Fetch Rewards API
     */
    fun createApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}