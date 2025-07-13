package com.fetchapp.app

import com.fetchapp.app.data.Item
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun item_validation_works() {
        // Test that items with valid names are considered valid
        val validItem = Item(1, 1, "Item 1")
        assertTrue(validItem.isValid)

        // Test that items with null names are invalid
        val nullNameItem = Item(2, 1, null)
        assertFalse(nullNameItem.isValid)

        // Test that items with blank names are invalid
        val blankNameItem = Item(3, 1, "")
        assertFalse(blankNameItem.isValid)
    }

    @Test
    fun list_sorting_works() {
        // Test that items are sorted correctly by listId then by name
        val items = listOf(
            Item(1, 2, "Item 2"),
            Item(2, 1, "Item 3"),
            Item(3, 1, "Item 1"),
            Item(4, 2, "Item 1")
        )

        val sorted = items.sortedWith(compareBy<Item> { it.listId }.thenBy { it.name })

        // Should be: (1,1), (1,3), (2,1), (2,2)
        assertEquals(1, sorted[0].listId)
        assertEquals("Item 1", sorted[0].name)
        assertEquals(1, sorted[1].listId)
        assertEquals("Item 3", sorted[1].name)
        assertEquals(2, sorted[2].listId)
        assertEquals("Item 1", sorted[2].name)
    }
}