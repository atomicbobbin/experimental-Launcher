/**
 * Widget Debugging Script
 * 
 * This script provides comprehensive debugging tools for the widget visibility issue.
 * Run this in the Android Studio debugger or add calls to MainActivity methods.
 */

package org.fossify.home.debug

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.view.View
import org.fossify.home.activities.MainActivity
import org.fossify.home.utils.WidgetDebugger
import org.fossify.home.views.HomeScreenGrid
import org.fossify.home.views.MyAppWidgetHostView

object WidgetDebugScript {
    private const val TAG = "WidgetDebugScript"
    
    /**
     * Run comprehensive widget debugging
     */
    fun runFullWidgetDebug(activity: MainActivity) {
        Log.d(TAG, "=== STARTING COMPREHENSIVE WIDGET DEBUG ===")
        
        try {
            // 1. Check widget host status
            Log.d(TAG, "Step 1: Checking widget host status")
            WidgetDebugger.logWidgetHostStatus(activity)
            
            // 2. Get home screen grid
            val homeScreenGrid = getHomeScreenGrid(activity)
            if (homeScreenGrid == null) {
                Log.e(TAG, "Could not access home screen grid")
                return
            }
            
            // 3. Check grid items
            Log.d(TAG, "Step 2: Checking grid items")
            WidgetDebugger.logGridItemsStatus(homeScreenGrid)
            
            // 4. Check widget views
            Log.d(TAG, "Step 3: Checking widget views")
            WidgetDebugger.logWidgetViewsStatus(homeScreenGrid)
            
            // 5. Enable debug visualization
            Log.d(TAG, "Step 4: Enabling debug visualization")
            activity.enableWidgetDebugVisualization()
            
            // 6. Force widget visibility check
            Log.d(TAG, "Step 5: Forcing widget visibility check")
            WidgetDebugger.forceWidgetVisibilityCheck(homeScreenGrid)
            
            // 7. Perform rendering diagnostic
            Log.d(TAG, "Step 6: Performing rendering diagnostic")
            WidgetDebugger.performWidgetRenderingDiagnostic(homeScreenGrid)
            
            // 8. Check view hierarchy
            Log.d(TAG, "Step 7: Checking view hierarchy")
            WidgetDebugger.logViewHierarchy(homeScreenGrid)
            
            // 9. Test widget providers
            Log.d(TAG, "Step 8: Testing widget providers")
            testWidgetProviders(activity)
            
            Log.d(TAG, "=== WIDGET DEBUG COMPLETE ===")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during widget debug", e)
        }
    }
    
    /**
     * Test different widget providers to identify compatibility issues
     */
    private fun testWidgetProviders(context: Context) {
        Log.d(TAG, "=== TESTING WIDGET PROVIDERS ===")
        
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val installedProviders = appWidgetManager.installedProviders
        
        Log.d(TAG, "Total installed providers: ${installedProviders.size}")
        
        installedProviders.forEach { provider ->
            Log.d(TAG, "Provider: ${provider.provider.className}")
            Log.d(TAG, "  Package: ${provider.provider.packageName}")
            Log.d(TAG, "  Min size: ${provider.minWidth}x${provider.minHeight}")
            Log.d(TAG, "  Resize mode: ${provider.resizeMode}")
            Log.d(TAG, "  Configure: ${provider.configure?.className}")
            Log.d(TAG, "  Preview: ${provider.previewImage}")
            Log.d(TAG, "  Label: ${provider.loadLabel(context.packageManager)}")
        }
    }
    
    /**
     * Get home screen grid through reflection
     */
    private fun getHomeScreenGrid(activity: MainActivity): HomeScreenGrid? {
        return try {
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
            null
        }
    }
    
    /**
     * Test widget rendering with different approaches
     */
    fun testWidgetRenderingApproaches(activity: MainActivity) {
        Log.d(TAG, "=== TESTING WIDGET RENDERING APPROACHES ===")
        
        val homeScreenGrid = getHomeScreenGrid(activity)
        if (homeScreenGrid == null) return
        
        homeScreenGrid.publicWidgetViews.forEach { widgetView ->
            if (widgetView is MyAppWidgetHostView) {
                val widgetId = widgetView.tag as? Int ?: -1
                
                Log.d(TAG, "Testing rendering approaches for widget $widgetId")
                
                // Approach 1: Force redraw
                widgetView.invalidate()
                widgetView.requestLayout()
                
                // Approach 2: Force visibility
                widgetView.visibility = View.VISIBLE
                widgetView.alpha = 1.0f
                
                // Approach 3: Force background
                widgetView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                
                // Approach 4: Force child visibility
                for (i in 0 until widgetView.childCount) {
                    val child = widgetView.getChildAt(i)
                    child.visibility = View.VISIBLE
                    child.alpha = 1.0f
                }
                
                // Approach 5: Force parent to redraw
                val parent = widgetView.parent
                if (parent is View) {
                    parent.invalidate()
                    parent.requestLayout()
                }
                
                Log.d(TAG, "Applied all rendering approaches to widget $widgetId")
            }
        }
    }
    
    /**
     * Check for common widget rendering issues
     */
    fun checkCommonWidgetIssues(activity: MainActivity) {
        Log.d(TAG, "=== CHECKING COMMON WIDGET ISSUES ===")
        
        val homeScreenGrid = getHomeScreenGrid(activity)
        if (homeScreenGrid == null) return
        
        homeScreenGrid.publicWidgetViews.forEach { widgetView ->
            if (widgetView is MyAppWidgetHostView) {
                val widgetId = widgetView.tag as? Int ?: -1
                
                Log.d(TAG, "Checking widget $widgetId for common issues:")
                
                // Issue 1: Zero size
                if (widgetView.width == 0 || widgetView.height == 0) {
                    Log.w(TAG, "  ISSUE: Widget has zero size: ${widgetView.width}x${widgetView.height}")
                }
                
                // Issue 2: Zero alpha
                if (widgetView.alpha == 0f) {
                    Log.w(TAG, "  ISSUE: Widget has zero alpha")
                }
                
                // Issue 3: Not visible
                if (widgetView.visibility != View.VISIBLE) {
                    Log.w(TAG, "  ISSUE: Widget is not visible: ${widgetView.visibility}")
                }
                
                // Issue 4: No children
                if (widgetView.childCount == 0) {
                    Log.w(TAG, "  ISSUE: Widget has no children")
                }
                
                // Issue 5: Children with zero size
                for (i in 0 until widgetView.childCount) {
                    val child = widgetView.getChildAt(i)
                    if (child.width == 0 || child.height == 0) {
                        Log.w(TAG, "  ISSUE: Child $i has zero size: ${child.width}x${child.height}")
                    }
                    if (child.alpha == 0f) {
                        Log.w(TAG, "  ISSUE: Child $i has zero alpha")
                    }
                    if (child.visibility != View.VISIBLE) {
                        Log.w(TAG, "  ISSUE: Child $i is not visible: ${child.visibility}")
                    }
                }
                
                // Issue 6: Parent issues
                val parent = widgetView.parent
                if (parent is View) {
                    if (parent.visibility != View.VISIBLE) {
                        Log.w(TAG, "  ISSUE: Parent is not visible: ${parent.visibility}")
                    }
                    if (parent.alpha == 0f) {
                        Log.w(TAG, "  ISSUE: Parent has zero alpha")
                    }
                }
                
                Log.d(TAG, "  Widget $widgetId check complete")
            }
        }
    }
}
