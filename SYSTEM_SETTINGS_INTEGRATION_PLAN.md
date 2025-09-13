# Smart Launcher-Style Settings Restructure [f041]

## Overview
Restructure the launcher's settings interface to match Smart Launcher's intuitive, categorized approach with better organization, visual hierarchy, and user experience.

## Current State vs Target State

### Current State
- Single monolithic SettingsActivity with all options in one long list
- Basic linear layout with simple text labels
- No visual hierarchy or logical grouping
- Limited visual feedback and preview capabilities

### Target State (Smart Launcher Style)
- Categorized settings with clear visual sections
- Card-based layout with icons and descriptions
- Logical grouping by functionality (Home Screen, App Drawer, Appearance, etc.)
- Live previews and visual feedback for changes
- Modern Material Design 3 interface
- Quick access shortcuts for common settings

## Technical Implementation Plan

### Phase 1: Settings UI Architecture Redesign

**1. Create Settings Category Model**
```kotlin
// New file: app/src/main/kotlin/org/fossify/home/settings/SettingsCategory.kt
data class SettingsCategory(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val iconRes: Int,
    val settingsCount: Int,
    val targetActivity: Class<*>
)

data class SettingItem(
    val key: String,
    val titleRes: Int,
    val summaryRes: Int,
    val iconRes: Int,
    val type: SettingType,
    val currentValue: String,
    val previewImageRes: Int? = null
)

enum class SettingType {
    TOGGLE, SLIDER, SELECTION, ACTION, NAVIGATION
}
```

**2. Main Settings Dashboard**
```kotlin
// Replace current SettingsActivity with dashboard-style main screen
class SettingsDashboardActivity : SimpleActivity() {
    private val categories = listOf(
        SettingsCategory("home_screen", R.string.home_screen, R.string.home_screen_desc, 
                        R.drawable.ic_home, 6, HomeScreenSettingsActivity::class.java),
        SettingsCategory("app_drawer", R.string.app_drawer, R.string.app_drawer_desc,
                        R.drawable.ic_drawer, 5, AppDrawerSettingsActivity::class.java),
        SettingsCategory("appearance", R.string.appearance, R.string.appearance_desc,
                        R.drawable.ic_palette, 8, AppearanceSettingsActivity::class.java),
        SettingsCategory("gestures", R.string.gestures, R.string.gestures_desc,
                        R.drawable.ic_gesture, 4, GestureSettingsActivity::class.java)
    )
}
```

### Phase 2: Card-Based Dashboard Layout

**1. Dashboard Layout (activity_settings_dashboard.xml)**
```xml
<!-- New file: app/src/main/res/layout/activity_settings_dashboard.xml -->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorSurface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- Quick Actions Card -->
        <com.google.android.material.card.MaterialCardView
            style="@style/SettingsCardStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <!-- Quick toggles for common settings -->
        </com.google.android.material.card.MaterialCardView>

        <!-- Home Screen Category -->
        <com.google.android.material.card.MaterialCardView
            style="@style/SettingsCardStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <!-- Grid size, icon size, margins, layout lock -->
        </com.google.android.material.card.MaterialCardView>

        <!-- App Drawer Category -->
        <com.google.android.material.card.MaterialCardView
            style="@style/SettingsCardStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <!-- Column count, search, sorting, hidden apps -->
        </com.google.android.material.card.MaterialCardView>

        <!-- Appearance Category -->
        <com.google.android.material.card.MaterialCardView
            style="@style/SettingsCardStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <!-- Themes, blur, transitions, icon packs -->
        </com.google.android.material.card.MaterialCardView>

    </LinearLayout>
</ScrollView>
```

**2. Settings Card Component**
```kotlin
// New file: app/src/main/kotlin/org/fossify/home/views/SettingsCard.kt
class SettingsCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    
    fun setupCategory(category: SettingsCategory) {
        // Setup card with icon, title, description, settings count
        // Add click listener to navigate to category activity
        // Show preview of current settings state
    }
    
    fun setupQuickToggle(setting: SettingItem) {
        // Setup quick toggle switches for common settings
        // Handle immediate state changes
        // Show visual feedback
    }
}

### Phase 3: Categorized Settings Activities

**1. HomeScreenSettingsActivity** - Layout & Grid
```kotlin
class HomeScreenSettingsActivity : SimpleActivity() {
    // Grid size with live preview
    // Icon size scaling with visual feedback
    // Grid margins with spacing visualization
    // Layout lock toggle
    // Page management options
}
```

**2. AppDrawerSettingsActivity** - Drawer Behavior  
```kotlin
class AppDrawerSettingsActivity : SimpleActivity() {
    // Column count with preview
    // Search bar toggle
    // Sort order selection
    // Auto-show keyboard
    // Close drawer on app launch
    // Hidden apps management (link to HiddenIconsActivity)
}
```

**3. AppearanceSettingsActivity** - Visual Customization
```kotlin
class AppearanceSettingsActivity : SimpleActivity() {
    // Theme and color customization
    // Dynamic colors toggle
    // Blur effects (with capability check)
    // Transition effects selection
    // Icon pack selection (when implemented)
    // Icon shape controls
    // Folder style presets
}
```

**4. GestureSettingsActivity** - Interactions
```kotlin
class GestureSettingsActivity : SimpleActivity() {
    // Double-tap to lock
    // Gesture customization (future)
    // Swipe actions (future)
    // Notification badges
    // Advanced interaction settings
}
```

### Phase 4: Enhanced UI Components

**1. Live Preview Components**
```kotlin
// New file: app/src/main/kotlin/org/fossify/home/views/GridPreviewView.kt
class GridPreviewView : View {
    // Mini home screen preview showing current grid layout
    // Updates in real-time as user changes settings
    // Shows icon size and margin changes visually
}

// New file: app/src/main/kotlin/org/fossify/home/views/IconSizePreview.kt  
class IconSizePreview : LinearLayout {
    // Shows sample icons at different sizes
    // Real-time scaling as user adjusts slider
    // Before/after comparison
}
```

**2. Smart Setting Controls**
```kotlin
// Enhanced slider with preview
class PreviewSlider : MaterialSlider {
    var previewView: View? = null
    
    fun setupWithPreview(preview: View, updateCallback: (Float) -> Unit) {
        // Link slider to preview view
        // Update preview in real-time
        // Show value labels and descriptions
    }
}

// Toggle with visual feedback
class SmartToggle : MaterialSwitch {
    fun setupWithDescription(descriptionText: String, previewCallback: (Boolean) -> Unit) {
        // Enhanced toggle with description
        // Visual state feedback
        // Immediate preview updates
    }
}
```

### Phase 5: Settings Categories Implementation

**1. Quick Actions Dashboard**
```kotlin
// Top section with most-used toggles
class QuickActionsCard : SettingsCard {
    // Layout lock toggle
    // Dynamic colors toggle  
    // Blur effects toggle
    // Search bar toggle
    // Auto-add apps toggle
}
```

**2. Category-Specific Activities**
```kotlin
class HomeScreenSettingsActivity : SimpleActivity() {
    private lateinit var gridPreview: GridPreviewView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup live preview
        gridPreview.updateFromConfig(config)
        
        // Setup sliders with real-time preview
        setupGridSizeControls()
        setupIconSizeControls() 
        setupMarginControls()
    }
    
    private fun setupGridSizeControls() {
        // Row count slider with live preview
        // Column count slider with live preview
        // Visual grid overlay on preview
    }
}
```

## Implementation Benefits

### User Experience
- **Intuitive Organization**: Settings grouped by logical categories
- **Visual Hierarchy**: Clear card-based layout with icons and descriptions
- **Live Previews**: Real-time visual feedback for changes
- **Quick Access**: Dashboard with most-used settings readily available
- **Professional Feel**: Modern Material Design 3 interface

### Technical Benefits
- **Modular Architecture**: Settings split into focused, maintainable activities
- **Reusable Components**: Custom views for consistent UI patterns
- **Performance**: Lazy loading of settings categories
- **Extensibility**: Easy to add new categories and settings

### Competitive Advantage
- **Smart Launcher Parity**: Matches premium launcher UX quality
- **Modern Design**: Contemporary interface that feels current
- **User-Friendly**: Reduces cognitive load with better organization

## Development Phases

### Phase 1: UI Architecture 
- Create settings category models and data structures
- Design card-based dashboard layout
- Implement SettingsCard custom view component
- Create base activity classes for categorized settings

### Phase 2: Dashboard Implementation 
- Replace current SettingsActivity with dashboard approach
- Implement category cards with navigation
- Add quick actions section with common toggles
- Create visual icons and descriptions for each category

### Phase 3: Category Activities 
- Split settings into focused activities:
  - HomeScreenSettingsActivity (grid, icons, layout)
  - AppDrawerSettingsActivity (drawer behavior, search)
  - AppearanceSettingsActivity (themes, effects, visual)
  - GestureSettingsActivity (interactions, badges)
- Implement live preview components
- Add enhanced UI controls with visual feedback

### Phase 4: Polish & Enhancement 
- Add smooth transitions between settings screens
- Implement search within settings
- Add settings backup/restore integration
- Performance optimization and testing

## Smart Launcher UX Patterns to Implement

### Dashboard Organization
- **Quick Actions**: Most-used toggles at the top
- **Visual Categories**: Cards with icons, titles, descriptions, and setting counts
- **Status Indicators**: Show current state of key settings
- **Search**: Quick search within launcher settings

### Enhanced Controls
- **Live Previews**: Visual feedback for grid, icon size, blur effects
- **Smart Sliders**: Value labels, descriptions, and preview updates
- **Toggle Groups**: Related settings grouped together
- **Reset Options**: Easy way to restore defaults

### Navigation Flow
- **Breadcrumb Navigation**: Clear path back to dashboard
- **Related Settings**: Quick access to related options
- **Apply/Preview**: Changes with preview before applying
- **Help Text**: Contextual descriptions for complex settings

## Success Metrics

### User Experience
- ✅ Intuitive settings organization matching Smart Launcher
- ✅ Reduced time to find and change settings
- ✅ Visual feedback for all setting changes
- ✅ Professional, modern interface design

### Technical
- ✅ Modular, maintainable settings architecture
- ✅ Reusable UI components for consistency
- ✅ Performance optimized with lazy loading
- ✅ Backward compatibility with current settings

### Competitive Positioning
- ✅ Smart Launcher-level UX quality
- ✅ Modern Material Design 3 interface
- ✅ Enhanced discoverability and usability

This restructure will transform the settings experience from a basic list to an intuitive, visually appealing dashboard that matches the quality and organization of premium launchers like Smart Launcher.
