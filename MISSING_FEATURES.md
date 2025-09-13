## Missing Features — Compared to Popular Android Launchers

This file lists features present in Smart Launcher, Samsung One UI Home, Pixel Launcher, and Nova Launcher that are currently NOT implemented in this app. Use it as a living backlog; update when features are added or deemed out of scope.

### Smart Launcher
- Deeper gesture customization (map multiple gestures to actions)
- Icon pack support and adaptive icon shape controls
- Wallpaper-adaptive theming (ambient colors beyond manual color customization)
- Biometric/PIN protection for hidden/locked apps
- Unified “smart search” beyond app titles (e.g., contacts/web/actions)
- Automatic app categorization in the App Drawer (smart categories)
- Popup widgets attached to app icons
- Contextual "Smart Page" with clock/weather and suggestions

### Samsung One UI Home
- Lock Home screen layout (prevent moves/edits)
- Auto-add newly installed apps to Home (optional)
- Extended folder/icon style customization options
- Home screen landscape rotation support
- App drawer sorting modes (A–Z, most used, recently installed)
- Background blur/translucency for folders and App Drawer
- Toggle between App Drawer mode and Home-only mode
- App icon badges (unread dots/counters)
- Stackable "Smart Widgets" (widget stacks)
- App Pairs (launch split-screen pairs)
- Edge Panels and system-level One-handed mode integrations (OEM-specific)

### Google Pixel Launcher
- Themed icons (Monochrome/Monet-tinted for supported apps)
- Predictive app suggestions (dock and app drawer top row)
- Full Material You (Monet) dynamic theming across launcher surfaces
- Unified device/web search with app actions and on-device results
- "At a Glance" widget with smart signals (calendar, weather, commute, timers, etc.)
- Google Discover feed on left screen

### Nova Launcher
- Desktop/drawer transition/scroll effects
- Icon pack support; custom icon shapes/sizes; label styling/visibility controls
- Extensive gesture mapping (pinch, double-swipe, etc.) to arbitrary actions
- Backup and restore of launcher layout and settings
- Per-icon and per-folder swipe actions (secondary action when swiping on an icon)
- Dock pages and deeper dock customizations
- Popup widgets attached to app icons
- Notification badges/dots with multiple providers/styles
- Drawer tabs/groups; advanced drawer layouts (vertical/horizontal/list) and background customization
- Subgrid positioning (place items between cells for fine layout)
- Third-party deep search integration (e.g., Sesame) for shortcuts and in-app results

### Implementation Risks & Solutions
- Icon pack support
  - Risk: Parsing diverse third-party icon pack formats; runtime performance and memory overhead for icon bitmaps.
  - Solution: Use a well-maintained icon pack parsing library; cache rasterized icons with versioned keys; provide a "Reset icons" tool; do work off the main thread.
- Notification badges/dots
  - Risk: Requires a NotificationListenerService, OEM variations, and background restrictions; number vs dot styles.
  - Solution: Make the listener optional with clear opt-in; support dot and numeric styles; use a robust provider abstraction and graceful fallbacks when the listener is disabled.
- Unified on-device/web search
  - Risk: Large indexes (apps, contacts, settings) can impact startup and privacy expectations.
  - Solution: Index incrementally; gate providers behind toggles; respect runtime permissions (Contacts); keep everything offline; provide per-source enable/disable and clear-index actions.
- Predictive app suggestions
  - Risk: Requires PACKAGE_USAGE_STATS; users may decline; can feel intrusive.
  - Solution: Make suggestions opt-in; use simple recency/frequency heuristics; degrade to static suggestions when permission is absent.
- Automatic app categorization
  - Risk: Accuracy varies; many apps lack reliable category metadata; no network access policy limits remote classification.
  - Solution: Prefer ApplicationInfo.category when available; supplement with local heuristics and on-device ML only if kept fully offline; always allow manual overrides.
- "At a Glance" and smart signals
  - Risk: Pixel’s Smartspace uses proprietary providers (weather, commute); replicating parity is difficult.
  - Solution: Build an open equivalent that surfaces calendar events, alarms, timers, and local weather via user-selected providers; make integrations modular and optional.
- Google Discover feed
  - Risk: Requires Google app and private APIs; may violate distribution policies if reverse-engineered.
  - Solution: Ship as an optional companion plugin that the user installs separately; expose a left-page provider API so alternative feeds (e.g., RSS) can be used.
- Home screen landscape rotation
  - Risk: Widget and layout constraints differ across orientations; clipping/overlap risk.
  - Solution: Maintain per-orientation grid metrics and positions; remeasure widgets on rotation; test across API levels and aspect ratios.
- Lock Home screen layout
  - Risk: Users may be confused why icons cannot be moved; accidental lock-in.
  - Solution: Add a quick toggle with snackbar hint and a temporary unlock gesture; block drags at the controller level.
- Drawer tabs/groups
  - Risk: Significant model and UI complexity; migration of existing hidden apps/sort settings.
  - Solution: Introduce a normalized data model (apps ∈ groups); add safe DB migrations; keep a simple default (single tab) and hide advanced controls behind an expert section.
- Subgrid positioning and free placement
  - Risk: Touch handling, collision resolution, and persistence get complex; widget resize conflicts.
  - Solution: Keep the existing grid model and add fractional cell units; centralize hit-testing; add exhaustive unit tests around placement and bounds.
- Stackable widgets (widget stacks)
  - Risk: Requires custom host container and state persistence; cross-widget sizing and performance.
  - Solution: Implement a paged container with state save/restore; pre-measure and pre-render; provide simple stack management UI and background prefetching.
- App Pairs (split-screen pairs)
  - Risk: True programmatic pairing is limited to privileged/OEM apps on many versions.
  - Solution: Offer best-effort shortcuts (launch two apps sequentially with split-screen intents on supported APIs); clearly label as device/ROM dependent; hide on unsupported versions.
- Background blur/translucency
  - Risk: High GPU cost on low-end devices; API level differences (RenderEffect on Android 12+).
  - Solution: Gate blur by device capability and API level; add intensity presets; provide a static translucent fallback below Android 12.

---

Notes
- Some OEM or Google integrations (Discover, Edge Panels, At a Glance) may require proprietary services or companion modules.
- Feature viability can vary by Android version (e.g., notification badges API behavior, app search APIs, Monet availability).

