package com.fetchapp.app.data

import com.fetchapp.app.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class that handles data operations and business logic.
 *
 * This class implements the Repository pattern, acting as a single source of truth
 * for data. It coordinates between the API service and the UI layer, handling:
 * - Network requests
 * - Data transformation (filtering and sorting)
 * - Error handling
 * - Thread management
 *
 * The Repository encapsulates all the business logic required by the Fetch exercise:
 * 1. Fetch data from API
 * 2. Filter out items with blank/null names
 * 3. Sort by listId first, then by name
 */
class Repository {

    // ApiService instance for making network requests
    private val apiService = NetworkUtils.createApiService()

    /**
     * Fetches items from the API and applies business logic transformations.
     *
     * This method implements all the core requirements of the Fetch exercise:
     * 1. Retrieves data from the specified API endpoint
     * 2. Filters out items where name is blank or null
     * 3. Sorts results first by listId, then by name
     *
     * The method uses Kotlin's Result type for clean error handling, avoiding
     * exceptions bubbling up to the UI layer.
     *
     * @return Result.success with filtered/sorted items, or Result.failure with error
     */
    suspend fun getItems(): Result<List<Item>> = withContext(Dispatchers.IO) {
        try {
            // Make the API call on the IO dispatcher (background thread)
            val response = apiService.getItems()

            if (response.isSuccessful) {
                // Extract the list from response body, defaulting to empty list if null
                val items = response.body() ?: emptyList()

                // Apply business logic transformations:
                val filteredAndSortedItems = items
                    // Requirement: Filter out items with blank/null names
                    .filter { it.isValid }
                    // Requirement: Sort first by listId, then by name
                    .sortedWith(compareBy<Item> { it.listId }.thenBy { it.name })

                Result.success(filteredAndSortedItems)
            } else {
                // HTTP error (4xx, 5xx) - create descriptive error message
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Network error, parsing error, or other exception
            Result.failure(e)
        }
    }
}