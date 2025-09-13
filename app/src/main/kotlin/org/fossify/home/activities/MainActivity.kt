package org.fossify.home.activities

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.app.role.RoleManager
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.telecom.TelecomManager
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import kotlinx.collections.immutable.toImmutableList
import org.fossify.commons.extensions.appLaunched
import org.fossify.commons.extensions.beVisible
import org.fossify.commons.extensions.getContrastColor
import org.fossify.commons.extensions.getProperBackgroundColor
import org.fossify.commons.extensions.isPackageInstalled
import org.fossify.commons.extensions.onGlobalLayout
import org.fossify.commons.extensions.performHapticFeedback
import org.fossify.commons.extensions.realScreenSize
import org.fossify.commons.extensions.showErrorToast
import org.fossify.commons.extensions.toast
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.DARK_GREY
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.commons.helpers.isQPlus
import org.fossify.home.BuildConfig
import org.fossify.home.R
import org.fossify.home.databinding.ActivityMainBinding
import org.fossify.home.databinding.AllAppsFragmentBinding
import org.fossify.home.databinding.WidgetsFragmentBinding
import org.fossify.home.dialogs.RenameItemDialog
import org.fossify.home.extensions.config
import org.fossify.home.extensions.getDrawableForPackageName
import org.fossify.home.extensions.getLabel
import org.fossify.home.extensions.hiddenIconsDB
import org.fossify.home.extensions.homeScreenGridItemsDB
import org.fossify.home.extensions.isDefaultLauncher
import org.fossify.home.extensions.launchApp
import org.fossify.home.extensions.launchAppInfo
import org.fossify.home.extensions.launchersDB
import org.fossify.home.extensions.roleManager
import org.fossify.home.extensions.uninstallApp
import org.fossify.home.fragments.FragmentManager
import org.fossify.home.fragments.MyFragment
import org.fossify.home.gestures.GestureHandler
import org.fossify.home.gestures.GestureHandlerCallback
import org.fossify.home.helpers.ITEM_TYPE_FOLDER
import org.fossify.home.helpers.ITEM_TYPE_ICON
import org.fossify.home.helpers.ITEM_TYPE_SHORTCUT
import org.fossify.home.helpers.ITEM_TYPE_WIDGET
import org.fossify.home.helpers.IconCache
import org.fossify.home.core.ServiceLocator
import org.fossify.home.gestures.GestureRouter
import org.fossify.home.icons.IconResolver
import org.fossify.home.helpers.REQUEST_ALLOW_BINDING_WIDGET
import org.fossify.home.helpers.REQUEST_CONFIGURE_WIDGET
import org.fossify.home.helpers.REQUEST_CREATE_SHORTCUT
import org.fossify.home.helpers.REQUEST_SET_DEFAULT
import org.fossify.home.helpers.UNINSTALL_APP_REQUEST_CODE
import org.fossify.home.interfaces.FlingListener
import org.fossify.home.interfaces.ItemMenuListener
import org.fossify.home.menu.MenuManager
import org.fossify.home.models.AppLauncher
import org.fossify.home.models.HiddenIcon
import org.fossify.home.models.HomeScreenGridItem
import org.fossify.home.receivers.LockDeviceAdminReceiver
import org.fossify.home.touch.TouchEventManager
import org.fossify.home.utils.Logger
import org.fossify.home.utils.ErrorHandler
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainActivity : SimpleActivity(), FlingListener, GestureHandlerCallback {
    // Component managers
    private lateinit var touchEventManager: TouchEventManager
    private lateinit var fragmentManager: FragmentManager
    private lateinit var menuManager: MenuManager
    private lateinit var gestureHandler: GestureHandler
    private lateinit var detector: GestureDetectorCompat
    
    // Callback actions
    private var actionOnCanBindWidget: ((granted: Boolean) -> Unit)? = null
    private var actionOnWidgetConfiguredWidget: ((granted: Boolean) -> Unit)? = null
    private var actionOnAddShortcut: ((shortcutId: String, label: String, icon: Drawable) -> Unit)? = null
    private var wasJustPaused: Boolean = false

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val settingsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "org.fossify.home.REFRESH_GRID" -> {
                    binding.homeScreenGrid.root.refreshIconSettings()
                }
                "org.fossify.home.REFRESH_DRAWER" -> {
                    binding.allAppsFragment.root.refreshIconSettings()
                }
                "org.fossify.home.REFRESH_BLUR" -> {
                    refreshBlurEffects()
                }
                "org.fossify.home.REFRESH_TRANSITIONS" -> {
                    // Transition effects are applied dynamically, no refresh needed
                }
                "org.fossify.home.REFRESH_BADGES" -> {
                    refreshNotificationBadges()
                }
                "org.fossify.home.REFRESH_WALLPAPER" -> {
                    binding.homeScreenGrid.root.refreshWallpaper()
                }
            }
        }
    }

    companion object {
        private var mLastUpEvent = 0L
        private const val ANIMATION_DURATION = 150L
        private const val APP_DRAWER_CLOSE_DELAY = 300L
        private const val APP_DRAWER_STATE = "app_drawer_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.methodEntry("onCreate", savedInstanceState)
        
        try {
            // Ensure ServiceLocator is initialized
            if (!ServiceLocator.isInitialized()) {
                ServiceLocator.initialize(this)
            }
            
            useDynamicTheme = config.useDynamicColors

            super.onCreate(savedInstanceState)
            setContentView(binding.root)
            
            // Make the main layout transparent so wallpaper shows through
            binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            
            appLaunched(BuildConfig.APPLICATION_ID)

            // Initialize component managers
            initializeComponentManagers()

            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            Logger.methodExit("onCreate")
        } catch (e: Exception) {
            ErrorHandler.handleError(this, e, "Failed to initialize launcher")
            Logger.methodExit("onCreate", "ERROR")
        }

        val screenHeight = realScreenSize.y
        arrayOf(
            binding.allAppsFragment.root as MyFragment<*>,
            binding.widgetsFragment.root as MyFragment<*>
        ).forEach { fragment ->
            fragment.setupFragment(this)
            fragment.y = screenHeight.toFloat()
            fragment.beVisible()
        }

        handleIntentAction(intent)

        binding.homeScreenGrid.root.itemClickListener = {
            performItemClick(it)
        }

        binding.homeScreenGrid.root.itemLongClickListener = {
            performItemLongClick(
                x = binding.homeScreenGrid.root.getClickableRect(it).left.toFloat(),
                clickedGridItem = it
            )
        }

        if (!isDefaultLauncher()) {
            requestHomeRole()
        }

        // Register broadcast receivers for settings changes
        registerSettingsBroadcastReceivers()
    }
    
    /**
     * Initialize all component managers.
     */
    private fun initializeComponentManagers() {
        Logger.methodEntry("initializeComponentManagers")
        
        try {
            val screenHeight = realScreenSize.y
            val moveGestureThreshold = resources.getDimensionPixelSize(R.dimen.move_gesture_threshold)
            
            // Initialize touch event manager
            touchEventManager = TouchEventManager().apply {
                initialize(screenHeight, moveGestureThreshold)
            }
            
            // Initialize fragment manager
            fragmentManager = FragmentManager(
                screenHeight = screenHeight,
                updateStatusBarIcons = { backgroundColor -> updateStatusBarIcons(backgroundColor) },
                updateNavigationBarColor = { color -> window.navigationBarColor = color }
            )
            
            // Initialize menu manager
            menuManager = MenuManager(this)
            
            // Initialize gesture handler
            gestureHandler = GestureHandler(this, this)
            detector = GestureDetectorCompat(this, gestureHandler)
            
            Logger.methodExit("initializeComponentManagers")
        } catch (e: Exception) {
            ErrorHandler.handleError(this, e, "Failed to initialize component managers")
            Logger.methodExit("initializeComponentManagers", "ERROR")
        }
    }

    private fun registerSettingsBroadcastReceivers() {
        val filter = IntentFilter().apply {
            addAction("org.fossify.home.REFRESH_GRID")
            addAction("org.fossify.home.REFRESH_DRAWER")
            addAction("org.fossify.home.REFRESH_BLUR")
            addAction("org.fossify.home.REFRESH_TRANSITIONS")
            addAction("org.fossify.home.REFRESH_BADGES")
            addAction("org.fossify.home.REFRESH_WALLPAPER")
        }
        registerReceiver(settingsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    private fun refreshBlurEffects() {
        // Apply blur to folder backgrounds and app drawer
        binding.allAppsFragment.root.refreshBlurEffects()
        binding.homeScreenGrid.root.refreshBlurEffects()
    }

    private fun refreshNotificationBadges() {
        // Refresh notification badges on home screen and drawer
        binding.homeScreenGrid.root.refreshNotificationBadges()
        binding.allAppsFragment.root.refreshNotificationBadges()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(settingsReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val wasAnyFragmentOpen = isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded()
        if (wasJustPaused) {
            if (isAllAppsFragmentExpanded()) {
                hideFragment(binding.allAppsFragment)
            }
            if (isWidgetsFragmentExpanded()) {
                hideFragment(binding.widgetsFragment)
            }
        } else {
            closeAppDrawer()
            closeWidgetsFragment()
        }

        binding.allAppsFragment.searchBar.closeSearch()

        // scroll to first page when home button is pressed
        val alreadyOnHome = intent.flags and FLAG_ACTIVITY_BROUGHT_TO_FRONT == 0
        if (alreadyOnHome && !wasAnyFragmentOpen) {
            binding.homeScreenGrid.root.skipToPage(0)
        }

        handleIntentAction(intent)
    }

    override fun onStart() {
        super.onStart()
        binding.homeScreenGrid.root.appWidgetHost.startListening()
    }

    override fun onResume() {
        super.onResume()
        wasJustPaused = false
        updateStatusbarColor(Color.TRANSPARENT)

        with(binding.mainHolder) {
            onGlobalLayout {
                binding.allAppsFragment.root.setupViews()
                binding.widgetsFragment.root.setupViews()
                updateStatusbarColor(Color.TRANSPARENT)
            }

            setOnApplyWindowInsetsListener { _, insets ->
                val windowInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
                val systemBarInsets = windowInsets.getInsets(Type.systemBars() or Type.ime())
                binding.allAppsFragment.root.setPadding(0, systemBarInsets.top, 0, 0)
                binding.widgetsFragment.root.setPadding(0, systemBarInsets.top, 0, 0)
                insets
            }
        }

        // Ensure latest desktop state (e.g., auto-added icons) is reflected
        binding.homeScreenGrid.root.fetchGridItems()

        ensureBackgroundThread {
            if (IconCache.launchers.isEmpty()) {
                val hiddenIcons = hiddenIconsDB.getHiddenIcons().map {
                    it.getIconIdentifier()
                }

                IconCache.launchers = launchersDB.getAppLaunchers().filter {
                    val showIcon = !hiddenIcons.contains(it.getLauncherIdentifier())
                    if (!showIcon) {
                        try {
                            launchersDB.deleteById(it.id!!)
                        } catch (_: Exception) {
                        }
                    }
                    showIcon
                }.toMutableList() as ArrayList<AppLauncher>
            }

            binding.allAppsFragment.root.gotLaunchers(IconCache.launchers)
            refreshLaunchers()
        }

        // avoid showing fully colored navigation bars
        if (window.navigationBarColor != resources.getColor(R.color.semitransparent_navigation)) {
            window.navigationBarColor = Color.TRANSPARENT
        }

        binding.homeScreenGrid.root.resizeGrid(
            newRowCount = config.homeRowCount,
            newColumnCount = config.homeColumnCount
        )
        binding.homeScreenGrid.root.updateColors()
        binding.allAppsFragment.root.onResume()
    }

    override fun onStop() {
        super.onStop()
        try {
            binding.homeScreenGrid.root.appWidgetHost.stopListening()
        } catch (_: Exception) {
        }

        wasJustPaused = false
    }

    override fun onPause() {
        super.onPause()
        wasJustPaused = true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (isAllAppsFragmentExpanded()) {
            if (!binding.allAppsFragment.root.onBackPressed()) {
                hideFragment(binding.allAppsFragment)
            }
        } else if (isWidgetsFragmentExpanded()) {
            hideFragment(binding.widgetsFragment)
        } else if (binding.homeScreenGrid.resizeFrame.isVisible) {
            binding.homeScreenGrid.root.hideResizeLines()
        } else {
            // this is a home launcher app, avoid glitching by pressing Back
            //super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        when (requestCode) {
            UNINSTALL_APP_REQUEST_CODE -> {
                ensureBackgroundThread {
                    refreshLaunchers()
                }
            }

            REQUEST_ALLOW_BINDING_WIDGET -> actionOnCanBindWidget?.invoke(resultCode == RESULT_OK)
            REQUEST_CONFIGURE_WIDGET -> actionOnWidgetConfiguredWidget?.invoke(resultCode == RESULT_OK)
            REQUEST_CREATE_SHORTCUT -> {
                if (resultCode == RESULT_OK && resultData != null) {
                    val launcherApps =
                        applicationContext.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
                    if (launcherApps.hasShortcutHostPermission()) {
                        val item = launcherApps.getPinItemRequest(resultData)
                        val shortcutInfo = item?.shortcutInfo ?: return
                        if (item.accept()) {
                            val shortcutId = shortcutInfo.id
                            val label = shortcutInfo.getLabel()
                            val icon = launcherApps.getShortcutBadgedIconDrawable(
                                shortcutInfo,
                                resources.displayMetrics.densityDpi
                            )
                            actionOnAddShortcut?.invoke(shortcutId, label, icon)
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.allAppsFragment.root.onConfigurationChanged()
        binding.widgetsFragment.root.onConfigurationChanged()
        updateStatusbarColor(Color.TRANSPARENT)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        if (touchEventManager.longPressedIcon != null && 
            (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL)) {
            gestureHandler.updateLastUpEvent()
        }

        try {
            detector.onTouchEvent(event)
        } catch (_: Exception) {
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchEventManager.onTouchDown(event)
                touchEventManager.allAppsFragmentY = binding.allAppsFragment.root.y.toInt()
                touchEventManager.widgetsFragmentY = binding.widgetsFragment.root.y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                val moveResult = touchEventManager.onTouchMove(event)
                
                if (touchEventManager.longPressedIcon != null && 
                    menuManager.isPopupMenuOpen() && 
                    moveResult.hasFingerMoved) {
                    menuManager.dismissPopupMenu()
                    binding.homeScreenGrid.root.itemDraggingStarted(touchEventManager.longPressedIcon!!)
                    fragmentManager.hideFragment(binding.allAppsFragment)
                }

                if (touchEventManager.longPressedIcon != null && moveResult.hasFingerMoved) {
                    binding.homeScreenGrid.root.draggedItemMoved(event.x.toInt(), event.y.toInt())
                }

                if (moveResult.hasFingerMoved && !touchEventManager.ignoreMoveEvents) {
                    if (abs(moveResult.diffY) > abs(moveResult.diffX) && !touchEventManager.ignoreYMoveEvents) {
                        touchEventManager.ignoreXMoveEvents = true
                        handleVerticalSwipe(moveResult.diffY)
                    } else if (abs(moveResult.diffX) > abs(moveResult.diffY) && !touchEventManager.ignoreXMoveEvents) {
                        touchEventManager.ignoreYMoveEvents = true
                        binding.homeScreenGrid.root.setSwipeMovement(moveResult.diffX)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                touchEventManager.onTouchUp()
                touchEventManager.resetFragmentTouches()
                binding.homeScreenGrid.root.itemDraggingStopped()

                if (!touchEventManager.ignoreUpEvent) {
                    handleTouchUp()
                }

                touchEventManager.ignoreXMoveEvents = false
                touchEventManager.ignoreYMoveEvents = false
            }
        }

        return true
    }
    
    /**
     * Handle vertical swipe gestures.
     */
    private fun handleVerticalSwipe(diffY: Float) {
        if (fragmentManager.isFragmentExpanded(binding.widgetsFragment)) {
            val newY = touchEventManager.widgetsFragmentY - diffY
            binding.widgetsFragment.root.y = min(
                a = max(0f, newY), 
                b = touchEventManager.screenHeight.toFloat()
            )
        } else if (touchEventManager.longPressedIcon == null) {
            val newY = touchEventManager.allAppsFragmentY - diffY
            binding.allAppsFragment.root.y = min(
                a = max(0f, newY), 
                b = touchEventManager.screenHeight.toFloat()
            )
        }
    }
    
    /**
     * Handle touch up events.
     */
    private fun handleTouchUp() {
        if (!touchEventManager.ignoreYMoveEvents) {
            val screenHeight = touchEventManager.screenHeight
            if (binding.allAppsFragment.root.y < screenHeight * 0.5) {
                fragmentManager.showFragment(binding.allAppsFragment)
            } else if (fragmentManager.isFragmentExpanded(binding.allAppsFragment)) {
                fragmentManager.hideFragment(binding.allAppsFragment)
            }

            if (binding.widgetsFragment.root.y < screenHeight * 0.5) {
                fragmentManager.showFragment(binding.widgetsFragment)
            } else if (fragmentManager.isFragmentExpanded(binding.widgetsFragment)) {
                fragmentManager.hideFragment(binding.widgetsFragment)
            }
        }

        if (!touchEventManager.ignoreXMoveEvents) {
            binding.homeScreenGrid.root.finalizeSwipe()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(APP_DRAWER_STATE, isAllAppsFragmentExpanded())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.getBoolean(APP_DRAWER_STATE)) {
            showFragment(binding.allAppsFragment, 0L)
        }
    }

    private fun handleIntentAction(intent: Intent) {
        if (intent.action == LauncherApps.ACTION_CONFIRM_PIN_SHORTCUT) {
            val launcherApps =
                applicationContext.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
            val item = launcherApps.getPinItemRequest(intent)
            val shortcutInfo = item?.shortcutInfo ?: return

            ensureBackgroundThread {
                val shortcutId = shortcutInfo.id
                val label = shortcutInfo.getLabel()
                val icon = launcherApps.getShortcutBadgedIconDrawable(
                    shortcutInfo,
                    resources.displayMetrics.densityDpi
                )
                val (page, rect) = findFirstEmptyCell()
                val gridItem = HomeScreenGridItem(
                    id = null,
                    left = rect.left,
                    top = rect.top,
                    right = rect.right,
                    bottom = rect.bottom,
                    page = page,
                    packageName = shortcutInfo.`package`,
                    activityName = "",
                    title = label,
                    type = ITEM_TYPE_SHORTCUT,
                    className = "",
                    widgetId = -1,
                    shortcutId = shortcutId,
                    icon = icon.toBitmap(),
                    docked = false,
                    parentId = null,
                    drawable = icon
                )

                runOnUiThread {
                    binding.homeScreenGrid.root.skipToPage(page)
                }
                // delay showing the shortcut both to let the user see adding it in realtime and hackily avoid concurrent modification exception at HomeScreenGrid
                Thread.sleep(2000)

                try {
                    item.accept()
                    binding.homeScreenGrid.root.storeAndShowGridItem(gridItem)
                } catch (_: IllegalStateException) {
                }
            }
        }
    }

    private fun findFirstEmptyCell(): Pair<Int, Rect> {
        val gridItems = homeScreenGridItemsDB.getAllItems() as ArrayList<HomeScreenGridItem>
        val maxPage = gridItems.maxOf { it.page }
        val occupiedCells = ArrayList<Triple<Int, Int, Int>>()
        gridItems.toImmutableList().filter { it.parentId == null }.forEach { item ->
            for (xCell in item.left..item.right) {
                for (yCell in item.top..item.bottom) {
                    occupiedCells.add(Triple(item.page, xCell, yCell))
                }
            }
        }

        for (page in 0 until maxPage) {
            for (checkedYCell in 0 until config.homeColumnCount) {
                for (checkedXCell in 0 until config.homeRowCount - 1) {
                    val wantedCell = Triple(page, checkedXCell, checkedYCell)
                    if (!occupiedCells.contains(wantedCell)) {
                        return Pair(
                            first = page,
                            second = Rect(
                                wantedCell.second,
                                wantedCell.third,
                                wantedCell.second,
                                wantedCell.third
                            )
                        )
                    }
                }
            }
        }

        return Pair(maxPage + 1, Rect(0, 0, 0, 0))
    }

    // some devices ACTION_MOVE keeps triggering for the whole long press duration, but we are interested in real moves only, when coords change
    private fun hasFingerMoved(event: MotionEvent): Boolean {
        return mTouchDownX != -1 && mTouchDownY != -1 &&
                (abs(mTouchDownX - event.x) > mMoveGestureThreshold || abs(mTouchDownY - event.y) > mMoveGestureThreshold)
    }

    private fun refreshLaunchers() {
        val launchers = getAllAppLaunchers()
        binding.allAppsFragment.root.gotLaunchers(launchers)
        binding.widgetsFragment.root.getAppWidgets()

        IconCache.launchers.map { it.packageName }.forEach { packageName ->
            if (!launchers.map { it.packageName }.contains(packageName)) {
                launchersDB.deleteApp(packageName)
                homeScreenGridItemsDB.deleteByPackageName(packageName)
            }
        }

        IconCache.launchers = launchers

        if (!config.wasHomeScreenInit) {
            ensureBackgroundThread {
                getDefaultAppPackages(launchers)
                config.wasHomeScreenInit = true
                binding.homeScreenGrid.root.fetchGridItems()
            }
        } else {
            binding.homeScreenGrid.root.fetchGridItems()
        }
    }

    fun isAllAppsFragmentExpanded() = fragmentManager.isFragmentExpanded(binding.allAppsFragment)

    private fun isWidgetsFragmentExpanded() = fragmentManager.isFragmentExpanded(binding.widgetsFragment)
    
    // GestureHandlerCallback implementation
    override fun homeScreenClicked(eventX: Float, eventY: Float) {
        binding.homeScreenGrid.root.hideResizeLines()
        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            performItemClick(clickedGridItem)
        }
        if (clickedGridItem?.type != ITEM_TYPE_FOLDER) {
            binding.homeScreenGrid.root.closeFolder(redraw = true)
        }
    }
    
    override fun homeScreenDoubleTapped(eventX: Float, eventY: Float) {
        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            return
        }

        val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val isLockDeviceAdminActive = devicePolicyManager.isAdminActive(
            ComponentName(this, LockDeviceAdminReceiver::class.java)
        )
        if (isLockDeviceAdminActive) {
            devicePolicyManager.lockNow()
        }
    }
    
    override fun homeScreenLongPressed(eventX: Float, eventY: Float) {
        if (isAllAppsFragmentExpanded()) {
            return
        }

        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        touchEventManager.ignoreMoveEvents = true
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            performItemLongClick(x, clickedGridItem)
            return
        }

        binding.mainHolder.performHapticFeedback()
        showMainLongPressMenu(x, y)
    }

    fun startHandlingTouches(touchDownY: Int) {
        touchEventManager.longPressedIcon = null
        touchEventManager.touchDownY = touchDownY
        touchEventManager.allAppsFragmentY = binding.allAppsFragment.root.y.toInt()
        touchEventManager.widgetsFragmentY = binding.widgetsFragment.root.y.toInt()
        touchEventManager.ignoreUpEvent = false
    }

    private fun showFragment(fragment: ViewBinding, animationDuration: Long = ANIMATION_DURATION) {
        fragmentManager.showFragment(fragment, animationDuration) {
            binding.homeScreenGrid.root.fragmentExpanded()
            binding.homeScreenGrid.root.hideResizeLines()
        }
    }

    private fun hideFragment(fragment: ViewBinding, animationDuration: Long = ANIMATION_DURATION) {
        fragmentManager.hideFragment(fragment, animationDuration) {
            binding.homeScreenGrid.root.fragmentCollapsed()
        }
    }

    fun homeScreenLongPressed(eventX: Float, eventY: Float) {
        if (isAllAppsFragmentExpanded()) {
            return
        }

        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        mIgnoreMoveEvents = true
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            performItemLongClick(x, clickedGridItem)
            return
        }

        binding.mainHolder.performHapticFeedback()
        showMainLongPressMenu(x, y)
    }

    fun homeScreenClicked(eventX: Float, eventY: Float) {
        binding.homeScreenGrid.root.hideResizeLines()
        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            performItemClick(clickedGridItem)
        }
        if (clickedGridItem?.type != ITEM_TYPE_FOLDER) {
            binding.homeScreenGrid.root.closeFolder(redraw = true)
        }
    }

    fun homeScreenDoubleTapped(eventX: Float, eventY: Float) {
        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            return
        }

        val devicePolicyManager =
            getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val isLockDeviceAdminActive = devicePolicyManager.isAdminActive(
            ComponentName(this, LockDeviceAdminReceiver::class.java)
        )
        if (isLockDeviceAdminActive) {
            devicePolicyManager.lockNow()
        }
    }

    fun closeAppDrawer(delayed: Boolean = false) {
        fragmentManager.closeAppDrawer(
            allAppsFragment = binding.allAppsFragment,
            fragmentCollapsed = { binding.homeScreenGrid.root.fragmentCollapsed() },
            delayed = delayed
        )
    }

    fun closeWidgetsFragment(delayed: Boolean = false) {
        fragmentManager.closeWidgetsFragment(
            widgetsFragment = binding.widgetsFragment,
            fragmentCollapsed = { binding.homeScreenGrid.root.fragmentCollapsed() },
            delayed = delayed
        )
    }

    private fun performItemClick(clickedGridItem: HomeScreenGridItem) {
        when (clickedGridItem.type) {
            ITEM_TYPE_ICON -> launchApp(clickedGridItem.packageName, clickedGridItem.activityName)
            ITEM_TYPE_FOLDER -> openFolder(clickedGridItem)
            ITEM_TYPE_SHORTCUT -> {
                val id = clickedGridItem.shortcutId
                val packageName = clickedGridItem.packageName
                val userHandle = android.os.Process.myUserHandle()
                val shortcutBounds = binding.homeScreenGrid.root.getClickableRect(clickedGridItem)
                val launcherApps =
                    applicationContext.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
                launcherApps.startShortcut(packageName, id, shortcutBounds, null, userHandle)
            }
        }
    }

    private fun openFolder(folder: HomeScreenGridItem) {
        binding.homeScreenGrid.root.openFolder(folder)
    }

    private fun performItemLongClick(x: Float, clickedGridItem: HomeScreenGridItem) {
        if (clickedGridItem.type == ITEM_TYPE_ICON || clickedGridItem.type == ITEM_TYPE_SHORTCUT || clickedGridItem.type == ITEM_TYPE_FOLDER) {
            binding.mainHolder.performHapticFeedback()
        }

        val anchorY = binding.homeScreenGrid.root.sideMargins.top +
                (clickedGridItem.top * binding.homeScreenGrid.root.cellHeight.toFloat())
        showHomeIconMenu(x, anchorY, clickedGridItem, false)
    }

    fun showHomeIconMenu(
        x: Float,
        y: Float,
        gridItem: HomeScreenGridItem,
        isOnAllAppsFragment: Boolean,
    ) {
        binding.homeScreenGrid.root.hideResizeLines()
        touchEventManager.longPressedIcon = gridItem
        
        menuManager.showHomeIconMenu(
            x = x,
            y = y,
            gridItem = gridItem,
            isOnAllAppsFragment = isOnAllAppsFragment,
            anchorView = binding.homeScreenPopupMenuAnchor,
            menuListener = menuListener,
            getClickableRect = { item -> binding.homeScreenGrid.root.getClickableRect(item) },
            getCurrentIconSize = { binding.homeScreenGrid.root.getCurrentIconSize() },
            getIconSize = { realScreenSize.x / config.drawerColumnCount }
        )
    }

    fun widgetLongPressedOnList(gridItem: HomeScreenGridItem) {
        touchEventManager.longPressedIcon = gridItem
        hideFragment(binding.widgetsFragment)
        binding.homeScreenGrid.root.itemDraggingStarted(touchEventManager.longPressedIcon!!)
    }

    private fun showMainLongPressMenu(x: Float, y: Float) {
        binding.homeScreenGrid.root.hideResizeLines()
        menuManager.showMainLongPressMenu(
            x = x,
            y = y,
            anchorView = binding.homeScreenPopupMenuAnchor
        ) { itemId ->
            when (itemId) {
                R.id.widgets -> showWidgetsFragment()
                R.id.wallpapers -> launchWallpapersIntent()
                R.id.launcher_settings -> launchSettings()
                R.id.set_as_default -> launchSetAsDefaultIntent()
            }
        }
    }

    private fun resetFragmentTouches() {
        binding.widgetsFragment.root.apply {
            touchDownY = -1
            ignoreTouches = false
        }

        binding.allAppsFragment.root.apply {
            touchDownY = -1
            ignoreTouches = false
        }
    }

    private fun showWidgetsFragment() {
        showFragment(binding.widgetsFragment)
    }

    private fun hideIcon(item: HomeScreenGridItem) {
        ensureBackgroundThread {
            val hiddenIcon = HiddenIcon(null, item.packageName, item.activityName, item.title, null)
            hiddenIconsDB.insert(hiddenIcon)

            runOnUiThread {
                binding.allAppsFragment.root.onIconHidden(item)
            }
        }
    }

    private fun renameItem(homeScreenGridItem: HomeScreenGridItem) {
        RenameItemDialog(this, homeScreenGridItem) {
            binding.homeScreenGrid.root.fetchGridItems()
        }
    }

    private fun showPopupWidget(gridItem: HomeScreenGridItem) {
        // Placeholder: reuse Widgets fragment as popup selection, or show simple toast
        toast(R.string.popup_widget)
    }

    private fun configureSwipeAction(gridItem: HomeScreenGridItem) {
        // Placeholder: open a simple chooser dialog in future
        toast(R.string.set_swipe_action)
    }

    private fun launchWallpapersIntent() {
        try {
            Intent(Intent.ACTION_SET_WALLPAPER).apply {
                startActivity(this)
            }
        } catch (_: ActivityNotFoundException) {
            toast(org.fossify.commons.R.string.no_app_found)
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }

    private fun launchSettings() {
        startActivity(
            Intent(this@MainActivity, SettingsActivity::class.java)
        )
    }

    private fun launchSetAsDefaultIntent() {
        val intents = listOf(
            Intent(Settings.ACTION_HOME_SETTINGS),
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
        val intent = intents.firstOrNull { it.resolveActivity(packageManager) != null }
        if (intent != null) {
            startActivity(intent)
        }
    }

    private fun requestHomeRole() {
        if (isQPlus()) {
            startActivityForResult(
                roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME),
                REQUEST_SET_DEFAULT
            )
        }
    }

    val menuListener: ItemMenuListener = object : ItemMenuListener {
        override fun onAnyClick() {
            resetFragmentTouches()
        }

        override fun hide(gridItem: HomeScreenGridItem) {
            hideIcon(gridItem)
        }

        override fun rename(gridItem: HomeScreenGridItem) {
            if (config.lockHomeLayout) {
                toast(R.string.home_locked_toast)
            } else {
                renameItem(gridItem)
            }
        }

        override fun resize(gridItem: HomeScreenGridItem) {
            if (config.lockHomeLayout) {
                toast(R.string.home_locked_toast)
            } else {
                binding.homeScreenGrid.root.widgetLongPressed(gridItem)
            }
        }

        override fun appInfo(gridItem: HomeScreenGridItem) {
            launchAppInfo(gridItem.packageName)
        }

        override fun remove(gridItem: HomeScreenGridItem) {
            if (config.lockHomeLayout) {
                toast(R.string.home_locked_toast)
            } else {
                binding.homeScreenGrid.root.removeAppIcon(gridItem)
            }
        }

        override fun uninstall(gridItem: HomeScreenGridItem) {
            uninstallApp(gridItem.packageName)
        }

        override fun popupWidget(gridItem: HomeScreenGridItem) {
            showPopupWidget(gridItem)
        }

        override fun setSwipeAction(gridItem: HomeScreenGridItem) {
            configureSwipeAction(gridItem)
        }

        override fun onDismiss() {
            menuManager.dismissPopupMenu()
            resetFragmentTouches()
        }

        override fun beforeShow(menu: Menu) {
            if (config.lockHomeLayout) {
                menu.findItem(R.id.rename)?.isVisible = false
                menu.findItem(R.id.remove)?.isVisible = false
                menu.findItem(R.id.resize)?.isVisible = false
                menu.findItem(R.id.set_swipe_action)?.isVisible = false
            }
            var visibleMenuItems = 0
            for (item in menu.iterator()) {
                if (item.isVisible) {
                    visibleMenuItems++
                }
            }
            val yOffset =
                resources.getDimension(R.dimen.long_press_anchor_button_offset_y) * (visibleMenuItems - 1)
            binding.homeScreenPopupMenuAnchor.y -= yOffset
        }
    }


    override fun onFlingUp() {
        if (touchEventManager.ignoreYMoveEvents) {
            return
        }

        if (!isWidgetsFragmentExpanded()) {
            touchEventManager.ignoreUpEvent = true
            showFragment(binding.allAppsFragment)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onFlingDown() {
        if (touchEventManager.ignoreYMoveEvents) {
            return
        }

        touchEventManager.ignoreUpEvent = true
        if (isAllAppsFragmentExpanded()) {
            hideFragment(binding.allAppsFragment)
        } else if (isWidgetsFragmentExpanded()) {
            hideFragment(binding.widgetsFragment)
        } else {
            try {
                Class.forName("android.app.StatusBarManager")
                    .getMethod("expandNotificationsPanel")
                    .invoke(getSystemService("statusbar"))
            } catch (_: Exception) {
            }
        }
    }

    override fun onFlingRight() {
        if (touchEventManager.ignoreXMoveEvents) {
            return
        }

        touchEventManager.ignoreUpEvent = true
        binding.homeScreenGrid.root.prevPage(redraw = true)
    }

    override fun onFlingLeft() {
        if (touchEventManager.ignoreXMoveEvents) {
            return
        }

        touchEventManager.ignoreUpEvent = true
        binding.homeScreenGrid.root.nextPage(redraw = true)
    }

    @SuppressLint("WrongConstant")
    fun getAllAppLaunchers(): ArrayList<AppLauncher> {
        ServiceLocator.settingsRepository.applyCapabilityGating(ServiceLocator.deviceCapabilities)
        val hiddenIdentifiers = hiddenIconsDB.getHiddenIcons().map { it.getIconIdentifier() }.toSet()
        val launchers = ServiceLocator.iconResolver.loadAllLaunchers(hiddenIdentifiers)
        launchersDB.insertAll(launchers)
        return ArrayList(launchers)
    }

    private fun getDefaultAppPackages(appLaunchers: ArrayList<AppLauncher>) {
        val homeScreenGridItems = ArrayList<HomeScreenGridItem>()
        try {
            val defaultDialerPackage =
                (getSystemService(TELECOM_SERVICE) as TelecomManager).defaultDialerPackage
            appLaunchers.firstOrNull { it.packageName == defaultDialerPackage }?.apply {
                val dialerIcon =
                    HomeScreenGridItem(
                        id = null,
                        left = 0,
                        top = config.homeRowCount - 1,
                        right = 0,
                        bottom = config.homeRowCount - 1,
                        page = 0,
                        packageName = defaultDialerPackage,
                        activityName = "",
                        title = title,
                        type = ITEM_TYPE_ICON,
                        className = "",
                        widgetId = -1,
                        shortcutId = "",
                        icon = null,
                        docked = true,
                        parentId = null
                    )
                homeScreenGridItems.add(dialerIcon)
            }
        } catch (_: Exception) {
        }

        try {
            val defaultSMSMessengerPackage = Telephony.Sms.getDefaultSmsPackage(this)
            appLaunchers.firstOrNull { it.packageName == defaultSMSMessengerPackage }?.apply {
                val messengerIcon =
                    HomeScreenGridItem(
                        id = null,
                        left = 1,
                        top = config.homeRowCount - 1,
                        right = 1,
                        bottom = config.homeRowCount - 1,
                        page = 0,
                        packageName = defaultSMSMessengerPackage,
                        activityName = "",
                        title = title,
                        type = ITEM_TYPE_ICON,
                        className = "",
                        widgetId = -1,
                        shortcutId = "",
                        icon = null,
                        docked = true,
                        parentId = null
                    )
                homeScreenGridItems.add(messengerIcon)
            }
        } catch (_: Exception) {
        }

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, "http://".toUri())
            val resolveInfo =
                packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val defaultBrowserPackage = resolveInfo!!.activityInfo.packageName
            appLaunchers.firstOrNull { it.packageName == defaultBrowserPackage }?.apply {
                val browserIcon =
                    HomeScreenGridItem(
                        id = null,
                        left = 2,
                        top = config.homeRowCount - 1,
                        right = 2,
                        bottom = config.homeRowCount - 1,
                        page = 0,
                        packageName = defaultBrowserPackage,
                        activityName = "",
                        title = title,
                        type = ITEM_TYPE_ICON,
                        className = "",
                        widgetId = -1,
                        shortcutId = "",
                        icon = null,
                        docked = true,
                        parentId = null
                    )
                homeScreenGridItems.add(browserIcon)
            }
        } catch (_: Exception) {
        }

        try {
            val potentialStores = arrayListOf(
                "com.android.vending", "org.fdroid.fdroid", "com.aurora.store"
            )
            val storePackage = potentialStores.firstOrNull {
                isPackageInstalled(it) && appLaunchers.map { it.packageName }.contains(it)
            }
            if (storePackage != null) {
                appLaunchers.firstOrNull { it.packageName == storePackage }?.apply {
                    val storeIcon = HomeScreenGridItem(
                        id = null,
                        left = 3,
                        top = config.homeRowCount - 1,
                        right = 3,
                        bottom = config.homeRowCount - 1,
                        page = 0,
                        packageName = storePackage,
                        activityName = "",
                        title = title,
                        type = ITEM_TYPE_ICON,
                        className = "",
                        widgetId = -1,
                        shortcutId = "",
                        icon = null,
                        docked = true,
                        parentId = null
                    )
                    homeScreenGridItems.add(storeIcon)
                }
            }
        } catch (_: Exception) {
        }

        try {
            val cameraIntent = Intent("android.media.action.IMAGE_CAPTURE")
            val resolveInfo =
                packageManager.resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val defaultCameraPackage = resolveInfo!!.activityInfo.packageName
            appLaunchers.firstOrNull { it.packageName == defaultCameraPackage }?.apply {
                val cameraIcon =
                    HomeScreenGridItem(
                        id = null,
                        left = 4,
                        top = config.homeRowCount - 1,
                        right = 4,
                        bottom = config.homeRowCount - 1,
                        page = 0,
                        packageName = defaultCameraPackage,
                        activityName = "",
                        title = title,
                        type = ITEM_TYPE_ICON,
                        className = "",
                        widgetId = -1,
                        shortcutId = "",
                        icon = null,
                        docked = true,
                        parentId = null
                    )
                homeScreenGridItems.add(cameraIcon)
            }
        } catch (_: Exception) {
        }

        homeScreenGridItemsDB.insertAll(homeScreenGridItems)
    }

    fun handleWidgetBinding(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        appWidgetInfo: AppWidgetProviderInfo,
        callback: (canBind: Boolean) -> Unit,
    ) {
        actionOnCanBindWidget = null
        val canCreateWidget =
            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, appWidgetInfo.provider)
        if (canCreateWidget) {
            callback(true)
        } else {
            actionOnCanBindWidget = callback
            Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, appWidgetInfo.provider)
                startActivityForResult(this, REQUEST_ALLOW_BINDING_WIDGET)
            }
        }
    }

    fun handleWidgetConfigureScreen(
        appWidgetHost: AppWidgetHost,
        appWidgetId: Int,
        callback: (canBind: Boolean) -> Unit,
    ) {
        actionOnWidgetConfiguredWidget = callback
        appWidgetHost.startAppWidgetConfigureActivityForResult(
            this,
            appWidgetId,
            0,
            REQUEST_CONFIGURE_WIDGET,
            null
        )
    }

    fun handleShorcutCreation(
        activityInfo: ActivityInfo,
        callback: (shortcutId: String, label: String, icon: Drawable) -> Unit,
    ) {
        actionOnAddShortcut = callback
        val componentName = ComponentName(activityInfo.packageName, activityInfo.name)
        Intent(Intent.ACTION_CREATE_SHORTCUT).apply {
            component = componentName
            startActivityForResult(this, REQUEST_CREATE_SHORTCUT)
        }
    }

    private fun updateStatusBarIcons(backgroundColor: Int = getProperBackgroundColor()) {
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars =
            backgroundColor.getContrastColor() == DARK_GREY
    }

    // taken from https://gist.github.com/maxjvh/a6ab15cbba9c82a5065d
    private fun calculateAverageColor(bitmap: Bitmap): Int {
        var red = 0
        var green = 0
        var blue = 0
        val height = bitmap.height
        val width = bitmap.width
        var n = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 0
        while (i < pixels.size) {
            val color = pixels[i]
            red += Color.red(color)
            green += Color.green(color)
            blue += Color.blue(color)
            n++
            i += 1
        }

        return Color.rgb(red / n, green / n, blue / n)
    }
}
