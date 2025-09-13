package org.fossify.home.theme

import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import org.fossify.home.core.DeviceCapabilities
import org.fossify.home.data.SettingsRepository

class ThemeManager(
    private val context: Context,
    private val deviceCapabilities: DeviceCapabilities,
    private val settingsRepository: SettingsRepository
) {
    data class ThemeTokens(
        @ColorInt val surface: Int,
        @ColorInt val onSurface: Int,
        @ColorInt val primary: Int,
        @ColorInt val onPrimary: Int,
    )

    // Stub tokens; will be replaced with Monet or Palette extraction
    @Volatile
    private var _tokens: ThemeTokens = ThemeTokens(
        surface = 0xFF101010.toInt(),
        onSurface = 0xFFEFEFEF.toInt(),
        primary = 0xFF2E7D32.toInt(),
        onPrimary = 0xFFFFFFFF.toInt(),
    )

    val tokens: ThemeTokens get() = _tokens

    fun refresh() {
        if (settingsRepository.featureFlags.enableDynamicColor && deviceCapabilities.supportsDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use Material3 dynamic color when enabled
            _tokens = _tokens.copy(primary = _tokens.primary)
        } else {
            // TODO: Add Palette-based extraction from wallpaper as fallback
            _tokens = _tokens.copy(primary = _tokens.primary)
        }
    }

    fun adjustForContrast(@ColorInt base: Int): Int {
        val contrast = ColorUtils.calculateLuminance(base)
        return if (contrast > 0.5) 0xFF101010.toInt() else 0xFFEFEFEF.toInt()
    }
}


