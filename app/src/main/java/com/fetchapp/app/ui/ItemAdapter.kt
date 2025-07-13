package com.fetchapp.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fetchapp.app.R
import com.fetchapp.app.data.Item
import com.fetchapp.app.databinding.ItemListHeaderBinding
import com.fetchapp.app.databinding.ItemListItemBinding

/**
 * A RecyclerView adapter that displays a list of items grouped by their listId, with section headers.
 *
 * It uses two view types—one for headers ("List ID: X") and one for the actual items—
 * to clearly separate each group visually. The adapter takes a flat list of items
 * and restructures it so that headers appear above their corresponding items.
 */
class ItemAdapter : ListAdapter<ItemAdapter.ListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    // Track which groups are expanded (true = expanded, false = collapsed)
    internal val expandedGroups = mutableSetOf<Int>()

    // Add this interface for header click callbacks
    interface OnHeaderClickListener {
        fun onHeaderClick(listId: Int, isExpanded: Boolean)
    }

    // set click listener
    private var headerClickListener: OnHeaderClickListener? = null

    fun setOnHeaderClickListener(listener: OnHeaderClickListener) {
        headerClickListener = listener
    }

    // ViewType constants for the two different row types
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    /**
     * Sealed class representing the different types of items that can appear in the list.
     *
     * Using a sealed class provides type safety and ensures the compiler can verify
     * that all cases are handled in when statements. This is better than using
     * plain objects or enums because it allows each type to carry different data.
     */
    sealed class ListItem {
        data class Header(val listId: Int, val itemCount: Int) : ListItem()
        data class ItemData(val item: Item) : ListItem()
    }

    /**
     * Determines which ViewType should be used for a given position.
     *
     * This method tells RecyclerView whether to create a header ViewHolder
     * or an item ViewHolder for each position in the list.
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Header -> TYPE_HEADER
            is ListItem.ItemData -> TYPE_ITEM
        }
    }

    /**
     * Creates ViewHolder instances based on the ViewType.
     *
     * This method is called by RecyclerView when it needs a new ViewHolder
     * of a specific type. It inflates the appropriate layout and wraps it
     * in the corresponding ViewHolder class.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                // Create header ViewHolder with header layout
                val binding = ItemListHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            TYPE_ITEM -> {
                // Create item ViewHolder with item layout
                val binding = ItemListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    /**
     * Binds data to ViewHolder instances.
     *
     * This method is called by RecyclerView to display data at a specific position.
     * It delegates to the appropriate ViewHolder's bind method based on the item type.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.Header -> (holder as HeaderViewHolder).bind(item, this)
            is ListItem.ItemData -> (holder as ItemViewHolder).bind(item)
        }
    }

    /**
     * Submits a list of Items to the adapter, automatically grouping them by listId.
     *
     * This method takes a flat list of items—already filtered and sorted by the Repository—
     * and builds a structured list with headers inserted before each group. It wraps each
     * item in an ItemData object and adds a Header when the listId changes.
     *
     * Assumes the input list is pre-sorted by listId for efficient grouping.
     *
     * @param items A list of Items from the Repository, already filtered and sorted
     */
    fun submitItems(items: List<Item>) {
        // Convert flat item list to grouped list with headers
        val grouped = mutableListOf<ListItem>()
        val itemsByListId = items.groupBy { it.listId }

        itemsByListId.keys.sorted().forEach { listId ->
            // Always add the header
            grouped.add(ListItem.Header(listId, itemsByListId[listId]?.size ?: 0))

            // Only add items if this group is expanded
            if (expandedGroups.contains(listId)) {
                itemsByListId[listId]?.forEach { item ->
                    grouped.add(ListItem.ItemData(item))
                }
            }
        }

        // Submit to the parent ListAdapter which will handle diffing and updates
        super.submitList(grouped)
    }

    /**
     * ViewHolder for section headers that display the listId group.
     *
     * This ViewHolder is responsible for displaying "List ID: X" headers
     * that visually separate different groups of items.
     */
    class HeaderViewHolder(private val binding: ItemListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds header data to the view and sets up click handling for expand/collapse functionality.
         *
         * This method is called by RecyclerView whenever this ViewHolder needs to display
         * data for a specific header position. It updates the UI elements and configures
         * the click listener for toggling group visibility.
         *
         * @param header The Header object containing the listId and item count to display
         * @param adapter Reference to the adapter to access expandedGroups and trigger callbacks
         */
        fun bind(header: ListItem.Header, adapter: ItemAdapter) {
            // Check if this group is currently expanded
            val isExpanded = adapter.expandedGroups.contains(header.listId)
            // Set the header text showing listId and how many items
            binding.headerText.text = binding.root.context.getString(
                R.string.list_header,
                header.listId,
                header.itemCount
            )

            // Set the correct arrow state
            if (isExpanded) {
                binding.expandIcon.setImageResource(R.drawable.ic_expand_less)
            } else {
                binding.expandIcon.setImageResource(R.drawable.ic_expand_more)
            }

            // Handle header clicks - CHECK CURRENT STATE, DON'T USE CAPTURED isExpanded
            binding.root.setOnClickListener {
                // Check the CURRENT state, not the captured one
                val currentlyExpanded = adapter.expandedGroups.contains(header.listId)
                val newExpandedState = !currentlyExpanded

                // Update the adapter's expanded groups set based on the new state
                if (newExpandedState) {
                    adapter.expandedGroups.add(header.listId)
                } else {
                    adapter.expandedGroups.remove(header.listId)
                }

                // Notify the adapter that this group's expanded state changed
                adapter.headerClickListener?.onHeaderClick(header.listId, newExpandedState)

                // Icon change for clicking
                binding.expandIcon.setImageResource(
                    if (newExpandedState) R.drawable.ic_expand_less else R.drawable.ic_expand_more
                )
            }
        }
    }

    /**
     * ViewHolder for individual item data.
     *
     * This ViewHolder displays the actual item information including
     * the item name and ID in a card-based layout.
     */
    class ItemViewHolder(private val binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds item data to the view.
         * @param itemData The ItemData wrapper containing the Item to display
         */
        fun bind(itemData: ListItem.ItemData) {
            val item = itemData.item
            // Display the item name and id
            binding.itemName.text = item.name
            binding.itemId.text = binding.root.context.getString(R.string.item_id, item.id)
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates.
     *
     * This class enables RecyclerView to efficiently update only the items
     * that have actually changed, rather than recreating the entire list.
     * It provides smooth animations and better performance.
     */
    class DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                // Two headers are the same if they have the same listId
                oldItem is ListItem.Header && newItem is ListItem.Header ->
                    oldItem.listId == newItem.listId
                // Two items are the same if they have the same item ID
                oldItem is ListItem.ItemData && newItem is ListItem.ItemData ->
                    oldItem.item.id == newItem.item.id
                // Different types are never the same
                else -> false
            }
        }

        /**
         * Determines if two items have the same content.
         * Used by DiffUtil to decide whether to update the ViewHolder.
         */
        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Forces all ViewHolders to rebind by incrementing a version counter.
     * This ensures arrow states are properly updated.
     */
    fun forceRefresh() {
        val currentList = currentList.toList() // Create a copy
        submitList(null) // Clear the list
        submitList(currentList) // Resubmit to force rebind
    }

    /**
     * Resets all groups to collapsed state and refreshes the display.
     *
     * This method is called when the user pulls to refresh or retries after an error.
     * It ensures the UI returns to the default state where all groups are collapsed,
     * providing a consistent user experience.
     *
     * @param items The current list of items to redisplay in collapsed state
     */
    fun resetToCollapsed(items: List<Item>) {
        // Clear expanded state
        expandedGroups.clear()

        // Force complete refresh
        submitItems(items)

        // Additional force refresh to ensure arrows update
        post { forceRefresh() }
    }

    private fun post(action: () -> Unit) {
        // Run on next frame to ensure timing
        android.os.Handler(android.os.Looper.getMainLooper()).post(action)
    }
}