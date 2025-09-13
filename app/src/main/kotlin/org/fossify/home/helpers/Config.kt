package org.fossify.home.helpers

import android.content.Context
import org.fossify.commons.helpers.BaseConfig
import org.fossify.home.R

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var wasHomeScreenInit: Boolean
        get() = prefs.getBoolean(WAS_HOME_SCREEN_INIT, false)
        set(wasHomeScreenInit) = prefs.edit().putBoolean(WAS_HOME_SCREEN_INIT, wasHomeScreenInit).apply()

    var homeColumnCount: Int
        get() = prefs.getInt(HOME_COLUMN_COUNT, COLUMN_COUNT)
        set(homeColumnCount) = prefs.edit().putInt(HOME_COLUMN_COUNT, homeColumnCount).apply()

    var homeRowCount: Int
        get() = prefs.getInt(HOME_ROW_COUNT, ROW_COUNT)
        set(homeRowCount) = prefs.edit().putInt(HOME_ROW_COUNT, homeRowCount).apply()

    var drawerColumnCount: Int
        get() = prefs.getInt(DRAWER_COLUMN_COUNT, context.resources.getInteger(R.integer.portrait_column_count))
        set(drawerColumnCount) = prefs.edit().putInt(DRAWER_COLUMN_COUNT, drawerColumnCount).apply()

    var showSearchBar: Boolean
        get() = prefs.getBoolean(SHOW_SEARCH_BAR, true)
        set(showSearchBar) = prefs.edit().putBoolean(SHOW_SEARCH_BAR, showSearchBar).apply()

    var closeAppDrawer: Boolean
        get() = prefs.getBoolean(CLOSE_APP_DRAWER, false)
        set(closeAppDrawer) = prefs.edit().putBoolean(CLOSE_APP_DRAWER, closeAppDrawer).apply()

    var autoShowKeyboardInAppDrawer: Boolean
        get() = prefs.getBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, false)
        set(autoShowKeyboardInAppDrawer) = prefs.edit()
            .putBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, autoShowKeyboardInAppDrawer).apply()

    var lockHomeLayout: Boolean
        get() = prefs.getBoolean(LOCK_HOME_LAYOUT, false)
        set(lockHomeLayout) = prefs.edit().putBoolean(LOCK_HOME_LAYOUT, lockHomeLayout).apply()

    var drawerSortMode: Int
        get() = prefs.getInt(DRAWER_SORT_MODE, 0)
        set(value) = prefs.edit().putInt(DRAWER_SORT_MODE, value).apply()

    var autoAddNewApps: Boolean
        get() = prefs.getBoolean(AUTO_ADD_NEW_APPS, false)
        set(value) = prefs.edit().putBoolean(AUTO_ADD_NEW_APPS, value).apply()

    // Label controls (drawer)
    var drawerLabelVisible: Boolean
        get() = prefs.getBoolean(DRAWER_LABEL_VISIBLE, true)
        set(value) = prefs.edit().putBoolean(DRAWER_LABEL_VISIBLE, value).apply()

    var drawerLabelSizeSp: Int
        get() = prefs.getInt(DRAWER_LABEL_SIZE_SP, 12)
        set(value) = prefs.edit().putInt(DRAWER_LABEL_SIZE_SP, value).apply()

    // Label controls (home)
    var homeLabelVisible: Boolean
        get() = prefs.getBoolean(HOME_LABEL_VISIBLE, true)
        set(value) = prefs.edit().putBoolean(HOME_LABEL_VISIBLE, value).apply()

    var homeLabelSizeSp: Int
        get() = prefs.getInt(HOME_LABEL_SIZE_SP, 12)
        set(value) = prefs.edit().putInt(HOME_LABEL_SIZE_SP, value).apply()

    // Folder style preset
    var folderStylePreset: Int
        get() = prefs.getInt(FOLDER_STYLE_PRESET, 0)
        set(value) = prefs.edit().putInt(FOLDER_STYLE_PRESET, value).apply()

    var useDynamicColors: Boolean
        get() = prefs.getBoolean(USE_DYNAMIC_COLORS, true)
        set(value) = prefs.edit().putBoolean(USE_DYNAMIC_COLORS, value).apply()

    var useThemedIcons: Boolean
        get() = prefs.getBoolean(USE_THEMED_ICONS, false)
        set(value) = prefs.edit().putBoolean(USE_THEMED_ICONS, value).apply()

    var iconPackPackage: String
        get() = prefs.getString(ICON_PACK_PACKAGE, "") ?: ""
        set(value) = prefs.edit().putString(ICON_PACK_PACKAGE, value).apply()

    var iconShapeMode: Int
        get() = prefs.getInt(ICON_SHAPE_MODE, 0)
        set(value) = prefs.edit().putInt(ICON_SHAPE_MODE, value).apply()

    var predictiveSuggestionsEnabled: Boolean
        get() = prefs.getBoolean(PREDICTIVE_SUGGESTIONS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(PREDICTIVE_SUGGESTIONS_ENABLED, value).apply()

    var predictiveSuggestionsCount: Int
        get() = prefs.getInt(PREDICTIVE_SUGGESTIONS_COUNT, 4)
        set(value) = prefs.edit().putInt(PREDICTIVE_SUGGESTIONS_COUNT, value).apply()
}
