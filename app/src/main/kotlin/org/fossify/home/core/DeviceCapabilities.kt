package org.fossify.home.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class DeviceCapabilities(private val context: Context) {
    val supportsDynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val supportsRenderEffectBlur: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val supportsThemedIcons: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val hasTelephony: Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

    // Rotation support is policy-driven; expose as capability to gate UI toggles
    val supportsHomeRotation: Boolean = true
}


