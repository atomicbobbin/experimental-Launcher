package org.fossify.home.icons

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import org.fossify.home.core.DeviceCapabilities
import org.fossify.home.data.SettingsRepository
import org.fossify.home.extensions.getDrawableForPackageName
import org.fossify.home.models.AppLauncher

class IconResolver(
    private val context: Context,
    private val capabilities: DeviceCapabilities,
    private val settings: SettingsRepository
) {
    data class ResolvedIcon(val appLauncher: AppLauncher?)

    fun loadAllLaunchers(hiddenIdentifiers: Set<String>): List<AppLauncher> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED)
        val simpleLauncher = context.packageName
        val microG = "com.google.android.gms"

        val out = ArrayList<AppLauncher>()
        for (info in list) {
            val componentInfo = info.activityInfo.applicationInfo
            val packageName = componentInfo.packageName
            if (packageName == simpleLauncher || packageName == microG) continue

            val activityName = info.activityInfo.name
            val identifier = "$packageName/$activityName"
            if (hiddenIdentifiers.contains(identifier)) continue

            val label = info.loadLabel(pm).toString()
            var drawable = info.loadIcon(pm) ?: context.getDrawableForPackageName(packageName) ?: continue
            // Apply icon shape mask (simple placeholder implementation)
            drawable = IconShapeMask.applyMask(context, drawable, settings)
            // Apply themed icons on Android 13+ when enabled and monochrome available
            drawable = ThemedIconTint.applyIfSupported(context, drawable, settings)
            val bitmap = drawable.toBitmap(
                width = kotlin.math.max(drawable.intrinsicWidth, 1),
                height = kotlin.math.max(drawable.intrinsicHeight, 1)
            )
            val placeholderColor = bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)
            out.add(
                AppLauncher(
                    id = null,
                    title = label,
                    packageName = packageName,
                    activityName = activityName,
                    order = 0,
                    thumbnailColor = placeholderColor,
                    drawable = bitmap.toDrawable(context.resources)
                )
            )
        }
        return out
    }
}


