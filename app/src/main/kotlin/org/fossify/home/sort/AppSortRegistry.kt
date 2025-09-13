package org.fossify.home.sort

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.format.DateUtils
import org.fossify.home.models.AppLauncher

enum class AppSortMode { Alphabetical, MostUsed, RecentlyInstalled }

interface AppSorter {
    val id: AppSortMode
    fun sort(context: Context, apps: List<AppLauncher>): List<AppLauncher>
}

class AppSortRegistry(private val context: Context) {
    private val sorters = linkedMapOf<AppSortMode, AppSorter>()

    init {
        register(AlphabeticalSorter())
        register(MostUsedSorter())
        register(RecentlyInstalledSorter())
    }

    fun register(sorter: AppSorter) {
        sorters[sorter.id] = sorter
    }

    fun sort(mode: AppSortMode, apps: List<AppLauncher>): List<AppLauncher> {
        val sorter = sorters[mode] ?: sorters[AppSortMode.Alphabetical]!!
        return sorter.sort(context, apps)
    }
}

private class AlphabeticalSorter : AppSorter {
    override val id: AppSortMode = AppSortMode.Alphabetical
    override fun sort(context: Context, apps: List<AppLauncher>): List<AppLauncher> {
        return apps.sortedWith(compareBy({ it.title.lowercase() }, { it.packageName }))
    }
}

private class MostUsedSorter : AppSorter {
    override val id: AppSortMode = AppSortMode.MostUsed
    override fun sort(context: Context, apps: List<AppLauncher>): List<AppLauncher> {
        // Placeholder: usage stats requires permission; fallback to alphabetical
        val hasUsageAccess = hasUsageAccess(context)
        if (!hasUsageAccess) return AlphabeticalSorter().sort(context, apps)
        // TODO: implement usage stats based ordering
        return AlphabeticalSorter().sort(context, apps)
    }

    private fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(android.app.AppOpsManager::class.java)
        val mode = appOps.noteOpNoThrow(
            "android:get_usage_stats",
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }
}

private class RecentlyInstalledSorter : AppSorter {
    override val id: AppSortMode = AppSortMode.RecentlyInstalled
    override fun sort(context: Context, apps: List<AppLauncher>): List<AppLauncher> {
        val pm: PackageManager = context.packageManager
        val installTimes = apps.associateWith { launcher ->
            try {
                pm.getPackageInfo(launcher.packageName, 0).firstInstallTime
            } catch (e: Exception) {
                0L
            }
        }
        return apps.sortedByDescending { installTimes[it] ?: 0L }
    }
}


