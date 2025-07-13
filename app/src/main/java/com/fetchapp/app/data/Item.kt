package com.fetchapp.app.data

import com.google.gson.annotations.SerializedName

/**
* Data model representing an item from the Fetch Rewards API.
*
* This class maps directly to the JSON structure returned by the API endpoint.
* Each item has an id, belongs to a listId group, and has an optional name.
*
* @property id Unique identifier for the item
* @property listId Group identifier used for organizing items
* @property name Display name for the item (can be null or blank)
*/
data class Item(
    @SerializedName("id")
    val id: Int,

    @SerializedName("listId")
    val listId: Int,

    @SerializedName("name")
    val name: String?
) {
    // Helper property to check if item should be filtered out
    val isValid: Boolean
        get() = !name.isNullOrBlank()
}