package org.fossify.home.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import org.fossify.home.activities.MainActivity
import org.fossify.home.models.HomeScreenGridItem
import org.fossify.home.views.HomeScreenGrid
import org.fossify.home.views.MyAppWidgetHostView

object WidgetDebugger {
    private const val TAG = "WidgetDebugger"
    
    fun logWidgetHostStatus(context: Context) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val installedProviders = appWidgetManager.installedProviders
            
            Log.d(TAG, "=== WIDGET HOST STATUS ===")
            Log.d(TAG, "Total installed widget providers: ${installedProviders.size}")
            
            installedProviders.forEach { provider ->
                Log.d(TAG, "Provider: ${provider.provider.className} - ${provider.provider.packageName}")
                Log.d(TAG, "  Min width: ${provider.minWidth}, Min height: ${provider.minHeight}")
                Log.d(TAG, "  Resize mode: ${provider.resizeMode}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting widget host status", e)
        }
    }
    
    fun logWidgetViewsStatus(homeScreenGrid: HomeScreenGrid) {
        Log.d(TAG, "=== WIDGET VIEWS STATUS ===")
        Log.d(TAG, "Total widget views: ${homeScreenGrid.publicWidgetViews.size}")
        
        homeScreenGrid.publicWidgetViews.forEach { widgetView ->
            if (widgetView is MyAppWidgetHostView) {
                val widgetId = widgetView.tag as? Int ?: -1
                val visibility = when (widgetView.visibility) {
                    View.VISIBLE -> "VISIBLE"
                    View.INVISIBLE -> "INVISIBLE"
                    View.GONE -> "GONE"
                    else -> "UNKNOWN"
                }
                
                Log.d(TAG, "Widget ID: $widgetId")
                Log.d(TAG, "  Visibility: $visibility")
                Log.d(TAG, "  Position: (${widgetView.x}, ${widgetView.y})")
                Log.d(TAG, "  Size: ${widgetView.width}x${widgetView.height}")
                Log.d(TAG, "  LayoutParams: ${widgetView.layoutParams?.width}x${widgetView.layoutParams?.height}")
                Log.d(TAG, "  Alpha: ${widgetView.alpha}")
                Log.d(TAG, "  IsEnabled: ${widgetView.isEnabled}")
                Log.d(TAG, "  IsClickable: ${widgetView.isClickable}")
            }
        }
    }
    
    fun logGridItemsStatus(homeScreenGrid: HomeScreenGrid) {
        Log.d(TAG, "=== GRID ITEMS STATUS ===")
        val widgetItems = homeScreenGrid.publicGridItems.filter { it.type == org.fossify.home.helpers.ITEM_TYPE_WIDGET }
        Log.d(TAG, "Total widget grid items: ${widgetItems.size}")
        
        widgetItems.forEach { item ->
            Log.d(TAG, "Widget Item ID: ${item.id}")
            Log.d(TAG, "  Widget ID: ${item.widgetId}")
            Log.d(TAG, "  Page: ${item.page}")
            Log.d(TAG, "  Position: (${item.getTopLeft(0).x}, ${item.getTopLeft(0).y})")
            Log.d(TAG, "  Size: ${item.getWidthInCells()}x${item.getHeightInCells()}")
            Log.d(TAG, "  ClassName: ${item.className}")
            Log.d(TAG, "  ProviderInfo: ${item.providerInfo?.provider?.className}")
            // Note: outOfBounds() is an extension function, skipping this check
        }
    }
    
    fun logViewHierarchy(view: View, depth: Int = 0, maxDepth: Int = 5) {
        if (depth > maxDepth) return
        
        val indent = "  ".repeat(depth)
        val className = view.javaClass.simpleName
        val visibility = when (view.visibility) {
            View.VISIBLE -> "VISIBLE"
            View.INVISIBLE -> "INVISIBLE"
            View.GONE -> "GONE"
            else -> "UNKNOWN"
        }
        
        Log.d(TAG, "$indent$className (vis: $visibility, size: ${view.width}x${view.height})")
        
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                logViewHierarchy(view.getChildAt(i), depth + 1, maxDepth)
            }
        }
    }
    
    fun performFullWidgetDiagnostic(activity: MainActivity) {
        Log.d(TAG, "=== FULL WIDGET DIAGNOSTIC ===")
        
        try {
            // Check widget host status
            logWidgetHostStatus(activity)
            
            // Get home screen grid through reflection to avoid private access
            val homeScreenGrid = try {
                val bindingField = activity.javaClass.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity)
                val homeScreenGridField = binding.javaClass.getDeclaredField("homeScreenGrid")
                homeScreenGridField.isAccessible = true
                val homeScreenGridHolder = homeScreenGridField.get(binding)
                val rootField = homeScreenGridHolder.javaClass.getDeclaredField("root")
                rootField.isAccessible = true
                rootField.get(homeScreenGridHolder) as HomeScreenGrid
            } catch (e: Exception) {
                Log.e(TAG, "Could not access home screen grid: ${e.message}")
                return
            }
            
            // Check grid items
            logGridItemsStatus(homeScreenGrid)
            
            // Check widget views
            logWidgetViewsStatus(homeScreenGrid)
            
            // Check view hierarchy for widget-related views
            Log.d(TAG, "=== WIDGET VIEW HIERARCHY ===")
            logViewHierarchy(homeScreenGrid)
            
            // Check if widget host is listening
            val isListening = try {
                val host = homeScreenGrid.appWidgetHost
                val method = host.javaClass.getDeclaredMethod("isListening")
                method.isAccessible = true
                method.invoke(host) as Boolean
            } catch (e: Exception) {
                Log.d(TAG, "Could not check listening status: ${e.message}")
                false
            }
            Log.d(TAG, "Widget host is listening: $isListening")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during widget diagnostic", e)
        }
    }
    
    fun forceWidgetVisibilityCheck(homeScreenGrid: HomeScreenGrid) {
        Log.d(TAG, "=== FORCING WIDGET VISIBILITY CHECK ===")
        
        homeScreenGrid.publicWidgetViews.forEach { widgetView ->
            if (widgetView is MyAppWidgetHostView) {
                val widgetId = widgetView.tag as? Int ?: -1
                val currentVisibility = widgetView.visibility
                
                Log.d(TAG, "Widget $widgetId current visibility: $currentVisibility")
                
                // Force visibility to VISIBLE temporarily for testing
                widgetView.visibility = View.VISIBLE
                Log.d(TAG, "Widget $widgetId forced to VISIBLE")
                
                // Enable debug visualization
                widgetView.setDebugVisualization(true)
                
                // Check if widget has content
                widgetView.post {
                    val hasContent = widgetView.childCount > 0
                    Log.d(TAG, "Widget $widgetId has content (childCount > 0): $hasContent")
                    
                    if (hasContent) {
                        Log.d(TAG, "Widget $widgetId child views:")
                        for (i in 0 until widgetView.childCount) {
                            val child = widgetView.getChildAt(i)
                            Log.d(TAG, "  Child $i: ${child.javaClass.simpleName} (${child.width}x${child.height})")
                        }
                    }
                }
            }
        }
    }
    
    fun performWidgetRenderingDiagnostic(homeScreenGrid: HomeScreenGrid) {
        Log.d(TAG, "=== WIDGET RENDERING DIAGNOSTIC ===")
        
        homeScreenGrid.publicWidgetViews.forEach { widgetView ->
            if (widgetView is MyAppWidgetHostView) {
                val widgetId = widgetView.tag as? Int ?: -1
                
                Log.d(TAG, "Widget $widgetId rendering analysis:")
                Log.d(TAG, "  Visibility: ${widgetView.visibility}")
                Log.d(TAG, "  Alpha: ${widgetView.alpha}")
                Log.d(TAG, "  Size: ${widgetView.width}x${widgetView.height}")
                Log.d(TAG, "  Position: (${widgetView.x}, ${widgetView.y})")
                Log.d(TAG, "  LayoutParams: ${widgetView.layoutParams?.width}x${widgetView.layoutParams?.height}")
                Log.d(TAG, "  Background: ${widgetView.background}")
                Log.d(TAG, "  ChildCount: ${widgetView.childCount}")
                Log.d(TAG, "  IsEnabled: ${widgetView.isEnabled}")
                Log.d(TAG, "  IsClickable: ${widgetView.isClickable}")
                Log.d(TAG, "  IsFocusable: ${widgetView.isFocusable}")
                Log.d(TAG, "  IsDrawingCacheEnabled: ${widgetView.isDrawingCacheEnabled}")
                Log.d(TAG, "  WillNotDraw: ${widgetView.willNotDraw()}")
                Log.d(TAG, "  HasOverlappingRendering: ${widgetView.hasOverlappingRendering()}")
                
                // Check parent view properties
                val parent = widgetView.parent
                if (parent is ViewGroup) {
                    Log.d(TAG, "  Parent: ${parent.javaClass.simpleName}")
                    Log.d(TAG, "  Parent visibility: ${parent.visibility}")
                    Log.d(TAG, "  Parent alpha: ${parent.alpha}")
                    Log.d(TAG, "  Parent size: ${parent.width}x${parent.height}")
                }
                
                // Force widget to redraw
                widgetView.invalidate()
                widgetView.requestLayout()
                
                // Enable debug visualization
                widgetView.setDebugVisualization(true)
            }
        }
    }
}
