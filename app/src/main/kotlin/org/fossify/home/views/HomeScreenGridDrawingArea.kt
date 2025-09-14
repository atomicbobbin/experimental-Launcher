package org.fossify.home.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.app.WallpaperManager

class HomeScreenGridDrawingArea @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var wallpaperDrawable: Drawable? = null
    private var isUsingFallbackBackground = false

    init {
        // Make this view transparent so wallpaper shows through
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        // Load wallpaper during initialization
        loadWallpaper()
    }

    private fun loadWallpaper() {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            
            // Try regular drawable first
            wallpaperDrawable = wallpaperManager.drawable
            if (wallpaperDrawable != null) {
                Log.d("WallpaperDebug", "Wallpaper loaded successfully using regular drawable")
                isUsingFallbackBackground = false
            } else {
                Log.w("WallpaperDebug", "Regular drawable is null, trying fastDrawable")
                // Fallback to fastDrawable
                wallpaperDrawable = wallpaperManager.fastDrawable
                if (wallpaperDrawable != null) {
                    Log.d("WallpaperDebug", "Wallpaper loaded successfully using fastDrawable")
                    isUsingFallbackBackground = false
                } else {
                    Log.w("WallpaperDebug", "Both drawable and fastDrawable are null")
                    isUsingFallbackBackground = true
                }
            }
            
            if (wallpaperDrawable != null) {
                Log.d("WallpaperDebug", "Wallpaper bounds: ${wallpaperDrawable!!.bounds}")
                Log.d("WallpaperDebug", "Wallpaper intrinsic size: ${wallpaperDrawable!!.intrinsicWidth}x${wallpaperDrawable!!.intrinsicHeight}")
            }
        } catch (e: SecurityException) {
            Log.w("WallpaperDebug", "Security exception loading wallpaper: ${e.message}")
            // Don't use fallback background - let FLAG_SHOW_WALLPAPER handle it
            wallpaperDrawable = null
            isUsingFallbackBackground = true
            Log.d("WallpaperDebug", "Using FLAG_SHOW_WALLPAPER for background")
        } catch (e: Exception) {
            Log.w("WallpaperDebug", "Error loading wallpaper: ${e.message}")
            // Don't use fallback background - let FLAG_SHOW_WALLPAPER handle it
            wallpaperDrawable = null
            isUsingFallbackBackground = true
            Log.d("WallpaperDebug", "Using FLAG_SHOW_WALLPAPER for background")
        }
    }

    private fun createGradientBackground(): Drawable {
        return android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(0xFF1E3A8A.toInt(), 0xFF1E40AF.toInt(), 0xFF1D4ED8.toInt())
        )
    }

    override fun onDraw(canvas: Canvas) {
        // Only draw wallpaper background if we successfully loaded it (not fallback)
        // If wallpaper loading failed, let FLAG_SHOW_WALLPAPER handle the background
        if (!isUsingFallbackBackground && wallpaperDrawable != null) {
            wallpaperDrawable!!.setBounds(0, 0, width, height)
            wallpaperDrawable!!.draw(canvas)
        }
        
        // Draw the grid content on top
        (parent as HomeScreenGrid).drawInto(canvas)
    }

    fun refreshWallpaper() {
        loadWallpaper()
        invalidate()
    }
}
