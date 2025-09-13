## Missing Features — Compared to Popular Android Launchers

This file lists features present in Smart Launcher, Samsung One UI Home, Pixel Launcher, and Nova Launcher that are currently NOT implemented in this app. Use it as a living backlog; update when features are added or deemed out of scope.

### Feature Index
- [f001] Dynamic theming (Monet) and wallpaper‑adaptive colors
- [f002] Themed icons (monochrome/Monet‑tinted for supported apps)
- [f003] Icon pack support
- [f004] Adaptive/custom icon shape controls (mask shapes)
- [f005] Icon label styling/visibility controls
- [f006] Normalize icon sizes for consistency
- [f007] Background blur/translucency for folders and App Drawer
- [f008] Folder/icon style presets (colors/shapes) beyond defaults
- [f009] App drawer sorting modes (A–Z, most used, recently installed)
- [f010] Drawer tabs/groups (organize apps into tabs)
- [f011] App drawer layout variants (vertical/horizontal/list) and background customization
- [f012] Toggle between App Drawer mode and Home‑only mode
- [f013] Deeper gesture customization (map multiple gestures to actions)
- [f014] Per‑icon and per‑folder swipe actions (secondary actions)
- [f015] Pull‑down gesture for notifications/quick settings
- [f016] Desktop/drawer transition and scroll effects
- [f017] Dock pages and deeper dock customizations
- [f018] Unified on‑device/web search with app actions and on‑device results
- [f019] Predictive app suggestions (dock and app drawer top row)
- [f020] Third‑party deep search integrations (e.g., Sesame)
- [f021] Widget picker search and suggested widgets
- [f022] Popup widgets attached to app icons
- [f023] Stackable widgets (widget stacks)
- [f024] "At a Glance"/Smartspace‑style contextual widget
- [f025] Left feed provider (e.g., Google Discover)
- [f026] Subgrid positioning (place items between cells for fine layout)
- [f027] Home screen landscape rotation support
- [f028] Hide apps from drawer and search
- [f029] Biometric/PIN protection for hidden/locked apps
- [f030] App Pairs (launch split‑screen pairs)
- [f031] Edge Panels and system‑level one‑handed mode integrations
- [f032] Automatic app categorization in the App Drawer (smart categories)
- [f033] Auto‑add newly installed apps to Home (optional)
- [f034] Lock Home screen layout (prevent moves/edits)
- [f035] Backup and restore of launcher layout and settings
- [f036] Work profile tab/badging and quick toggle
- [f037] Rename app labels
- [f038] Grid size and margin customization
- [f039] Icon size customization
- [f040] Notification badges/dots with multiple providers/styles

### Smart Launcher
1. [f013] Deeper gesture customization (map multiple gestures to actions)
2. [f003] Icon pack support
3. [f004] Adaptive icon shape controls
4. [f001] Wallpaper‑adaptive dynamic theming (Monet)
5. [f029] Biometric/PIN protection for hidden/locked apps
6. [f018] Unified “smart search” beyond app titles (apps/contacts/web/actions)
7. [f032] Automatic app categorization in the App Drawer (smart categories)
8. [f022] Popup widgets attached to app icons
9. [f024] Contextual "Smart Page" with clock/weather and suggestions

### Samsung One UI Home
1. [f034] Lock Home screen layout (prevent moves/edits)
2. [f033] Auto‑add newly installed apps to Home (optional)
3. [f008] Extended folder/icon style customization options
4. [f027] Home screen landscape rotation support
5. [f009] App drawer sorting modes (A–Z, most used, recently installed)
6. [f007] Background blur/translucency for folders and App Drawer
7. [f012] Toggle between App Drawer mode and Home‑only mode
8. [f040] App icon badges (unread dots/counters)
9. [f023] Stackable "Smart Widgets" (widget stacks)
10. [f030] App Pairs (launch split‑screen pairs)
11. [f031] Edge Panels and system‑level One‑handed mode integrations (OEM‑specific)
12. [f028] Hide apps from drawer and search
13. [f038] Grid size and margin customization
14. [f039] Icon size customization

### Google Pixel Launcher
1. [f002] Themed icons (Monochrome/Monet‑tinted for supported apps)
2. [f019] Predictive app suggestions (dock and app drawer top row)
3. [f001] Full Material You (Monet) dynamic theming across launcher surfaces
4. [f018] Unified device/web search with app actions and on‑device results
5. [f024] "At a Glance" widget with smart signals (calendar, weather, commute, timers, etc.)
6. [f025] Google Discover feed on left screen
7. [f036] Work profile tab/badging and quick toggle
8. [f021] Widget picker search and suggested widgets

### Nova Launcher
1. [f016] Desktop/drawer transition/scroll effects
2. [f003] Icon pack support
3. [f004] Adaptive/custom icon shapes
4. [f039] Icon size customization
5. [f005] Label styling/visibility controls
6. [f013] Extensive gesture mapping (pinch, double‑swipe, etc.) to arbitrary actions
7. [f035] Backup and restore of launcher layout and settings
8. [f014] Per‑icon and per‑folder swipe actions (secondary action when swiping on an icon)
9. [f017] Dock pages and deeper dock customizations
10. [f022] Popup widgets attached to app icons
11. [f040] Notification badges/dots with multiple providers/styles
12. [f010] Drawer tabs/groups
13. [f011] Advanced drawer layouts (vertical/horizontal/list) and background customization
14. [f026] Subgrid positioning (place items between cells for fine layout)
15. [f020] Third‑party deep search integration (e.g., Sesame) for shortcuts and in‑app results
16. [f028] Hide apps from drawer and search
17. [f037] Rename app labels
18. [f015] Pull‑down gesture for notifications/quick settings
19. [f038] Grid size and margin customization

### Implementation Risks & Solutions
- Icon pack support [f003]
  - Risk: Parsing diverse third-party icon pack formats; runtime performance and memory overhead for icon bitmaps.
  - Solution: Use a well-maintained icon pack parsing library; cache rasterized icons with versioned keys; provide a "Reset icons" tool; do work off the main thread.
- Notification badges/dots [f040]
  - Risk: Requires a NotificationListenerService, OEM variations, and background restrictions; number vs dot styles.
  - Solution: Make the listener optional with clear opt-in; support dot and numeric styles; use a robust provider abstraction and graceful fallbacks when the listener is disabled.
- Unified on-device/web search [f018]
  - Risk: Large indexes (apps, contacts, settings) can impact startup and privacy expectations.
  - Solution: Index incrementally; gate providers behind toggles; respect runtime permissions (Contacts); keep everything offline; provide per-source enable/disable and clear-index actions.
- Predictive app suggestions [f019]
  - Risk: Requires PACKAGE_USAGE_STATS; users may decline; can feel intrusive.
  - Solution: Make suggestions opt-in; use simple recency/frequency heuristics; degrade to static suggestions when permission is absent.
- Automatic app categorization [f032]
  - Risk: Accuracy varies; many apps lack reliable category metadata; no network access policy limits remote classification.
  - Solution: Prefer ApplicationInfo.category when available; supplement with local heuristics and on-device ML only if kept fully offline; always allow manual overrides.
- "At a Glance" and smart signals [f024]
  - Risk: Pixel’s Smartspace uses proprietary providers (weather, commute); replicating parity is difficult.
  - Solution: Build an open equivalent that surfaces calendar events, alarms, timers, and local weather via user-selected providers; make integrations modular and optional.
- Google Discover feed [f025]
  - Risk: Requires Google app and private APIs; may violate distribution policies if reverse-engineered.
  - Solution: Ship as an optional companion plugin that the user installs separately; expose a left-page provider API so alternative feeds (e.g., RSS) can be used.
- Home screen landscape rotation [f027]
  - Risk: Widget and layout constraints differ across orientations; clipping/overlap risk.
  - Solution: Maintain per-orientation grid metrics and positions; remeasure widgets on rotation; test across API levels and aspect ratios.
- Lock Home screen layout [f034]
  - Risk: Users may be confused why icons cannot be moved; accidental lock-in.
  - Solution: Add a quick toggle with snackbar hint and a temporary unlock gesture; block drags at the controller level.
- Drawer tabs/groups [f010]
  - Risk: Significant model and UI complexity; migration of existing hidden apps/sort settings.
  - Solution: Introduce a normalized data model (apps ∈ groups); add safe DB migrations; keep a simple default (single tab) and hide advanced controls behind an expert section.
- Subgrid positioning and free placement [f026]
  - Risk: Touch handling, collision resolution, and persistence get complex; widget resize conflicts.
  - Solution: Keep the existing grid model and add fractional cell units; centralize hit-testing; add exhaustive unit tests around placement and bounds.
- Stackable widgets (widget stacks) [f023]
  - Risk: Requires custom host container and state persistence; cross-widget sizing and performance.
  - Solution: Implement a paged container with state save/restore; pre-measure and pre-render; provide simple stack management UI and background prefetching.
- App Pairs (split-screen pairs) [f030]
  - Risk: True programmatic pairing is limited to privileged/OEM apps on many versions.
  - Solution: Offer best-effort shortcuts (launch two apps sequentially with split-screen intents on supported APIs); clearly label as device/ROM dependent; hide on unsupported versions.
- Background blur/translucency [f007]
  - Risk: High GPU cost on low-end devices; API level differences (RenderEffect on Android 12+).
  - Solution: Gate blur by device capability and API level; add intensity presets; provide a static translucent fallback below Android 12.
- Gesture customization [f013] and per-icon/folder swipe actions [f014]
  - Risk: Gesture conflicts with system navigation and accessibility; accidental triggers; complex gesture routing; long-press conflicts; performance overhead.
  - Solution: Centralize gesture dispatch; respect system gesture exclusion regions; expose per-gesture sensitivity; provide safe defaults; allow quick disable; add accessibility review and haptics.
- Adaptive icon shape controls [f004] and label style customization [f005]
  - Risk: Fragmented mask support; third‑party icon inconsistencies; label legibility and truncation across DPIs and languages.
  - Solution: Use `AdaptiveIconDrawable` masks with a curated set of shapes; validate at multiple DPIs; provide contrast-aware label colors, max lines and ellipsize options; preview before apply.
- Dynamic theming (Monet) and wallpaper‑adaptive colors [f001]
  - Risk: Monet is Android 12+ only; visual inconsistency on older versions; jank on live theme recomposition.
  - Solution: Use Material 3 dynamic color APIs when available; fallback to palette extraction (e.g., `Palette`) and manual theming on older Android; debounce theme updates; centralize theme tokens.
- Themed icons (monochrome) [f002]
  - Risk: Only available on Android 13+ and for apps that ship a `monochrome` layer; inconsistent coverage; brand identity concerns.
  - Solution: Make it optional and per-app overridable; fall back gracefully; allow user-supplied overrides; keep a compatibility map.
- Biometric/PIN protection for hidden/locked apps [f029]
  - Risk: Launcher cannot enforce system-wide; apps can be launched from elsewhere; privacy expectations mismatch.
  - Solution: Gate only launcher-initiated launches using `BiometricPrompt`; hide locked apps from launcher search/suggestions; document limitations; offer quick-unlock timeout.
- Popup widgets attached to app icons [f022]
  - Risk: Widget host sizing and lifecycle constraints; performance/memory overhead; long-press gesture conflicts.
  - Solution: Provide a lightweight popup host container with size limits; pre-measure and cache remote views; explicit opt-in per shortcut; fall back to app shortcuts if unsupported.
- Contextual “Smart Page” [f024]
  - Risk: Multiple data providers (calendar, weather, timers) increase battery, privacy, and crash surface area.
  - Solution: Modular provider API; WorkManager-based refresh with backoff; per-provider toggles; offline-first providers; robust error isolation.
- Auto-add newly installed apps to Home [f033]
  - Risk: User annoyance and clutter; broadcast restrictions/races on recent Android versions.
  - Solution: Make it opt-in; process package add events off the main thread; queue additions with dedupe; add to a designated page/folder; provide undo snackbar.
- Extended folder/icon style customization [f008]
  - Risk: Settings sprawl and migration complexity; visual inconsistency across themes and DPIs.
  - Solution: Ship a small set of presets; unify style model in one schema; version and migrate settings safely; include previews.
- App drawer sorting modes [f009]
  - Risk: “Most used” requires usage stats and can be noisy; frequent resorting causes jank; conflicts with groups/tabs.
  - Solution: Use stable tie-breakers; compute asynchronously; allow mode-specific filters; degrade when permission is absent.
- Toggle between App Drawer and Home-only modes [f012]
  - Risk: Migrating to Home-only can overflow pages; discoverability of “All apps” changes; user confusion.
  - Solution: Provide a migration wizard (create an “All apps” folder); one-tap revert; safe guardrails and progress UI.
- Edge Panels and OEM one-handed integrations [f031]
  - Risk: OEM/private APIs; compatibility and policy risks.
  - Solution: Treat as out-of-scope for core app; expose optional plugin points; hide features on unsupported devices.
- Desktop/drawer transition and scroll effects [f016]
  - Risk: GPU cost and jank on low-end devices; inconsistent feel across refresh rates.
  - Solution: Offer a small set of performant effects with a preview; prefer hardware-accelerated transforms; auto-fallback on weak devices.
- Backup and restore (layout and settings) [f035]
  - Risk: Data corruption and privacy; cross-version schema changes; storage access UX.
  - Solution: Export/import via SAF using versioned JSON; run validations and checksums; optional encryption; dry-run before applying.
- Dock pages and deeper dock customizations [f017]
  - Risk: More complex layout, with gesture-nav interference near the bottom edge.
  - Solution: Model dock as a paged container; add migration routines; avoid gesture exclusion regions; exhaustive hit-testing.
- Third-party deep search integrations [f020]
  - Risk: Dependency on third-party apps/services; permission scopes; stability and security concerns.
  - Solution: Define a provider ABI; sandbox integrations; explicit user opt-in per provider; graceful degradation when unavailable.
- App drawer layout variants and background customization [f011]
  - Risk: Accessibility and performance pitfalls with vertical/list layouts and heavy backgrounds.
  - Solution: Use virtualized lists; fast scrollers; contrast checks; presets with sensible defaults.

### Recommended Development Order
1. Lock Home screen layout [f034] (quick win, low risk)
2. App drawer sorting modes [f009] (no special permissions; improves discoverability)
3. Auto-add newly installed apps to Home [f033] (opt-in; simple)
4. Extended icon/label/folder style customization [f005, f008] (presets; minimal migrations)
5. Desktop/drawer transition effects [f016] (select few performant ones)
6. Icon pack support [f003] and adaptive icon shapes [f004] (foundation for visuals)
7. Themed icons [f002] and dynamic theming (Monet) + wallpaper-adaptive colors [f001]
8. Notification badges/dots [f040] (optional listener; provider abstraction)
9. Predictive app suggestions [f019] (opt-in; simple heuristics)
10. Backup and restore [f035] (versioned schema to support future migrations)
11. Background blur/translucency [f007] (gate by capability and API)
12. Popup widgets [f022] and per-icon/folder swipe actions [f014] (UI + gesture routing)
13. Dock pages and deeper dock customizations [f017] (layout migration)
14. Drawer tabs/groups [f010] and app drawer layout variants [f011] (DB and UI complexity)
15. Unified on-device/web search [f018] (modular providers and indexing)
16. “At a Glance”/Smart Page [f024] (modular providers)
17. Automatic app categorization [f032] (offline heuristics + overrides)
18. Stackable widgets [f023] (custom host container)
19. App Pairs [f030] (best-effort on supported versions)
20. Subgrid positioning and free placement [f026] (advanced layout engine)
21. Google Discover feed [f025] (optional companion plugin)
22. Edge Panels and OEM one-handed integrations [f031] (plugin/out-of-scope)

---

Notes
- Some OEM or Google integrations (Discover, Edge Panels, At a Glance) may require proprietary services or companion modules.
- Feature viability can vary by Android version (e.g., notification badges API behavior, app search APIs, Monet availability).

