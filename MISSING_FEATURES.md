## Missing Features — Compared to Popular Android Launchers

This file lists features present in Smart Launcher, Samsung One UI Home, Pixel Launcher, and Nova Launcher that are currently NOT implemented in this app. Use it as a living backlog; update when features are added or deemed out of scope.

### Smart Launcher
1. Deeper gesture customization (map multiple gestures to actions)
2. Icon pack support and adaptive icon shape controls
3. Wallpaper-adaptive theming (ambient colors beyond manual color customization)
4. Biometric/PIN protection for hidden/locked apps
5. Unified “smart search” beyond app titles (e.g., contacts/web/actions)
6. Automatic app categorization in the App Drawer (smart categories)
7. Popup widgets attached to app icons
8. Contextual "Smart Page" with clock/weather and suggestions

### Samsung One UI Home
1. Lock Home screen layout (prevent moves/edits)
2. Auto-add newly installed apps to Home (optional)
3. Extended folder/icon style customization options
4. Home screen landscape rotation support
5. App drawer sorting modes (A–Z, most used, recently installed)
6. Background blur/translucency for folders and App Drawer
7. Toggle between App Drawer mode and Home-only mode
8. App icon badges (unread dots/counters)
9. Stackable "Smart Widgets" (widget stacks)
10. App Pairs (launch split-screen pairs)
11. Edge Panels and system-level One-handed mode integrations (OEM-specific)

### Google Pixel Launcher
1. Themed icons (Monochrome/Monet-tinted for supported apps)
2. Predictive app suggestions (dock and app drawer top row)
3. Full Material You (Monet) dynamic theming across launcher surfaces
4. Unified device/web search with app actions and on-device results
5. "At a Glance" widget with smart signals (calendar, weather, commute, timers, etc.)
6. Google Discover feed on left screen

### Nova Launcher
1. Desktop/drawer transition/scroll effects
2. Icon pack support; custom icon shapes/sizes; label styling/visibility controls
3. Extensive gesture mapping (pinch, double-swipe, etc.) to arbitrary actions
4. Backup and restore of launcher layout and settings
5. Per-icon and per-folder swipe actions (secondary action when swiping on an icon)
6. Dock pages and deeper dock customizations
7. Popup widgets attached to app icons
8. Notification badges/dots with multiple providers/styles
9. Drawer tabs/groups; advanced drawer layouts (vertical/horizontal/list) and background customization
10. Subgrid positioning (place items between cells for fine layout)
11. Third-party deep search integration (e.g., Sesame) for shortcuts and in-app results

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

- Gesture customization and per-icon/folder swipe actions
  - Risk: Gesture conflicts with system navigation and accessibility; accidental triggers; complex gesture routing; long-press conflicts; performance overhead.
  - Solution: Centralize gesture dispatch; respect system gesture exclusion regions; expose per-gesture sensitivity; provide safe defaults; allow quick disable; add accessibility review and haptics.
- Adaptive icon shape controls and label style customization
  - Risk: Fragmented mask support; third‑party icon inconsistencies; label legibility and truncation across DPIs and languages.
  - Solution: Use `AdaptiveIconDrawable` masks with a curated set of shapes; validate at multiple DPIs; provide contrast-aware label colors, max lines and ellipsize options; preview before apply.
- Dynamic theming (Monet) and wallpaper‑adaptive colors
  - Risk: Monet is Android 12+ only; visual inconsistency on older versions; jank on live theme recomposition.
  - Solution: Use Material 3 dynamic color APIs when available; fallback to palette extraction (e.g., `Palette`) and manual theming on older Android; debounce theme updates; centralize theme tokens.
- Themed icons (monochrome)
  - Risk: Only available on Android 13+ and for apps that ship a `monochrome` layer; inconsistent coverage; brand identity concerns.
  - Solution: Make it optional and per-app overridable; fall back gracefully; allow user-supplied overrides; keep a compatibility map.
- Biometric/PIN protection for hidden/locked apps
  - Risk: Launcher cannot enforce system-wide; apps can be launched from elsewhere; privacy expectations mismatch.
  - Solution: Gate only launcher-initiated launches using `BiometricPrompt`; hide locked apps from launcher search/suggestions; document limitations; offer quick-unlock timeout.
- Popup widgets attached to app icons
  - Risk: Widget host sizing and lifecycle constraints; performance/memory overhead; long-press gesture conflicts.
  - Solution: Provide a lightweight popup host container with size limits; pre-measure and cache remote views; explicit opt-in per shortcut; fall back to app shortcuts if unsupported.
- Contextual “Smart Page”
  - Risk: Multiple data providers (calendar, weather, timers) increase battery, privacy, and crash surface area.
  - Solution: Modular provider API; WorkManager-based refresh with backoff; per-provider toggles; offline-first providers; robust error isolation.
- Auto-add newly installed apps to Home
  - Risk: User annoyance and clutter; broadcast restrictions/races on recent Android versions.
  - Solution: Make it opt-in; process package add events off the main thread; queue additions with dedupe; add to a designated page/folder; provide undo snackbar.
- Extended folder/icon style customization
  - Risk: Settings sprawl and migration complexity; visual inconsistency across themes and DPIs.
  - Solution: Ship a small set of presets; unify style model in one schema; version and migrate settings safely; include previews.
- App drawer sorting modes
  - Risk: “Most used” requires usage stats and can be noisy; frequent resorting causes jank; conflicts with groups/tabs.
  - Solution: Use stable tie-breakers; compute asynchronously; allow mode-specific filters; degrade when permission is absent.
- Toggle between App Drawer and Home-only modes
  - Risk: Migrating to Home-only can overflow pages; discoverability of “All apps” changes; user confusion.
  - Solution: Provide a migration wizard (create an “All apps” folder); one-tap revert; safe guardrails and progress UI.
- Edge Panels and OEM one-handed integrations
  - Risk: OEM/private APIs; compatibility and policy risks.
  - Solution: Treat as out-of-scope for core app; expose optional plugin points; hide features on unsupported devices.
- Desktop/drawer transition and scroll effects
  - Risk: GPU cost and jank on low-end devices; inconsistent feel across refresh rates.
  - Solution: Offer a small set of performant effects with a preview; prefer hardware-accelerated transforms; auto-fallback on weak devices.
- Backup and restore (layout and settings)
  - Risk: Data corruption and privacy; cross-version schema changes; storage access UX.
  - Solution: Export/import via SAF using versioned JSON; run validations and checksums; optional encryption; dry-run before applying.
- Dock pages and deeper dock customizations
  - Risk: More complex layout, with gesture-nav interference near the bottom edge.
  - Solution: Model dock as a paged container; add migration routines; avoid gesture exclusion regions; exhaustive hit-testing.
- Third-party deep search integrations
  - Risk: Dependency on third-party apps/services; permission scopes; stability and security concerns.
  - Solution: Define a provider ABI; sandbox integrations; explicit user opt-in per provider; graceful degradation when unavailable.
- App drawer layout variants and background customization
  - Risk: Accessibility and performance pitfalls with vertical/list layouts and heavy backgrounds.
  - Solution: Use virtualized lists; fast scrollers; contrast checks; presets with sensible defaults.

### Recommended Development Order
1. Lock Home screen layout (quick win, low risk)
2. App drawer sorting modes (no special permissions; improves discoverability)
3. Auto-add newly installed apps to Home (opt-in; simple)
4. Extended icon/label/folder style customization (presets; minimal migrations)
5. Desktop/drawer transition effects (select few performant ones)
6. Icon pack support and adaptive icon shapes (foundation for visuals)
7. Themed icons and dynamic theming (Monet) + wallpaper-adaptive colors
8. Notification badges/dots (optional listener; provider abstraction)
9. Predictive app suggestions (opt-in; simple heuristics)
10. Backup and restore (versioned schema to support future migrations)
11. Background blur/translucency (gate by capability and API)
12. Popup widgets and per-icon/folder swipe actions (UI + gesture routing)
13. Dock pages and deeper dock customizations (layout migration)
14. Drawer tabs/groups and app drawer layout variants (DB and UI complexity)
15. Unified on-device/web search (modular providers and indexing)
16. “At a Glance” and contextual Smart Page (modular providers)
17. Automatic app categorization (offline heuristics + overrides)
18. Stackable widgets (custom host container)
19. App Pairs (best-effort on supported versions)
20. Subgrid positioning and free placement (advanced layout engine)
21. Google Discover feed (optional companion plugin)
22. Edge Panels and OEM one-handed integrations (plugin/out-of-scope)

---

Notes
- Some OEM or Google integrations (Discover, Edge Panels, At a Glance) may require proprietary services or companion modules.
- Feature viability can vary by Android version (e.g., notification badges API behavior, app search APIs, Monet availability).

