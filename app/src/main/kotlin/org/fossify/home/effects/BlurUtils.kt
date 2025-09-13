package org.fossify.home.effects

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import org.fossify.home.core.ServiceLocator

object BlurUtils {

    /**
     * Apply blur effect to a view if supported and enabled
     */
    fun applyBlurToView(view: View, intensity: Float = 15f) {
        val context = view.context
        val capabilities = ServiceLocator.deviceCapabilities
        val settings = ServiceLocator.settingsRepository

        if (!capabilities.supportsRenderEffectBlur || 
            !settings.featureFlags.enableBlur || 
            !settings.enableBlurEffects) {
            // Clear any existing blur effect
            clearBlurFromView(view)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applyRenderEffectBlur(view, intensity)
        } else {
            // Fallback to alpha-based translucency for older versions
            applyTranslucency(view)
        }
    }

    /**
     * Apply RenderEffect blur for Android 12+ (API 31+)
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyRenderEffectBlur(view: View, intensity: Float) {
        val blurEffect = RenderEffect.createBlurEffect(
            intensity, intensity, Shader.TileMode.CLAMP
        )
        view.setRenderEffect(blurEffect)
    }

    /**
     * Apply translucency effect as fallback
     */
    private fun applyTranslucency(view: View) {
        view.alpha = 0.85f
        view.setBackgroundColor(0x80000000.toInt()) // Semi-transparent black
    }

    /**
     * Clear blur/translucency effects from view
     */
    fun clearBlurFromView(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        }
        view.alpha = 1.0f
        view.background = null
    }

    /**
     * Apply blur to folder background
     */
    fun applyFolderBlur(view: View) {
        val settings = ServiceLocator.settingsRepository
        val intensity = settings.blurIntensity.toFloat()
        applyBlurToView(view, intensity)
    }

    /**
     * Apply blur to app drawer background
     */
    fun applyDrawerBlur(view: View) {
        val settings = ServiceLocator.settingsRepository
        val intensity = settings.blurIntensity.toFloat()
        applyBlurToView(view, intensity)
    }
}
