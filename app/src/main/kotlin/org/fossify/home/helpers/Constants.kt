package org.fossify.home.helpers

const val WIDGET_LIST_SECTION = 0
const val WIDGET_LIST_ITEMS_HOLDER = 1

const val REPOSITORY_NAME = "Launcher"

// shared prefs
const val WAS_HOME_SCREEN_INIT = "was_home_screen_init"
const val HOME_ROW_COUNT = "home_row_count"
const val HOME_COLUMN_COUNT = "home_column_count"
const val DRAWER_COLUMN_COUNT = "drawer_column_count"
const val SHOW_SEARCH_BAR = "show_search_bar"
const val CLOSE_APP_DRAWER = "close_app_drawer"
const val AUTO_SHOW_KEYBOARD_IN_APP_DRAWER = "auto_show_keyboard_in_app_drawer"
const val LOCK_HOME_LAYOUT = "lock_home_layout"
const val DRAWER_SORT_MODE = "drawer_sort_mode"
const val AUTO_ADD_NEW_APPS = "auto_add_new_apps"
const val DRAWER_LABEL_VISIBLE = "drawer_label_visible"
const val DRAWER_LABEL_SIZE_SP = "drawer_label_size_sp"
const val HOME_LABEL_VISIBLE = "home_label_visible"
const val HOME_LABEL_SIZE_SP = "home_label_size_sp"
const val FOLDER_STYLE_PRESET = "folder_style_preset"
const val USE_DYNAMIC_COLORS = "use_dynamic_colors"
const val USE_THEMED_ICONS = "use_themed_icons"
const val ICON_PACK_PACKAGE = "icon_pack_package"
const val ICON_SHAPE_MODE = "icon_shape_mode" // 0 default, 1 circle, 2 rounded, 3 squircle
const val PREDICTIVE_SUGGESTIONS_ENABLED = "predictive_suggestions_enabled"
const val PREDICTIVE_SUGGESTIONS_COUNT = "predictive_suggestions_count"
const val HOME_ICON_SIZE_SCALE = "home_icon_size_scale"
const val DRAWER_ICON_SIZE_SCALE = "drawer_icon_size_scale"
const val GRID_MARGIN_SIZE = "grid_margin_size"
const val ENABLE_BLUR_EFFECTS = "enable_blur_effects"
const val BLUR_INTENSITY = "blur_intensity"
const val TRANSITION_EFFECT_MODE = "transition_effect_mode"
const val ENABLE_NOTIFICATION_BADGES = "enable_notification_badges"
const val NOTIFICATION_BADGE_STYLE = "notification_badge_style"

// default home screen grid size
const val ROW_COUNT = 6
const val COLUMN_COUNT = 5
const val MIN_ROW_COUNT = 2
const val MAX_ROW_COUNT = 15
const val MIN_COLUMN_COUNT = 2
const val MAX_COLUMN_COUNT = 15

const val UNINSTALL_APP_REQUEST_CODE = 50
const val REQUEST_CONFIGURE_WIDGET = 51
const val REQUEST_ALLOW_BINDING_WIDGET = 52
const val REQUEST_CREATE_SHORTCUT = 53
const val REQUEST_SET_DEFAULT = 54

const val ITEM_TYPE_ICON = 0
const val ITEM_TYPE_WIDGET = 1
const val ITEM_TYPE_SHORTCUT = 2
const val ITEM_TYPE_FOLDER = 3

const val WIDGET_HOST_ID = 12345
const val MAX_CLICK_DURATION = 150

// Icon size scale constants
const val DEFAULT_ICON_SIZE_SCALE = 100 // 100% = default size
const val MIN_ICON_SIZE_SCALE = 50      // 50% = minimum size
const val MAX_ICON_SIZE_SCALE = 150     // 150% = maximum size

// Grid margin constants  
const val DEFAULT_GRID_MARGIN = 0       // 0 = default margin
const val MIN_GRID_MARGIN = -10         // -10dp = tighter spacing
const val MAX_GRID_MARGIN = 20          // +20dp = looser spacing

// Blur constants
const val DEFAULT_BLUR_INTENSITY = 15   // 15 = default blur radius
const val MIN_BLUR_INTENSITY = 5        // 5 = minimum blur
const val MAX_BLUR_INTENSITY = 30       // 30 = maximum blur

// Transition effect constants
const val TRANSITION_NONE = 0            // 0 = no enhanced effects
const val TRANSITION_FADE = 1            // 1 = fade transition
const val TRANSITION_SLIDE = 2           // 2 = slide transition  
const val TRANSITION_ZOOM = 3            // 3 = zoom transition
const val TRANSITION_FLIP = 4            // 4 = flip transition
const val DEFAULT_TRANSITION_MODE = TRANSITION_SLIDE

// Notification badge constants
const val BADGE_STYLE_DOT = 0            // 0 = simple dot indicator
const val BADGE_STYLE_COUNT = 1          // 1 = numeric count
const val BADGE_STYLE_LARGE_DOT = 2      // 2 = larger dot indicator
const val DEFAULT_BADGE_STYLE = BADGE_STYLE_DOT
