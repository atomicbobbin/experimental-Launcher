package org.fossify.home.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Process
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.home.extensions.config
import org.fossify.home.extensions.getDrawableForPackageName
import org.fossify.home.extensions.homeScreenGridItemsDB
import org.fossify.home.extensions.launchersDB
import org.fossify.home.helpers.ITEM_TYPE_ICON
import org.fossify.home.models.HomeScreenGridItem

class PackageEventsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_PACKAGE_ADDED || intent.data == null) return
        if (!context.config.autoAddNewApps || context.config.lockHomeLayout) return

        val packageName = intent.data?.schemeSpecificPart ?: return

        ensureBackgroundThread {
            val launchers = context.launchersDB.getAppLaunchers()
            val matching = launchers.filter { it.packageName == packageName }
            if (matching.isEmpty()) return@ensureBackgroundThread

            val existing = context.homeScreenGridItemsDB.getAllItems()
            val (page, cellX, cellY) = findFirstFreeCell(context, existing)

            matching.forEach { launcher ->
                val iconDrawable = launcher.drawable ?: context.getDrawableForPackageName(packageName)
                val iconBitmap = iconDrawable?.toBitmap()
                val item = HomeScreenGridItem(
                    id = null,
                    left = cellX,
                    top = cellY,
                    right = cellX,
                    bottom = cellY,
                    page = page,
                    packageName = launcher.packageName,
                    activityName = launcher.activityName,
                    title = launcher.title,
                    type = ITEM_TYPE_ICON,
                    className = "",
                    widgetId = -1,
                    shortcutId = "",
                    icon = iconBitmap,
                    docked = false,
                    parentId = null,
                    drawable = iconBitmap?.toDrawable(context.resources),
                    providerInfo = null,
                    activityInfo = null
                )
                context.homeScreenGridItemsDB.insert(item)
            }
        }
    }

    private fun findFirstFreeCell(
        context: Context,
        existing: List<HomeScreenGridItem>
    ): Triple<Int, Int, Int> {
        val rows = context.config.homeRowCount
        val cols = context.config.homeColumnCount
        val occupied = existing.filter { it.parentId == null && !it.docked }
            .groupBy { it.page }
            .mapValues { (_, items) ->
                items.flatMap { item -> (item.left..item.right).map { x -> Pair(x, item.top) } }.toSet()
            }

        var page = 0
        while (true) {
            val occ = occupied[page] ?: emptySet()
            for (y in 0 until rows - 1) { // leave last row for dock
                for (x in 0 until cols) {
                    if (!occ.contains(Pair(x, y))) return Triple(page, x, y)
                }
            }
            page++
        }
    }
}


