package org.fossify.home.backup

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import org.fossify.home.extensions.config
import org.fossify.home.extensions.homeScreenGridItemsDB
import org.fossify.home.extensions.hiddenIconsDB
import org.fossify.home.models.HomeScreenGridItem
import org.fossify.home.models.HiddenIcon

class BackupManager(private val context: Context) {
    data class ExportBundle(
        val version: Int,
        val settings: Map<String, Any?>,
        val layout: Any?
    )

    fun export(): ExportBundle {
        val cfg = context.config
        val settings = mapOf(
            "homeRowCount" to cfg.homeRowCount,
            "homeColumnCount" to cfg.homeColumnCount,
            "drawerColumnCount" to cfg.drawerColumnCount,
            "showSearchBar" to cfg.showSearchBar,
            "closeAppDrawer" to cfg.closeAppDrawer,
            "autoShowKeyboardInAppDrawer" to cfg.autoShowKeyboardInAppDrawer,
            "drawerSortMode" to cfg.drawerSortMode,
            "autoAddNewApps" to cfg.autoAddNewApps,
            "drawerLabelVisible" to cfg.drawerLabelVisible,
            "drawerLabelSizeSp" to cfg.drawerLabelSizeSp,
            "homeLabelVisible" to cfg.homeLabelVisible,
            "homeLabelSizeSp" to cfg.homeLabelSizeSp,
            "folderStylePreset" to cfg.folderStylePreset,
            "useDynamicColors" to cfg.useDynamicColors,
            "useThemedIcons" to cfg.useThemedIcons,
            "iconPackPackage" to cfg.iconPackPackage,
            "iconShapeMode" to cfg.iconShapeMode,
            "predictiveSuggestionsEnabled" to cfg.predictiveSuggestionsEnabled,
            "predictiveSuggestionsCount" to cfg.predictiveSuggestionsCount,
        )

        val items = context.homeScreenGridItemsDB.getAllItems()
        val layoutJson = JSONArray(items.map { it.toJson() })
        val hidden = context.hiddenIconsDB.getHiddenIcons()
        val hiddenJson = JSONArray(hidden.map { it.toJson() })
        val layoutBundle = JSONObject().apply {
            put("homeItems", layoutJson)
            put("hiddenIcons", hiddenJson)
        }
        return ExportBundle(version = 1, settings = settings, layout = layoutBundle)
    }

    fun exportJson(): String {
        val bundle = export()
        val obj = JSONObject().apply {
            put("version", bundle.version)
            put("settings", JSONObject(bundle.settings))
            put("layout", bundle.layout as JSONObject)
        }
        return obj.toString()
    }

    fun import(bundle: ExportBundle, dryRun: Boolean = true): Boolean {
        if (bundle.version != 1) return false
        val settings = bundle.settings
        val layout = bundle.layout as? JSONObject ?: return false
        if (dryRun) return validate(layout)

        // Apply settings
        context.config.apply {
            (settings["homeRowCount"] as? Int)?.let { homeRowCount = it }
            (settings["homeColumnCount"] as? Int)?.let { homeColumnCount = it }
            (settings["drawerColumnCount"] as? Int)?.let { drawerColumnCount = it }
            (settings["showSearchBar"] as? Boolean)?.let { showSearchBar = it }
            (settings["closeAppDrawer"] as? Boolean)?.let { closeAppDrawer = it }
            (settings["autoShowKeyboardInAppDrawer"] as? Boolean)?.let { autoShowKeyboardInAppDrawer = it }
            (settings["drawerSortMode"] as? Int)?.let { drawerSortMode = it }
            (settings["autoAddNewApps"] as? Boolean)?.let { autoAddNewApps = it }
            (settings["drawerLabelVisible"] as? Boolean)?.let { drawerLabelVisible = it }
            (settings["drawerLabelSizeSp"] as? Int)?.let { drawerLabelSizeSp = it }
            (settings["homeLabelVisible"] as? Boolean)?.let { homeLabelVisible = it }
            (settings["homeLabelSizeSp"] as? Int)?.let { homeLabelSizeSp = it }
            (settings["folderStylePreset"] as? Int)?.let { folderStylePreset = it }
            (settings["useDynamicColors"] as? Boolean)?.let { useDynamicColors = it }
            (settings["useThemedIcons"] as? Boolean)?.let { useThemedIcons = it }
            (settings["iconPackPackage"] as? String)?.let { iconPackPackage = it }
            (settings["iconShapeMode"] as? Int)?.let { iconShapeMode = it }
            (settings["predictiveSuggestionsEnabled"] as? Boolean)?.let { predictiveSuggestionsEnabled = it }
            (settings["predictiveSuggestionsCount"] as? Int)?.let { predictiveSuggestionsCount = it }
        }

        // Apply layout
        val items = layout.optJSONArray("homeItems") ?: JSONArray()
        val hidden = layout.optJSONArray("hiddenIcons") ?: JSONArray()

        val db = context.homeScreenGridItemsDB
        val existing = db.getAllItems()
        existing.forEach { db.deleteById(it.id!!) }
        val toInsert = ArrayList<HomeScreenGridItem>()
        for (i in 0 until items.length()) {
            val obj = items.getJSONObject(i)
            toInsert.add(toHomeScreenGridItem(obj))
        }
        db.insertAll(toInsert)

        val hiddenDao = context.hiddenIconsDB
        val existingHidden = hiddenDao.getHiddenIcons()
        if (existingHidden.isNotEmpty()) hiddenDao.removeHiddenIcons(existingHidden)
        for (i in 0 until hidden.length()) {
            val obj = hidden.getJSONObject(i)
            hiddenDao.insert(toHiddenIcon(obj))
        }

        return true
    }

    private fun validate(layout: JSONObject): Boolean {
        return layout.has("homeItems") && layout.has("hiddenIcons")
    }
}

private fun HomeScreenGridItem.toJson(): JSONObject = JSONObject().apply {
    put("left", left)
    put("top", top)
    put("right", right)
    put("bottom", bottom)
    put("page", page)
    put("packageName", packageName)
    put("activityName", activityName)
    put("title", title)
    put("type", type)
    put("className", className)
    put("widgetId", widgetId)
    put("shortcutId", shortcutId)
    put("docked", docked)
    put("parentId", parentId)
}

private fun HiddenIcon.toJson(): JSONObject = JSONObject().apply {
    put("packageName", packageName)
    put("activityName", activityName)
    put("title", title)
}

private fun toHomeScreenGridItem(obj: JSONObject): HomeScreenGridItem {
    return HomeScreenGridItem(
        id = null,
        left = obj.getInt("left"),
        top = obj.getInt("top"),
        right = obj.getInt("right"),
        bottom = obj.getInt("bottom"),
        page = obj.getInt("page"),
        packageName = obj.getString("packageName"),
        activityName = obj.getString("activityName"),
        title = obj.getString("title"),
        type = obj.getInt("type"),
        className = obj.getString("className"),
        widgetId = obj.getInt("widgetId"),
        shortcutId = obj.getString("shortcutId"),
        icon = null,
        docked = obj.getBoolean("docked"),
        parentId = if (obj.has("parentId") && !obj.isNull("parentId")) obj.getLong("parentId") else null,
        drawable = null,
        providerInfo = null,
        activityInfo = null
    )
}

private fun toHiddenIcon(obj: JSONObject): HiddenIcon {
    return HiddenIcon(
        id = null,
        packageName = obj.getString("packageName"),
        activityName = obj.getString("activityName"),
        title = obj.getString("title"),
        drawable = null
    )
}



