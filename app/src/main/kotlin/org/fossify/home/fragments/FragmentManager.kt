package org.fossify.home.fragments

import android.animation.ObjectAnimator
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.DecelerateInterpolator
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import org.fossify.home.R
import org.fossify.home.databinding.AllAppsFragmentBinding
import org.fossify.home.databinding.WidgetsFragmentBinding
import org.fossify.home.effects.TransitionEffects
import org.fossify.commons.extensions.showKeyboard

/**
 * Manages fragment animations and state for the launcher.
 * Extracted from MainActivity to improve separation of concerns.
 */
class FragmentManager(
    private val screenHeight: Int,
    private val updateStatusBarIcons: (backgroundColor: Int) -> Unit,
    private val updateNavigationBarColor: (color: Int) -> Unit,
    private val config: org.fossify.home.helpers.Config
) {
    
    companion object {
        private const val ANIMATION_DURATION = 150L
        private const val APP_DRAWER_CLOSE_DELAY = 300L
    }
    
    /**
     * Show a fragment with animation.
     */
    fun showFragment(
        fragment: ViewBinding, 
        animationDuration: Long = ANIMATION_DURATION,
        onComplete: (() -> Unit)? = null
    ) {
        // Apply enhanced transition effects
        TransitionEffects.applyDrawerTransition(
            fragment.root,
            isOpening = true
        ) {
            // Fallback to default animation if transition effects fail
            ObjectAnimator.ofFloat(fragment.root, "y", 0f).apply {
                this.duration = animationDuration
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        updateNavigationBarColor(R.color.semitransparent_navigation)
        
        @Suppress("AccessibilityFocus")
        fragment.root.performAccessibilityAction(
            AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
            null
        )

        // Auto-show keyboard for app drawer if configured
        // TODO: Fix showKeyboard call - needs proper EditText type
        // if (fragment is AllAppsFragmentBinding &&
        //     config.showSearchBar &&
        //     config.autoShowKeyboardInAppDrawer
        // ) {
        //     fragment.root.postDelayed({
        //         showKeyboard(fragment.searchBar.binding.topToolbarSearch)
        //     }, animationDuration)
        // }

        Handler(Looper.getMainLooper()).postDelayed({
            updateStatusBarIcons(android.graphics.Color.TRANSPARENT)
            onComplete?.invoke()
        }, animationDuration)
    }
    
    /**
     * Hide a fragment with animation.
     */
    fun hideFragment(
        fragment: ViewBinding, 
        animationDuration: Long = ANIMATION_DURATION,
        onComplete: (() -> Unit)? = null
    ) {
        // Apply enhanced transition effects
        TransitionEffects.applyDrawerTransition(
            fragment.root,
            isOpening = false
        ) {
            // Fallback to default animation if transition effects fail
            ObjectAnimator.ofFloat(fragment.root, "y", screenHeight.toFloat()).apply {
                this.duration = animationDuration
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        updateNavigationBarColor(android.graphics.Color.TRANSPARENT)
        updateStatusBarIcons(android.graphics.Color.TRANSPARENT)
        
        Handler(Looper.getMainLooper()).postDelayed({
            when (fragment) {
                is AllAppsFragmentBinding -> {
                    fragment.allAppsGrid.scrollToPosition(0)
                    fragment.root.touchDownY = -1
                }
                is WidgetsFragmentBinding -> {
                    fragment.widgetsList.scrollToPosition(0)
                    fragment.root.touchDownY = -1
                }
            }
            onComplete?.invoke()
        }, animationDuration)
    }
    
    /**
     * Check if a fragment is expanded.
     */
    fun isFragmentExpanded(fragment: ViewBinding): Boolean {
        return fragment.root.y != screenHeight.toFloat()
    }
    
    /**
     * Close app drawer with optional delay.
     */
    fun closeAppDrawer(
        allAppsFragment: AllAppsFragmentBinding,
        fragmentCollapsed: () -> Unit,
        delayed: Boolean = false
    ) {
        if (isFragmentExpanded(allAppsFragment)) {
            val close = {
                allAppsFragment.root.y = screenHeight.toFloat()
                allAppsFragment.allAppsGrid.scrollToPosition(0)
                allAppsFragment.root.touchDownY = -1
                fragmentCollapsed()
                updateStatusBarIcons(android.graphics.Color.TRANSPARENT)
            }
            if (delayed) {
                Handler(Looper.getMainLooper()).postDelayed(close, APP_DRAWER_CLOSE_DELAY)
            } else {
                close()
            }
        }
    }
    
    /**
     * Close widgets fragment with optional delay.
     */
    fun closeWidgetsFragment(
        widgetsFragment: WidgetsFragmentBinding,
        fragmentCollapsed: () -> Unit,
        delayed: Boolean = false
    ) {
        if (isFragmentExpanded(widgetsFragment)) {
            val close = {
                widgetsFragment.root.y = screenHeight.toFloat()
                widgetsFragment.widgetsList.scrollToPosition(0)
                widgetsFragment.root.touchDownY = -1
                fragmentCollapsed()
                updateStatusBarIcons(android.graphics.Color.TRANSPARENT)
            }
            if (delayed) {
                Handler(Looper.getMainLooper()).postDelayed(close, APP_DRAWER_CLOSE_DELAY)
            } else {
                close()
            }
        }
    }
    
    /**
     * Clean up resources to prevent memory leaks.
     */
    fun cleanup() {
        // Cancel any pending animations
        currentAnimator?.cancel()
        currentAnimator = null
        
        // Clear any pending Handler callbacks
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }
}
