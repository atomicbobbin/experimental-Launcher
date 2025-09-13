package org.fossify.home.predict

import android.content.Context
import android.content.SharedPreferences

class UsageTracker(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("usage_tracker", Context.MODE_PRIVATE)

    fun recordLaunch(identifier: String) {
        val now = System.currentTimeMillis()
        val keyLast = "last_$identifier"
        val keyCount = "count_$identifier"
        prefs.edit()
            .putLong(keyLast, now)
            .putInt(keyCount, prefs.getInt(keyCount, 0) + 1)
            .apply()
    }

    fun getRecentLaunches(limit: Int = 20): List<String> {
        return prefs.all
            .filter { it.key.startsWith("last_") }
            .map { it.key.removePrefix("last_") to (it.value as? Long ?: 0L) }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limit)
    }

    fun getMostFrequent(limit: Int = 20): List<String> {
        return prefs.all
            .filter { it.key.startsWith("count_") }
            .map { it.key.removePrefix("count_") to (it.value as? Int ?: 0) }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limit)
    }
}


