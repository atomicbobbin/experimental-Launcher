package org.fossify.home.views

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import org.fossify.home.R
import kotlin.math.abs

class MyAppWidgetHostView(context: Context) : AppWidgetHostView(context) {
    private var longPressHandler = Handler()
    private var actionDownCoords = PointF()
    private var currentCoords = PointF()
    private var actionDownMS = 0L
    private val moveGestureThreshold = resources.getDimension(R.dimen.move_gesture_threshold).toInt() / 4
    var hasLongPressed = false
    var ignoreTouches = false
    var longPressListener: ((x: Float, y: Float) -> Unit)? = null
    var onIgnoreInterceptedListener: (() -> Unit)? = null       // let the home grid react on swallowed clicks, for example by hiding the widget resize frame
    
    init {
        // CRITICAL FIX: Enable drawing for widget host views
        setWillNotDraw(false)
        Log.d("WidgetDebug", "MyAppWidgetHostView.init() - setWillNotDraw(false) called")
    }
    
    // Debug visualization
    private val debugPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }
    private val debugTextPaint = Paint().apply {
        color = Color.RED
        textSize = 24f
        isAntiAlias = true
    }
    private var showDebugVisualization = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (ignoreTouches) {
            onIgnoreInterceptedListener?.invoke()
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (ignoreTouches || event == null) {
            return true
        }

        if (hasLongPressed) {
            hasLongPressed = false
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                longPressHandler.postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
                actionDownCoords.x = event.rawX
                actionDownCoords.y = event.rawY
                currentCoords.x = event.rawX
                currentCoords.y = event.rawY
                actionDownMS = System.currentTimeMillis()
            }

            MotionEvent.ACTION_MOVE -> {
                currentCoords.x = event.rawX
                currentCoords.y = event.rawY
                if (abs(actionDownCoords.x - currentCoords.x) > moveGestureThreshold) {
                    resetTouches()
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                resetTouches()
            }
        }

        return false
    }

    private val longPressRunnable = Runnable {
        if (abs(actionDownCoords.x - currentCoords.x) < moveGestureThreshold && abs(actionDownCoords.y - currentCoords.y) < moveGestureThreshold) {
            longPressHandler.removeCallbacksAndMessages(null)
            hasLongPressed = true
            longPressListener?.invoke(actionDownCoords.x, actionDownCoords.y)
        }
    }

    fun resetTouches() {
        longPressHandler.removeCallbacksAndMessages(null)
    }

    private fun hasFingerMoved(x: Float, y: Float) =
        ((abs(actionDownCoords.x - x) > moveGestureThreshold) || (abs(actionDownCoords.y - y) > moveGestureThreshold))
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Always log when onDraw is called
        val widgetId = tag as? Int ?: -1
        Log.d("WidgetDebug", "MyAppWidgetHostView.onDraw() CALLED - Widget $widgetId: " +
                "Size=${width}x${height}, Alpha=$alpha, ChildCount=$childCount, Position=($x, $y)")
        
        if (showDebugVisualization) {
            val visibility = when (visibility) {
                VISIBLE -> "VISIBLE"
                INVISIBLE -> "INVISIBLE"
                GONE -> "GONE"
                else -> "UNKNOWN"
            }
            
            // Draw red border around widget
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), debugPaint)
            
            // Draw debug text
            val debugText = "Widget $widgetId\n$visibility\n${width}x${height}\nAlpha: $alpha\nChildren: $childCount"
            canvas.drawText(debugText, 10f, 30f, debugTextPaint)
            
            // Log detailed debug info
            Log.d("WidgetDebug", "MyAppWidgetHostView.onDraw() - Widget $widgetId: " +
                    "Visibility=$visibility, Size=${width}x${height}, Alpha=$alpha, " +
                    "ChildCount=$childCount, Position=($x, $y)")
            
            // Check child views
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                Log.d("WidgetDebug", "  Child $i: ${child.javaClass.simpleName}, " +
                        "Visibility=${child.visibility}, Size=${child.width}x${child.height}, " +
                        "Alpha=${child.alpha}, Position=(${child.x}, ${child.y})")
            }
        }
    }
    
    fun setDebugVisualization(enabled: Boolean) {
        showDebugVisualization = enabled
        invalidate()
    }
    
    fun forceRedraw() {
        Log.d("WidgetDebug", "MyAppWidgetHostView.forceRedraw() - Widget ${tag}")
        invalidate()
        requestLayout()
        post {
            Log.d("WidgetDebug", "MyAppWidgetHostView.forceRedraw() POST - Widget ${tag}: " +
                    "Size=${width}x${height}, Alpha=$alpha, ChildCount=$childCount")
        }
    }
}
