package org.fossify.home.data

import android.content.Context
import org.fossify.home.core.DeviceCapabilities
import org.fossify.home.helpers.Config

class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val config: Config = Config.newInstance(appContext)

    // Feature flags that can be expanded as needed
    data class FeatureFlags(
        val enableBlur: Boolean,
        val enableThemedIcons: Boolean,
        val enableDynamicColor: Boolean,
    )

    @Volatile
    private var _featureFlags = FeatureFlags(
        enableBlur = true,
        enableThemedIcons = false,
        enableDynamicColor = false,
    )
    val featureFlags: FeatureFlags get() = _featureFlags

    // Existing settings bridged from Config for future migration
    var homeColumnCount: Int
        get() = config.homeColumnCount
        set(value) { config.homeColumnCount = value }

    var homeRowCount: Int
        get() = config.homeRowCount
        set(value) { config.homeRowCount = value }

    var drawerColumnCount: Int
        get() = config.drawerColumnCount
        set(value) { config.drawerColumnCount = value }

    var showSearchBar: Boolean
        get() = config.showSearchBar
        set(value) { config.showSearchBar = value }

    var closeAppDrawerOnLaunch: Boolean
        get() = config.closeAppDrawer
        set(value) { config.closeAppDrawer = value }

    fun applyCapabilityGating(capabilities: DeviceCapabilities) {
        _featureFlags = _featureFlags.copy(
            enableDynamicColor = capabilities.supportsDynamicColor,
            enableThemedIcons = capabilities.supportsThemedIcons,
            enableBlur = capabilities.supportsRenderEffectBlur,
        )
    }
}


