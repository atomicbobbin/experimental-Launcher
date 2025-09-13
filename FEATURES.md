## Fossify Launcher — Feature List (auto-maintained)

- **Update policy**: Keep this file in sync with the codebase. Update whenever features are added/removed.

- **Core**
  - Default HOME launcher (handles MAIN + HOME; `singleTask`; excluded from recents)
  - Privacy-first: no network access, no ads, no tracking
  - Key permissions: `QUERY_ALL_PACKAGES` (enumerate launchables), `BIND_APPWIDGET` (widgets), `REQUEST_DELETE_PACKAGES` (uninstall), `EXPAND_STATUS_BAR` (expand shade), device admin for force-lock

- **Home screen**
  - Configurable grid size: rows 2–15, columns 2–15; live reflow and redraw
  - Multi-page desktop: horizontal swipe, animated page indicators, auto-delete empty pages
  - Bottom dock row (last grid row); items marked as docked stick to bottom across pages
  - First-run dock auto-population (if available): dialer, SMS, browser, store (Play/F-Droid/Aurora), camera
  - Portrait orientation enforced on MainActivity

- **Folders**
  - Drag-to-create folders by dropping an icon onto another
  - Capacity up to 16 items; auto grid layout; composite circular folder icon preview
  - Animated open/close; drag in/out; maintain intra-folder order; auto-delete empty folders

- **Widgets & shortcuts**
  - Full AppWidget host with binding and optional configure flow
  - Dedicated Widgets screen grouped by app with previews (incl. “widgets that are shortcuts”)
  - Place, move, and resize widgets within grid constraints; prevents overlap/out-of-bounds
  - Pinned shortcuts: accepts CONFIRM_PIN_SHORTCUT; stores shortcut icon bitmaps for persistence

- **App Drawer (All Apps)**
  - Grid with configurable column count (independent of desktop)
  - Fast scroller with index bubble; smooth animations; no item animator for performance
  - Search bar (toggleable); live filtering by title; optional auto-show keyboard on open
  - Alphabetical sorting with locale normalization; deterministic tiebreak by package name
  - Optional “close drawer on app launch” behavior

- **Gestures**
  - Fling up: open App Drawer
  - Fling down: close panels or expand the notification shade
  - Fling left/right: switch desktop pages
  - Double-tap on empty area: lock screen (requires device admin enabled)

- **Context menus (long-press)**
  - App icons (home): Rename, Remove, Uninstall, App info, dynamic app shortcuts submenu
  - App icons (drawer): Hide icon, dynamic app shortcuts submenu
  - Widgets: Resize, Remove

- **Hidden apps**
  - Hide from App Drawer via context menu; persisted in DB
  - "Manage Hidden Icons" screen with multi-select unhide; cleans up invalid entries

- **Settings**
  - Color customization (opens Fossify Customization activity)
  - Language controls: pre-Android 13 “Use English” toggle; Android 13+ launches system app-language picker
  - Drawer: column count; show/hide search bar; auto-show keyboard; close drawer on app open
  - Home screen: row count; column count
  - Double-tap-to-lock toggle with device admin request UI and explanations

- **Accessibility & UX**
  - ExploreByTouch accessibility helper for the grid; enlarged clickable regions (icon+label)
  - Automatic status bar icon contrast against background; semi-transparent nav bar when panels open
  - Haptic feedback on key interactions; decelerate/overshoot animations; anchored popup menus

