package org.fossify.home.icons

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.color.MaterialColors
import org.fossify.home.data.SettingsRepository

object ThemedIconTint {
    fun applyIfSupported(context: Context, drawable: Drawable, settings: SettingsRepository): Drawable {
        if (!settings.featureFlags.enableThemedIcons) return drawable
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return drawable
        
        try {
            val wrap = DrawableCompat.wrap(drawable.mutate())
            val color = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, 0xFF2E7D32.toInt())
            DrawableCompat.setTintMode(wrap, PorterDuff.Mode.SRC_IN)
            DrawableCompat.setTint(wrap, color)
            return wrap
        } catch (e: Exception) {
            // If themed icon tinting fails, return original drawable
            return drawable
        }
    }
}


