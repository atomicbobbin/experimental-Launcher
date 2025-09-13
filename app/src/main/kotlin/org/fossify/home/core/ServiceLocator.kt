package org.fossify.home.core

import android.content.Context
import org.fossify.home.backup.BackupManager
import org.fossify.home.data.SettingsRepository
import org.fossify.home.gestures.GestureRouter
import org.fossify.home.icons.IconResolver
import org.fossify.home.search.SearchRegistry
import org.fossify.home.search.AppsSearchProvider
import org.fossify.home.search.SettingsSearchProvider
import org.fossify.home.search.WebSearchProvider
import org.fossify.home.theme.ThemeManager

/**
 * Centralized service locator to avoid scattered singletons and ease testing.
 * Initialize once at app startup and access via ServiceLocator.instance.* members.
 */
object ServiceLocator {
    @Volatile
    private var initialized: Boolean = false

    lateinit var deviceCapabilities: DeviceCapabilities
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var themeManager: ThemeManager
        private set

    lateinit var gestureRouter: GestureRouter
        private set

    lateinit var searchRegistry: SearchRegistry
        private set

    lateinit var iconResolver: IconResolver
        private set

    lateinit var backupManager: BackupManager
        private set

    fun initialize(context: Context) {
        if (initialized) return

        synchronized(this) {
            if (initialized) return

            val appContext = context.applicationContext
            deviceCapabilities = DeviceCapabilities(appContext)
            settingsRepository = SettingsRepository(appContext)
            themeManager = ThemeManager(appContext, deviceCapabilities, settingsRepository)
            gestureRouter = GestureRouter()
            searchRegistry = SearchRegistry(appContext)
            // Register default providers
            searchRegistry.register(AppsSearchProvider { iconResolver.loadAllLaunchers(emptySet()) })
            searchRegistry.register(SettingsSearchProvider(appContext))
            searchRegistry.register(WebSearchProvider(appContext))
            iconResolver = IconResolver(appContext, deviceCapabilities, settingsRepository)
            backupManager = BackupManager(appContext)

            initialized = true
        }
    }
}


