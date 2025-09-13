package org.fossify.home.search

import android.content.Context
import android.content.Intent
import android.provider.Settings
import org.fossify.home.models.AppLauncher

class AppsSearchProvider(private val apps: () -> List<AppLauncher>) : SearchProvider {
    override val id: String = "apps"
    override fun isEnabled(): Boolean = true
    override fun search(query: String): List<SearchResult> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return apps().filter { it.title.lowercase().contains(q) }
            .take(20)
            .map { app ->
                SearchResult(app.title, app.packageName) {
                    // action bound by caller
                }
            }
    }
}

class SettingsSearchProvider(private val context: Context) : SearchProvider {
    override val id: String = "settings"
    override fun isEnabled(): Boolean = true
    override fun search(query: String): List<SearchResult> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        // Basic: always offer to open Android settings when query mentions settings
        return if ("setting" in q) listOf(
            SearchResult("Open Android Settings", null) {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        ) else emptyList()
    }
}

class WebSearchProvider(private val context: Context) : SearchProvider {
    override val id: String = "web"
    override fun isEnabled(): Boolean = true
    override fun search(query: String): List<SearchResult> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return listOf(
            SearchResult("Search web for \"$q\"", null) {
                val intent = Intent(Intent.ACTION_WEB_SEARCH).putExtra("query", q)
                try { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) { }
            }
        )
    }
}


