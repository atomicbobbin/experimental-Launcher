package org.fossify.home.activities

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.fossify.commons.dialogs.RadioGroupDialog
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getProperPrimaryColor
import org.fossify.commons.extensions.updateTextColors
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.isTiramisuPlus
import org.fossify.commons.models.RadioItem
import org.fossify.home.R
import org.fossify.home.databinding.ActivitySettingsBinding
import org.fossify.home.extensions.config
import org.fossify.home.helpers.MAX_COLUMN_COUNT
import org.fossify.home.helpers.MAX_ROW_COUNT
import org.fossify.home.helpers.MIN_COLUMN_COUNT
import org.fossify.home.helpers.MIN_ROW_COUNT
import org.fossify.home.helpers.*
import org.fossify.home.core.ServiceLocator
import org.fossify.home.receivers.LockDeviceAdminReceiver
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : SimpleActivity() {

    private val binding by viewBinding(ActivitySettingsBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateMaterialActivityViews(
            mainCoordinatorLayout = binding.settingsCoordinator,
            nestedView = binding.settingsHolder,
            useTransparentNavigation = true,
            useTopSearchMenu = false
        )
        setupMaterialScrollListener(binding.settingsNestedScrollview, binding.settingsToolbar)
        setupOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.settingsToolbar, NavigationIcon.Arrow)
        refreshMenuItems()

        setupCustomizeColors()
        setupUseEnglish()
        setupDoubleTapToLock()
        setupCloseAppDrawerOnOtherAppOpen()
        setupOpenKeyboardOnAppDrawer()
        setupDrawerColumnCount()
        setupDrawerSortOrder()
        setupDrawerSearchBar()
        setupAutoAddNewApps()
        setupDynamicColors()
        setupThemedIcons()
        setupIconPack()
        setupIconShape()
        setupPredictiveSuggestions()
        setupSuggestionsCount()
        setupHomeRowCount()
        setupHomeColumnCount()
        setupHomeIconSize()
        setupDrawerIconSize()
        setupGridMargin()
        setupBlurEffects()
        setupBlurIntensity()
        setupTransitionEffects()
        setupNotificationBadges()
        setupBadgeStyle()
        setupLabelControls()
        setupFolderStyle()
        setupLockHomeLayout()
        setupLanguage()
        setupManageHiddenIcons()
        updateTextColors(binding.settingsHolder)

        arrayOf(
            binding.settingsColorCustomizationSectionLabel,
            binding.settingsGeneralSettingsLabel,
            binding.settingsDrawerSettingsLabel,
            binding.settingsHomeScreenLabel
        ).forEach {
            it.setTextColor(getProperPrimaryColor())
        }
    }

    private fun setupOptionsMenu() {
        binding.settingsToolbar.setOnMenuItemClickListener { _ -> false }
    }

    private fun refreshMenuItems() {
        // no-op: no menu items
    }

    private fun setupCustomizeColors() {
        binding.settingsColorCustomizationHolder.setOnClickListener {
            startCustomizationActivity()
        }
    }

    private fun setupUseEnglish() {
        binding.settingsUseEnglishHolder.beVisibleIf(
            beVisible = (config.wasUseEnglishToggled || Locale.getDefault().language != "en")
                    && !isTiramisuPlus()
        )

        binding.settingsUseEnglish.isChecked = config.useEnglish
        binding.settingsUseEnglishHolder.setOnClickListener {
            binding.settingsUseEnglish.toggle()
            config.useEnglish = binding.settingsUseEnglish.isChecked
            exitProcess(0)
        }
    }

    private fun setupDoubleTapToLock() {
        val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        binding.settingsDoubleTapToLock.isChecked = devicePolicyManager.isAdminActive(
            ComponentName(this, LockDeviceAdminReceiver::class.java)
        )

        binding.settingsDoubleTapToLockHolder.setOnClickListener {
            val isLockDeviceAdminActive = devicePolicyManager.isAdminActive(
                ComponentName(this, LockDeviceAdminReceiver::class.java)
            )
            if (isLockDeviceAdminActive) {
                devicePolicyManager.removeActiveAdmin(
                    ComponentName(this, LockDeviceAdminReceiver::class.java)
                )
            } else {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    ComponentName(this, LockDeviceAdminReceiver::class.java)
                )
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.lock_device_admin_hint)
                )
                startActivity(intent)
            }
        }
    }

    private fun setupOpenKeyboardOnAppDrawer() {
        binding.settingsOpenKeyboardOnAppDrawerHolder.beVisibleIf(config.showSearchBar)
        binding.settingsOpenKeyboardOnAppDrawer.isChecked = config.autoShowKeyboardInAppDrawer
        binding.settingsOpenKeyboardOnAppDrawerHolder.setOnClickListener {
            binding.settingsOpenKeyboardOnAppDrawer.toggle()
            config.autoShowKeyboardInAppDrawer = binding.settingsOpenKeyboardOnAppDrawer.isChecked
        }
    }

    private fun setupCloseAppDrawerOnOtherAppOpen() {
        binding.settingsCloseAppDrawerOnOtherApp.isChecked = config.closeAppDrawer
        binding.settingsCloseAppDrawerOnOtherAppHolder.setOnClickListener {
            binding.settingsCloseAppDrawerOnOtherApp.toggle()
            config.closeAppDrawer = binding.settingsCloseAppDrawerOnOtherApp.isChecked
        }
    }

    private fun setupAutoAddNewApps() {
        binding.settingsAutoAddNewApps.isChecked = config.autoAddNewApps
        binding.settingsAutoAddNewAppsHolder.setOnClickListener {
            binding.settingsAutoAddNewApps.toggle()
            config.autoAddNewApps = binding.settingsAutoAddNewApps.isChecked
        }
    }

    private fun setupDrawerColumnCount() {
        val currentColumnCount = config.drawerColumnCount
        binding.settingsDrawerColumnCount.text = currentColumnCount.toString()
        binding.settingsDrawerColumnCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 1..MAX_COLUMN_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.column_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentColumnCount) {
                val newColumnCount = it as Int
                if (currentColumnCount != newColumnCount) {
                    config.drawerColumnCount = newColumnCount
                    setupDrawerColumnCount()
                }
            }
        }
    }

    private fun setupDrawerSearchBar() {
        val showSearchBar = config.showSearchBar
        binding.settingsShowSearchBar.isChecked = showSearchBar
        binding.settingsDrawerSearchHolder.setOnClickListener {
            binding.settingsShowSearchBar.toggle()
            config.showSearchBar = binding.settingsShowSearchBar.isChecked
            binding.settingsOpenKeyboardOnAppDrawerHolder.beVisibleIf(config.showSearchBar)
        }
    }

    private fun setupDrawerSortOrder() {
        val currentMode = config.drawerSortMode
        binding.settingsDrawerSortOrder.text = when (currentMode) {
            1 -> getString(R.string.sort_most_used)
            2 -> getString(R.string.sort_recently_installed)
            else -> getString(R.string.sort_alphabetical)
        }
        binding.settingsDrawerSortOrderHolder.setOnClickListener {
            val items = listOf(
                RadioItem(0, getString(R.string.sort_alphabetical)),
                RadioItem(1, getString(R.string.sort_most_used)),
                RadioItem(2, getString(R.string.sort_recently_installed))
            )
            RadioGroupDialog(this, ArrayList(items), currentMode) { selected ->
                config.drawerSortMode = selected as Int
                setupDrawerSortOrder()
            }
        }
    }

    private fun setupHomeRowCount() {
        val currentRowCount = config.homeRowCount
        binding.settingsHomeScreenRowCount.text = currentRowCount.toString()
        binding.settingsHomeScreenRowCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in MIN_ROW_COUNT..MAX_ROW_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.row_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentRowCount) {
                val newRowCount = it as Int
                if (currentRowCount != newRowCount) {
                    config.homeRowCount = newRowCount
                    setupHomeRowCount()
                }
            }
        }
    }

    private fun setupHomeColumnCount() {
        val currentColumnCount = config.homeColumnCount
        binding.settingsHomeScreenColumnCount.text = currentColumnCount.toString()
        binding.settingsHomeScreenColumnCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in MIN_COLUMN_COUNT..MAX_COLUMN_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.column_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentColumnCount) {
                val newColumnCount = it as Int
                if (currentColumnCount != newColumnCount) {
                    config.homeColumnCount = newColumnCount
                    setupHomeColumnCount()
                }
            }
        }
    }

    private fun setupHomeIconSize() {
        val currentScale = config.homeIconSizeScale
        binding.settingsHomeIconSize.text = "${currentScale}%"
        binding.settingsHomeIconSizeHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (scale in MIN_ICON_SIZE_SCALE..MAX_ICON_SIZE_SCALE step 10) {
                items.add(RadioItem(scale, "${scale}%"))
            }
            RadioGroupDialog(this, items, currentScale) { selected ->
                config.homeIconSizeScale = selected as Int
                setupHomeIconSize()
                refreshHomeScreenGrid()
            }
        }
    }

    private fun setupDrawerIconSize() {
        val currentScale = config.drawerIconSizeScale
        binding.settingsDrawerIconSize.text = "${currentScale}%"
        binding.settingsDrawerIconSizeHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (scale in MIN_ICON_SIZE_SCALE..MAX_ICON_SIZE_SCALE step 10) {
                items.add(RadioItem(scale, "${scale}%"))
            }
            RadioGroupDialog(this, items, currentScale) { selected ->
                config.drawerIconSizeScale = selected as Int
                setupDrawerIconSize()
                refreshDrawerIcons()
            }
        }
    }

    private fun setupGridMargin() {
        val currentMargin = config.gridMarginSize
        binding.settingsGridMargin.text = "${currentMargin}dp"
        binding.settingsGridMarginHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (margin in MIN_GRID_MARGIN..MAX_GRID_MARGIN) {
                val label = if (margin == 0) "Default" else "${margin}dp"
                items.add(RadioItem(margin, label))
            }
            RadioGroupDialog(this, items, currentMargin) { selected ->
                config.gridMarginSize = selected as Int
                setupGridMargin()
                refreshHomeScreenGrid()
            }
        }
    }

    private fun refreshHomeScreenGrid() {
        // Notify MainActivity to refresh the home screen grid
        val intent = Intent("org.fossify.home.REFRESH_GRID")
        sendBroadcast(intent)
    }

    private fun refreshDrawerIcons() {
        // Notify MainActivity to refresh the drawer icons
        val intent = Intent("org.fossify.home.REFRESH_DRAWER")
        sendBroadcast(intent)
    }

    private fun setupBlurEffects() {
        // Only show blur settings if device supports it
        val supportsBlur = ServiceLocator.deviceCapabilities.supportsRenderEffectBlur
        binding.settingsVisualEffectsLabel.beVisibleIf(supportsBlur)
        binding.settingsBlurEffectsHolder.beVisibleIf(supportsBlur)
        binding.settingsBlurIntensityHolder.beVisibleIf(supportsBlur)

        if (!supportsBlur) return

        binding.settingsBlurEffects.isChecked = config.enableBlurEffects
        binding.settingsBlurEffectsHolder.setOnClickListener {
            binding.settingsBlurEffects.toggle()
            config.enableBlurEffects = binding.settingsBlurEffects.isChecked
            refreshBlurEffects()
            setupBlurIntensity() // Update intensity visibility
        }
    }

    private fun setupBlurIntensity() {
        val supportsBlur = ServiceLocator.deviceCapabilities.supportsRenderEffectBlur
        val blurEnabled = config.enableBlurEffects
        binding.settingsBlurIntensityHolder.beVisibleIf(supportsBlur && blurEnabled)

        if (!supportsBlur || !blurEnabled) return

        val currentIntensity = config.blurIntensity
        binding.settingsBlurIntensity.text = currentIntensity.toString()
        binding.settingsBlurIntensityHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (intensity in MIN_BLUR_INTENSITY..MAX_BLUR_INTENSITY step 5) {
                items.add(RadioItem(intensity, intensity.toString()))
            }
            RadioGroupDialog(this, items, currentIntensity) { selected ->
                config.blurIntensity = selected as Int
                setupBlurIntensity()
                refreshBlurEffects()
            }
        }
    }

    private fun refreshBlurEffects() {
        // Notify MainActivity to refresh blur effects
        val intent = Intent("org.fossify.home.REFRESH_BLUR")
        sendBroadcast(intent)
    }

    private fun setupTransitionEffects() {
        val currentMode = config.transitionEffectMode
        binding.settingsTransitionEffects.text = when (currentMode) {
            TRANSITION_NONE -> getString(R.string.transition_none)
            TRANSITION_FADE -> getString(R.string.transition_fade)
            TRANSITION_SLIDE -> getString(R.string.transition_slide)
            TRANSITION_ZOOM -> getString(R.string.transition_zoom)
            TRANSITION_FLIP -> getString(R.string.transition_flip)
            else -> getString(R.string.transition_slide)
        }
        
        binding.settingsTransitionEffectsHolder.setOnClickListener {
            val items = listOf(
                RadioItem(TRANSITION_NONE, getString(R.string.transition_none)),
                RadioItem(TRANSITION_FADE, getString(R.string.transition_fade)),
                RadioItem(TRANSITION_SLIDE, getString(R.string.transition_slide)),
                RadioItem(TRANSITION_ZOOM, getString(R.string.transition_zoom)),
                RadioItem(TRANSITION_FLIP, getString(R.string.transition_flip))
            )
            RadioGroupDialog(this, ArrayList(items), currentMode) { selected ->
                config.transitionEffectMode = selected as Int
                setupTransitionEffects()
                refreshTransitionEffects()
            }
        }
    }

    private fun refreshTransitionEffects() {
        // Notify MainActivity to refresh transition effects
        val intent = Intent("org.fossify.home.REFRESH_TRANSITIONS")
        sendBroadcast(intent)
    }

    private fun setupNotificationBadges() {
        binding.settingsNotificationBadges.isChecked = config.enableNotificationBadges
        binding.settingsNotificationBadgesHolder.setOnClickListener {
            if (!binding.settingsNotificationBadges.isChecked) {
                // Request notification listener permission
                requestNotificationListenerPermission()
            } else {
                // Disable badges
                binding.settingsNotificationBadges.toggle()
                config.enableNotificationBadges = binding.settingsNotificationBadges.isChecked
                setupBadgeStyle() // Update badge style visibility
                refreshNotificationBadges()
            }
        }
    }

    private fun setupBadgeStyle() {
        val badgesEnabled = config.enableNotificationBadges
        binding.settingsBadgeStyleHolder.beVisibleIf(badgesEnabled)

        if (!badgesEnabled) return

        val currentStyle = config.notificationBadgeStyle
        binding.settingsBadgeStyle.text = when (currentStyle) {
            BADGE_STYLE_DOT -> getString(R.string.badge_style_dot)
            BADGE_STYLE_COUNT -> getString(R.string.badge_style_count)
            BADGE_STYLE_LARGE_DOT -> getString(R.string.badge_style_large_dot)
            else -> getString(R.string.badge_style_dot)
        }
        
        binding.settingsBadgeStyleHolder.setOnClickListener {
            val items = listOf(
                RadioItem(BADGE_STYLE_DOT, getString(R.string.badge_style_dot)),
                RadioItem(BADGE_STYLE_COUNT, getString(R.string.badge_style_count)),
                RadioItem(BADGE_STYLE_LARGE_DOT, getString(R.string.badge_style_large_dot))
            )
            RadioGroupDialog(this, ArrayList(items), currentStyle) { selected ->
                config.notificationBadgeStyle = selected as Int
                setupBadgeStyle()
                refreshNotificationBadges()
            }
        }
    }

    private fun requestNotificationListenerPermission() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(R.string.notification_access_required)
        dialogBuilder.setMessage(R.string.notification_access_description)
        dialogBuilder.setPositiveButton(R.string.grant_access) { _, _ ->
            // Open notification listener settings
            try {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback to general settings
                val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
        }
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.show()
    }

    private fun refreshNotificationBadges() {
        // Notify MainActivity to refresh notification badges
        val intent = Intent("org.fossify.home.REFRESH_BADGES")
        sendBroadcast(intent)
    }

    override fun onResume() {
        super.onResume()
        // Check if notification listener permission was granted
        if (isNotificationListenerEnabled()) {
            if (!config.enableNotificationBadges && binding.settingsNotificationBadges.isChecked) {
                config.enableNotificationBadges = true
                setupBadgeStyle()
                refreshNotificationBadges()
            }
        } else {
            if (config.enableNotificationBadges) {
                config.enableNotificationBadges = false
                binding.settingsNotificationBadges.isChecked = false
                setupBadgeStyle()
                refreshNotificationBadges()
            }
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val packageName = packageName
        val flat = android.provider.Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(packageName) == true
    }

    private fun setupLabelControls() {
        binding.settingsDrawerLabelVisibility.isChecked = config.drawerLabelVisible
        binding.settingsDrawerLabelVisibilityHolder.setOnClickListener {
            binding.settingsDrawerLabelVisibility.toggle()
            config.drawerLabelVisible = binding.settingsDrawerLabelVisibility.isChecked
        }

        binding.settingsDrawerLabelSize.text = config.drawerLabelSizeSp.toString()
        binding.settingsDrawerLabelSizeHolder.setOnClickListener {
            val sizes = (8..18).toList()
            val items = ArrayList<RadioItem>()
            sizes.forEach { sp -> items.add(RadioItem(sp, sp.toString())) }
            RadioGroupDialog(this, items, config.drawerLabelSizeSp) { selected ->
                config.drawerLabelSizeSp = selected as Int
                setupLabelControls()
            }
        }

        binding.settingsHomeLabelVisibility.isChecked = config.homeLabelVisible
        binding.settingsHomeLabelVisibilityHolder.setOnClickListener {
            binding.settingsHomeLabelVisibility.toggle()
            config.homeLabelVisible = binding.settingsHomeLabelVisibility.isChecked
        }

        binding.settingsHomeLabelSize.text = config.homeLabelSizeSp.toString()
        binding.settingsHomeLabelSizeHolder.setOnClickListener {
            val sizes = (8..18).toList()
            val items = ArrayList<RadioItem>()
            sizes.forEach { sp -> items.add(RadioItem(sp, sp.toString())) }
            RadioGroupDialog(this, items, config.homeLabelSizeSp) { selected ->
                config.homeLabelSizeSp = selected as Int
                setupLabelControls()
            }
        }
    }

    private fun setupFolderStyle() {
        val styles = ArrayList<RadioItem>().apply {
            add(RadioItem(0, getString(R.string.folder_style_default)))
            add(RadioItem(1, getString(R.string.folder_style_soft_round)))
            add(RadioItem(2, getString(R.string.folder_style_translucent)))
        }
        binding.settingsFolderStyle.text = styles.firstOrNull { it.id == config.folderStylePreset }?.title
            ?: getString(R.string.folder_style_default)
        binding.settingsFolderStyleHolder.setOnClickListener {
            RadioGroupDialog(this, styles, config.folderStylePreset) { selected ->
                config.folderStylePreset = selected as Int
                setupFolderStyle()
            }
        }
    }

    private fun setupLockHomeLayout() {
        binding.settingsLockHomeLayout.isChecked = config.lockHomeLayout
        binding.settingsLockHomeLayoutHolder.setOnClickListener {
            binding.settingsLockHomeLayout.toggle()
            config.lockHomeLayout = binding.settingsLockHomeLayout.isChecked
        }
    }

    @SuppressLint("NewApi")
    private fun setupLanguage() {
        binding.settingsLanguage.text = Locale.getDefault().displayLanguage
        binding.settingsLanguageHolder.beVisibleIf(isTiramisuPlus())
        binding.settingsLanguageHolder.setOnClickListener {
            launchChangeAppLanguageIntent()
        }
    }

    private fun setupManageHiddenIcons() {
        binding.settingsManageHiddenIconsHolder.setOnClickListener {
            startActivity(Intent(this, HiddenIconsActivity::class.java))
        }
    }

    // Backup/Restore UI hooks (placed under General section via toolbar menu in future)
    private fun exportBackup(uri: Uri) {
        val json = org.fossify.home.core.ServiceLocator.backupManager.exportJson()
        contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
    }

    private fun importBackup(uri: Uri, dryRun: Boolean): Boolean {
        val json = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: return false
        val obj = org.json.JSONObject(json)
        val bundle = org.fossify.home.backup.BackupManager.ExportBundle(
            version = obj.getInt("version"),
            settings = obj.getJSONObject("settings").let { jo ->
                jo.keys().asSequence().associateWith { jo.get(it) }
            },
            layout = obj.getJSONObject("layout")
        )
        return org.fossify.home.core.ServiceLocator.backupManager.import(bundle, dryRun)
    }

    private fun setupDynamicColors() {
        binding.settingsDynamicColors.isChecked = config.useDynamicColors
        binding.settingsDynamicColorsHolder.setOnClickListener {
            binding.settingsDynamicColors.toggle()
            config.useDynamicColors = binding.settingsDynamicColors.isChecked
        }
    }

    private fun setupThemedIcons() {
        binding.settingsThemedIcons.isChecked = config.useThemedIcons
        binding.settingsThemedIconsHolder.setOnClickListener {
            binding.settingsThemedIcons.toggle()
            config.useThemedIcons = binding.settingsThemedIcons.isChecked
        }
    }

    private fun setupIconPack() {
        val current = if (config.iconPackPackage.isEmpty()) getString(R.string.shape_default) else config.iconPackPackage
        binding.settingsIconPack.text = current
        binding.settingsIconPackHolder.setOnClickListener {
            // Placeholder: future icon pack picker
        }
    }

    private fun setupIconShape() {
        binding.settingsIconShape.text = when (config.iconShapeMode) {
            1 -> getString(R.string.shape_circle)
            2 -> getString(R.string.shape_rounded)
            3 -> getString(R.string.shape_squircle)
            else -> getString(R.string.shape_default)
        }
        binding.settingsIconShapeHolder.setOnClickListener {
            val items = arrayListOf(
                RadioItem(0, getString(R.string.shape_default)),
                RadioItem(1, getString(R.string.shape_circle)),
                RadioItem(2, getString(R.string.shape_rounded)),
                RadioItem(3, getString(R.string.shape_squircle)),
            )
            RadioGroupDialog(this, items, config.iconShapeMode) { selected ->
                config.iconShapeMode = selected as Int
                setupIconShape()
            }
        }
    }

    private fun setupPredictiveSuggestions() {
        binding.settingsPredictiveSuggestions.isChecked = config.predictiveSuggestionsEnabled
        binding.settingsPredictiveSuggestionsHolder.setOnClickListener {
            binding.settingsPredictiveSuggestions.toggle()
            config.predictiveSuggestionsEnabled = binding.settingsPredictiveSuggestions.isChecked
        }
    }

    private fun setupSuggestionsCount() {
        binding.settingsSuggestionsCount.text = config.predictiveSuggestionsCount.toString()
        binding.settingsSuggestionsCountHolder.setOnClickListener {
            val options = arrayListOf(
                RadioItem(0, "2"),
                RadioItem(1, "4"),
                RadioItem(2, "6"),
                RadioItem(3, "8"),
            )
            val currentIndex = when (config.predictiveSuggestionsCount) {
                2 -> 0
                4 -> 1
                6 -> 2
                8 -> 3
                else -> 1
            }
            RadioGroupDialog(this, options, currentIndex) { selected ->
                config.predictiveSuggestionsCount = listOf(2, 4, 6, 8)[selected as Int]
                setupSuggestionsCount()
            }
        }
    }

    // About screen removed for a blank, unbranded experience.
}
