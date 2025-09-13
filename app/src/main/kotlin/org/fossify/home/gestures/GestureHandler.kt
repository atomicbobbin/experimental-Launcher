package org.fossify.home.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import org.fossify.home.interfaces.FlingListener

/**
 * Handles gesture detection and routing for the launcher.
 * Extracted from MainActivity to improve separation of concerns.
 */
class GestureHandler(
    private val context: Context,
    private val flingListener: FlingListener
) : GestureDetector.SimpleOnGestureListener() {
    
    private var lastUpEvent = 0L
    
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        if (flingListener is GestureHandlerCallback) {
            flingListener.homeScreenClicked(event.x, event.y)
        }
        return super.onSingleTapUp(event)
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        if (flingListener is GestureHandlerCallback) {
            flingListener.homeScreenDoubleTapped(event.x, event.y)
        }
        return super.onDoubleTap(event)
    }

    override fun onFling(
        event1: MotionEvent?,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        // ignore fling events just after releasing an icon from dragging
        if (System.currentTimeMillis() - lastUpEvent < 500L) {
            return true
        }

        if (kotlin.math.abs(velocityY) > kotlin.math.abs(velocityX)) {
            if (velocityY > 0) {
                flingListener.onFlingDown()
            } else {
                flingListener.onFlingUp()
            }
        } else if (kotlin.math.abs(velocityX) > kotlin.math.abs(velocityY)) {
            if (velocityX > 0) {
                flingListener.onFlingRight()
            } else {
                flingListener.onFlingLeft()
            }
        }

        return true
    }

    override fun onLongPress(event: MotionEvent) {
        if (flingListener is GestureHandlerCallback) {
            flingListener.homeScreenLongPressed(event.x, event.y)
        }
    }
    
    fun updateLastUpEvent() {
        lastUpEvent = System.currentTimeMillis()
    }
}

/**
 * Callback interface for gesture events that need additional context.
 */
interface GestureHandlerCallback {
    fun homeScreenClicked(eventX: Float, eventY: Float)
    fun homeScreenDoubleTapped(eventX: Float, eventY: Float)
    fun homeScreenLongPressed(eventX: Float, eventY: Float)
}
