## Missing Features — Compared to Popular Android Launchers

This file lists features present in Smart Launcher, Samsung One UI Home, Pixel Launcher, and Nova Launcher that are currently NOT implemented in this app. Use it as a living backlog; update when features are added or deemed out of scope.

### Foundational Restructures (pre‑work)
- Icon rendering pipeline: IconResolver scaffold in place; will add themed icons (A13+), icon packs, adaptive mask, caching and size normalization next.
- Theme system: ThemeManager scaffold with central theme tokens; Monet/palette fallback planned.
- Search and suggestions providers: SearchRegistry scaffold (provider API); apps/contacts/settings providers pending.
- Gesture router: GestureRouter added (central dispatch) to enable future custom mappings and per‑icon/folder swipe actions.
- Drawer model for groups/tabs: Room schema and migration prepared (`drawer_groups` table); repository and UI pending.
- Backup/restore: BackupManager scaffold for versioned JSON export/import with validations and dry‑run (implementation pending).
- Capability gating: DeviceCapabilities added; gating wired via SettingsRepository flags.
- Settings/data layer hardening: SettingsRepository wrapping Config with feature flags baseline.

### Feature Index
- (Partial) [f003] Icon pack support (selector scaffold; parsing TBD)
- (Basic) [f004] Adaptive/custom icon shape controls (basic masks)
- [f006] Normalize icon sizes for consistency
- ✅ (Completed) [f007] Background blur/translucency for folders and App Drawer
- [f010] Drawer tabs/groups (organize apps into tabs)
- [f011] App drawer layout variants (vertical/horizontal/list) and background customization
- [f012] Toggle between App Drawer mode and Home‑only mode
- [f013] Deeper gesture customization (map multiple gestures to actions)
- [f014] Per‑icon and per‑folder swipe actions (secondary actions)
- [f015] Pull‑down gesture for notifications/quick settings
- ✅ (Completed) [f016] Desktop/drawer transition and scroll effects
- [f020] Third‑party deep search integrations (e.g., Sesame)
- [f021] Widget picker search and suggested widgets
- [f022] Popup widgets attached to app icons
- [f023] Stackable widgets (widget stacks)
- [f024] "At a Glance"/Smartspace‑style contextual widget
- [f025] Left feed provider (e.g., Google Discover)
- [f026] Subgrid positioning (place items between cells for fine layout)
- [f027] Home screen landscape rotation support
- [f029] Biometric/PIN protection for hidden/locked apps
- [f030] App Pairs (launch split‑screen pairs)
- [f031] Edge Panels and system‑level one‑handed mode integrations
- [f032] Automatic app categorization in the App Drawer (smart categories)
- [f036] Work profile tab/badging and quick toggle
- [f037] Rename app labels
- ✅ (Completed) [f038] Grid margin/gap customization (grid size already supported)
- ✅ (Completed) [f039] Icon size customization
- ✅ (Completed) [f040] Notification badges/dots with multiple providers/styles

### Smart Launcher
1. [f013] Deeper gesture customization (map multiple gestures to actions)
2. [f003] Icon pack support
3. [f004] Adaptive icon shape controls
4. [f029] Biometric/PIN protection for hidden/locked apps
5. [f032] Automatic app categorization in the App Drawer (smart categories)
6. [f022] Popup widgets attached to app icons
7. [f024] Contextual "Smart Page" with clock/weather and suggestions

### Samsung One UI Home
1. [f027] Home screen landscape rotation support
2. ✅ (Completed) [f007] Background blur/translucency for folders and App Drawer
3. [f012] Toggle between App Drawer mode and Home‑only mode
4. ✅ (Completed) [f040] App icon badges (unread dots/counters)
5. [f023] Stackable "Smart Widgets" (widget stacks)
6. [f030] App Pairs (launch split‑screen pairs)
7. [f031] Edge Panels and system‑level One‑handed mode integrations (OEM‑specific)
8. ✅ (Completed) [f038] Grid size and margin customization
9. ✅ (Completed) [f039] Icon size customization

### Google Pixel Launcher
1. [f024] "At a Glance" widget with smart signals (calendar, weather, commute, timers, etc.)
2. [f025] Google Discover feed on left screen
3. [f036] Work profile tab/badging and quick toggle
4. [f021] Widget picker search and suggested widgets

### Nova Launcher
1. ✅ (Completed) [f016] Desktop/drawer transition/scroll effects
2. [f003] Icon pack support
3. [f004] Adaptive/custom icon shapes
4. ✅ (Completed) [f039] Icon size customization
5. [f013] Extensive gesture mapping (pinch, double‑swipe, etc.) to arbitrary actions
6. [f014] Per‑icon and per‑folder swipe actions (secondary action when swiping on an icon)
7. [f022] Popup widgets attached to app icons
8. ✅ (Completed) [f040] Notification badges/dots with multiple providers/styles
9. [f010] Drawer tabs/groups
10. [f011] Advanced drawer layouts (vertical/horizontal/list) and background customization
11. [f026] Subgrid positioning (place items between cells for fine layout)
12. [f020] Third‑party deep search integration (e.g., Sesame) for shortcuts and in‑app results
13. [f037] Rename app labels
14. [f015] Pull‑down gesture for notifications/quick settings
15. ✅ (Completed) [f038] Grid size and margin customization

### Implementation Risks & Solutions
- Icon pack support [f003]
  - Risk: Parsing diverse third-party icon pack formats; runtime performance and memory overhead for icon bitmaps.
  - Solution: Use a well-maintained icon pack parsing library; cache rasterized icons with versioned keys; provide a "Reset icons" tool; do work off the main thread.
- Notification badges/dots [f040]
  - Risk: Requires a NotificationListenerService, OEM variations, and background restrictions; number vs dot styles.
  - Solution: Make the listener optional with clear opt-in; support dot and numeric styles; use a robust provider abstraction and graceful fallbacks when the listener is disabled.
- Toggle between App Drawer and Home-only modes [f012]
  - Risk: Migrating to Home-only can overflow pages; discoverability of “All apps” changes; user confusion.
  - Solution: Provide a migration wizard (create an “All apps” folder); one-tap revert; safe guardrails and progress UI.
- Drawer tabs/groups [f010]
  - Risk: Significant model and UI complexity; migration of existing hidden apps/sort settings.
  - Solution: Introduce a normalized data model (apps ∈ groups); add safe DB migrations; keep a simple default (single tab) and hide advanced controls behind an expert section.
- Subgrid positioning and free placement [f026]
  - Risk: Touch handling, collision resolution, and persistence get complex; widget resize conflicts.
  - Solution: Keep the existing grid model and add fractional cell units; centralize hit-testing; add exhaustive unit tests around placement and bounds.
- Popup widgets attached to app icons [f022]
  - Risk: Widget host sizing and lifecycle constraints; performance/memory overhead; long-press gesture conflicts.
  - Solution: Provide a lightweight popup host container with size limits; pre-measure and cache remote views; explicit opt-in per shortcut; fall back to app shortcuts if unsupported.
- Contextual “Smart Page” [f024]
  - Risk: Multiple data providers (calendar, weather, timers) increase battery, privacy, and crash surface area.
  - Solution: Modular provider API; WorkManager-based refresh with backoff; per-provider toggles; offline-first providers; robust error isolation.
- Extended folder/icon style customization [f008]
  - Risk: Settings sprawl and migration complexity; visual inconsistency across themes and DPIs.
  - Solution: Ship a small set of presets; unify style model in one schema; version and migrate settings safely; include previews.
- App drawer layout variants and background customization [f011]
  - Risk: Accessibility and performance pitfalls with vertical/list layouts and heavy backgrounds.
  - Solution: Use virtualized lists; fast scrollers; contrast checks; presets with sensible defaults.

### Recommended Development Order
0. ✅ (Done) Run Foundational Restructures (pre‑work) to establish stable architecture
1. ✅ (Done) Desktop/drawer transition effects [f016] (select few performant ones)
2. ✅ (Done) Icon size customization [f039] and Grid margin/gap customization [f038] 
3. ✅ (Done) Background blur/translucency [f007] (gate by capability and API)
4. ✅ (Done) Notification badges/dots [f040] (optional listener; provider abstraction)
5. Icon pack support [f003] and adaptive icon shapes [f004] (foundation for visuals)
6. Normalize icon sizes [f006] 
7. Popup widgets [f022] and per-icon/folder swipe actions [f014] (UI + gesture routing)
8. Drawer tabs/groups [f010] and app drawer layout variants [f011] (DB and UI complexity)
9. Deeper gesture customization [f013] and pull‑down gesture [f015]
10. Toggle between App Drawer and Home‑only modes [f012]
11. "At a Glance"/Smart Page [f024] (modular providers)
12. Widget picker search and suggested widgets [f021]
13. Automatic app categorization [f032] (offline heuristics + overrides)
14. Stackable widgets [f023] (custom host container)
15. Work profile tab/badging [f036]
16. App Pairs [f030] (best‑effort on supported versions)
17. Subgrid positioning and free placement [f026] (advanced layout engine)
18. Home screen landscape rotation support [f027]
19. Third‑party deep search integration [f020]
20. Google Discover feed [f025] (optional companion plugin)
21. Edge Panels and OEM one‑handed integrations [f031] (plugin/out‑of‑scope)
22. Rename app labels [f037]

