package org.fossify.home.touch

import android.view.MotionEvent
import org.fossify.home.models.HomeScreenGridItem

/**
 * Manages touch events and gesture state for the launcher.
 * Extracted from MainActivity to improve separation of concerns.
 */
class TouchEventManager {
    
    // Touch state variables
    var touchDownX = -1
    var touchDownY = -1
    var allAppsFragmentY = 0
    var widgetsFragmentY = 0
    var screenHeight = 0
    var moveGestureThreshold = 0
    var ignoreUpEvent = false
    var ignoreMoveEvents = false
    var ignoreXMoveEvents = false
    var ignoreYMoveEvents = false
    var longPressedIcon: HomeScreenGridItem? = null
    var lastTouchCoords = Pair(-1f, -1f)
    
    /**
     * Initialize touch state with screen dimensions and threshold.
     */
    fun initialize(screenHeight: Int, moveGestureThreshold: Int) {
        this.screenHeight = screenHeight
        this.moveGestureThreshold = moveGestureThreshold
    }
    
    /**
     * Handle touch down event.
     */
    fun onTouchDown(event: MotionEvent) {
        touchDownX = event.x.toInt()
        touchDownY = event.y.toInt()
        ignoreUpEvent = false
    }
    
    /**
     * Handle touch move event.
     */
    fun onTouchMove(event: MotionEvent): TouchMoveResult {
        // Fix initial gesture values if needed
        val hasFingerMoved = if (touchDownX == -1 || touchDownY == -1) {
            touchDownX = event.x.toInt()
            touchDownY = event.y.toInt()
            false
        } else {
            hasFingerMoved(event)
        }
        
        lastTouchCoords = Pair(event.x, event.y)
        
        return TouchMoveResult(
            hasFingerMoved = hasFingerMoved,
            diffX = touchDownX - event.x,
            diffY = touchDownY - event.y
        )
    }
    
    /**
     * Handle touch up/cancel event.
     */
    fun onTouchUp() {
        touchDownX = -1
        touchDownY = -1
        ignoreMoveEvents = false
        longPressedIcon = null
        lastTouchCoords = Pair(-1f, -1f)
        ignoreXMoveEvents = false
        ignoreYMoveEvents = false
    }
    
    /**
     * Check if finger has moved beyond threshold.
     */
    private fun hasFingerMoved(event: MotionEvent): Boolean {
        return touchDownX != -1 && touchDownY != -1 &&
                (kotlin.math.abs(touchDownX - event.x) > moveGestureThreshold || 
                 kotlin.math.abs(touchDownY - event.y) > moveGestureThreshold)
    }
    
    /**
     * Reset fragment touch states.
     */
    fun resetFragmentTouches() {
        // This will be called by the activity to reset fragment states
    }
    
    /**
     * Clean up resources to prevent memory leaks.
     */
    fun cleanup() {
        longPressedIcon = null
        lastTouchCoords = Pair(-1f, -1f)
        resetFragmentTouches()
    }
}

/**
 * Result of touch move processing.
 */
data class TouchMoveResult(
    val hasFingerMoved: Boolean,
    val diffX: Float,
    val diffY: Float
)
