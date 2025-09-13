package org.fossify.home.icons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import org.fossify.home.data.SettingsRepository

object IconShapeMask {
    fun applyMask(context: Context, drawable: Drawable, settings: SettingsRepository): Drawable {
        val maskMode = settings.featureFlags // not used; placeholder for repository link if needed
        // Use app config via context if needed; for now, keep original drawable
        return drawable
    }
}


