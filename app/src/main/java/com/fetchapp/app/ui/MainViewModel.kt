package com.fetchapp.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fetchapp.app.data.Item
import com.fetchapp.app.data.Repository
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen that manages UI state and coordinates data operations.
 *
 * This class implements the ViewModel component of the MVVM architecture pattern.
 * It serves as a bridge between the UI (MainActivity) and the data layer (Repository),
 * managing:
 * - UI state (loading, error, data)
 * - Lifecycle-aware data operations
 * - Business logic coordination
 *
 * The ViewModel survives configuration changes (like screen rotation) and provides
 * a clean separation between UI logic and business logic.
 */
class MainViewModel : ViewModel() {

    // repo instance
    private val repository = Repository()

    // LiveData for the filtered and sorted list of items
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    // LiveData for loading state.
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // LiveData for error state.
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Fetches items from the API and updates the UI state.
     *
     * It shows a loading indicator, clears old errors, retrieves data from the repository,
     * updates LiveData based on the result, and then stops the loading indicator.
     * The whole process runs in a coroutine tied to the ViewModel's lifecycle.
     */
    fun loadItems() {
        viewModelScope.launch {

            // start the loading and clear prev stage
            _loading.value = true
            _error.value = null

            // Call repository to fetch, filter, and sort the data
            repository.getItems()
                .onSuccess { itemList ->
                    _items.value = itemList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Unknown error occurred"
                }

            // loading finish
            _loading.value = false
        }
    }
}