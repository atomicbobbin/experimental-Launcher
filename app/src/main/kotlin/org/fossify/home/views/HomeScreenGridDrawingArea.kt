package org.fossify.home.views

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

    init {
        loadWallpaper()
    }

    private fun loadWallpaper() {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperDrawable = wallpaperManager.drawable
            Log.d("Wallpaper", "Wallpaper loaded successfully")
        } catch (e: SecurityException) {
            Log.w("Wallpaper", "Security exception loading wallpaper: ${e.message}")
            // Use a gradient background as fallback
            wallpaperDrawable = createGradientBackground()
            Log.d("Wallpaper", "Using gradient fallback background")
        } catch (e: Exception) {
            Log.w("Wallpaper", "Error loading wallpaper: ${e.message}")
            // Use a gradient background as fallback
            wallpaperDrawable = createGradientBackground()
            Log.d("Wallpaper", "Using gradient fallback background")
        }
    }

    private fun createGradientBackground(): Drawable {
        return android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(0xFF1E3A8A.toInt(), 0xFF1E40AF.toInt(), 0xFF1D4ED8.toInt())
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw wallpaper if available
        wallpaperDrawable?.let { wallpaper ->
            wallpaper.setBounds(0, 0, width, height)
            wallpaper.draw(canvas)
            Log.d("Wallpaper", "Drawing wallpaper: ${width}x${height}")
        } ?: run {
            Log.w("Wallpaper", "No wallpaper drawable available")
        }
        
        // Draw grid content on top of wallpaper
        (parent as HomeScreenGrid).drawInto(canvas)
    }

    fun refreshWallpaper() {
        loadWallpaper()
        invalidate()
    }
}
