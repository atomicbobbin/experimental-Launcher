package org.fossify.home.views

import android.content.Context
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.test.core.app.ApplicationProvider
import org.fossify.home.helpers.ITEM_TYPE_ICON
import org.fossify.home.models.HomeScreenGridItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HomeScreenGridAccessibilityTest {

    private lateinit var context: Context
    private lateinit var homeScreenGrid: HomeScreenGrid
    private lateinit var accessibilityHelper: HomeScreenGrid.AccessibilityHelper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        homeScreenGrid = HomeScreenGrid(context, null, 0)
        accessibilityHelper = homeScreenGrid.AccessibilityHelper()
    }

    @Test
    fun `onPopulateNodeForVirtualView handles unknown virtual view ID gracefully`() {
        // Given
        val node = AccessibilityNodeInfoCompat.obtain()
        val unknownVirtualViewId = 99999

        // When
        accessibilityHelper.onPopulateNodeForVirtualView(unknownVirtualViewId, node)

        // Then
        assertEquals("Unknown item", node.text)
        assertNotNull(node.boundsInScreen)
        assertNotNull(node.boundsInParent)
    }

    @Test
    fun `onPopulateNodeForVirtualView handles home screen virtual view ID correctly`() {
        // Given
        val node = AccessibilityNodeInfoCompat.obtain()
        val homeScreenVirtualViewId = -1

        // When
        accessibilityHelper.onPopulateNodeForVirtualView(homeScreenVirtualViewId, node)

        // Then
        assertNotNull(node.text)
        assertTrue(node.actions and AccessibilityNodeInfoCompat.ACTION_CLICK != 0)
        assertTrue(node.actions and AccessibilityNodeInfoCompat.ACTION_LONG_CLICK != 0)
    }

    @Test
    fun `onPopulateNodeForVirtualView handles valid grid item correctly`() {
        // Given
        val node = AccessibilityNodeInfoCompat.obtain()
        val gridItem = HomeScreenGridItem(
            id = 1L,
            left = 0,
            top = 0,
            right = 0,
            bottom = 0,
            page = 0,
            packageName = "com.test.app",
            activityName = "TestActivity",
            title = "Test App",
            type = ITEM_TYPE_ICON,
            className = "TestClass",
            widgetId = -1,
            shortcutId = "",
            icon = null,
            docked = false,
            parentId = null
        )
        
        // Add the item to the grid items list
        homeScreenGrid.gridItems.add(gridItem)

        // When
        accessibilityHelper.onPopulateNodeForVirtualView(1, node)

        // Then
        assertEquals("Test App", node.text)
        assertTrue(node.actions and AccessibilityNodeInfoCompat.ACTION_CLICK != 0)
        assertTrue(node.actions and AccessibilityNodeInfoCompat.ACTION_LONG_CLICK != 0)
    }

    @Test
    fun `onPerformActionForVirtualView handles unknown virtual view ID gracefully`() {
        // Given
        val unknownVirtualViewId = 99999
        val action = AccessibilityNodeInfoCompat.ACTION_CLICK

        // When
        val result = accessibilityHelper.onPerformActionForVirtualView(
            unknownVirtualViewId, 
            action, 
            null
        )

        // Then
        assertFalse(result)
    }

    @Test
    fun `onPerformActionForVirtualView handles valid action correctly`() {
        // Given
        val gridItem = HomeScreenGridItem(
            id = 1L,
            left = 0,
            top = 0,
            right = 0,
            bottom = 0,
            page = 0,
            packageName = "com.test.app",
            activityName = "TestActivity",
            title = "Test App",
            type = ITEM_TYPE_ICON,
            className = "TestClass",
            widgetId = -1,
            shortcutId = "",
            icon = null,
            docked = false,
            parentId = null
        )
        
        // Add the item to the grid items list
        homeScreenGrid.gridItems.add(gridItem)
        
        // Set up click listener
        var clickHandled = false
        homeScreenGrid.itemClickListener = { clickHandled = true }

        // When
        val result = accessibilityHelper.onPerformActionForVirtualView(
            1, 
            AccessibilityNodeInfoCompat.ACTION_CLICK, 
            null
        )

        // Then
        assertTrue(result)
        assertTrue(clickHandled)
    }

    @Test
    fun `onPerformActionForVirtualView handles exceptions gracefully`() {
        // Given
        val gridItem = HomeScreenGridItem(
            id = 1L,
            left = 0,
            top = 0,
            right = 0,
            bottom = 0,
            page = 0,
            packageName = "com.test.app",
            activityName = "TestActivity",
            title = "Test App",
            type = ITEM_TYPE_ICON,
            className = "TestClass",
            widgetId = -1,
            shortcutId = "",
            icon = null,
            docked = false,
            parentId = null
        )
        
        // Add the item to the grid items list
        homeScreenGrid.gridItems.add(gridItem)
        
        // Set up click listener that throws exception
        homeScreenGrid.itemClickListener = { throw RuntimeException("Test exception") }

        // When
        val result = accessibilityHelper.onPerformActionForVirtualView(
            1, 
            AccessibilityNodeInfoCompat.ACTION_CLICK, 
            null
        )

        // Then
        assertFalse(result) // Should return false when exception occurs
    }
}
