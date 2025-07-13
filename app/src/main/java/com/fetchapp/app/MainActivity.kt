package com.fetchapp.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fetchapp.app.databinding.ActivityMainBinding
import com.fetchapp.app.ui.ItemAdapter
import com.fetchapp.app.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Main Activity that displays the list of items from the Fetch Rewards API.
 *
 * This Activity implements the View layer of the MVVM architecture pattern.
 */
class MainActivity : AppCompatActivity() {

    // View binding for type-safe access to layout views
    private lateinit var binding: ActivityMainBinding

    // ViewModel instance using the viewModels() delegate for automatic lifecycle management
    private val viewModel: MainViewModel by viewModels()

    // Adapter for the RecyclerView that handles displaying grouped items
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding to access layout views safely
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up UI components
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        // Load data initially
        viewModel.loadItems()
    }

    /**
     * Configures the RecyclerView with adapter and layout manager.
     *
     * Sets up the list display component that will show the grouped items.
     * Uses LinearLayoutManager for a standard vertical list layout.
     */
    private fun setupRecyclerView() {
        adapter = ItemAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            // Add some nice item animations
            itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator().apply {
                addDuration = 200
                removeDuration = 200
            }
        }

        // Handle header clicks to refresh the list
        adapter.setOnHeaderClickListener(object : ItemAdapter.OnHeaderClickListener {
            override fun onHeaderClick(listId: Int, isExpanded: Boolean) {
                // Get the current items and re-submit them to trigger a refresh
                val currentItems = viewModel.items.value ?: emptyList()
                adapter.submitItems(currentItems)
            }
        })
    }

    /**
     * Configures the SwipeRefreshLayout for pull-to-refresh functionality.
     *
     * Provides a standard Android pattern to manually
     * refresh the data by pulling down on the list.
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadItems()
        }
    }

    /**
     * Sets up observers for ViewModel LiveData to react to state changes.
     *
     * This method implements the reactive UI pattern where the UI automatically
     * updates when the underlying data changes. It observes three state streams:
     * - items: The filtered and sorted list to display
     * - loading: Whether a network request is in progress
     * - error: Any error messages to show the user
     */
    private fun observeViewModel() {
        // Observe the items list and update the adapter when it changes
        viewModel.items.observe(this, Observer { items ->
            // Pass the new data to the adapter, which will handle grouping and display
            adapter.submitItems(items)
            // Hide the refresh spinner since loading is complete
            binding.swipeRefresh.isRefreshing = false
        })

        // Observe loading state to show/hide progress indicator
        viewModel.loading.observe(this, Observer { isLoading ->
            // Show progress bar during initial load, hide it otherwise
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Observe error state to display user-friendly error messages
        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                // Show error in a Snackbar with a retry action
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { viewModel.loadItems() }
                    .show()
                // Hide refresh spinner on error
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }
}