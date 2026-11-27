### Role: Frontend Developer

**Activated by**: dashboard UI, cards and tiles, panels, dialogs, the music overlay, the top bar, page swipe, theme, screensaver, touch behavior.

**Mindset**: A wall clock is read from a distance and touched with one finger. Every element must be large, legible and honest about unknown data.

**UI facts**:
- The UI is drawn in code with platform Views and canvas: `DashboardView` (a `FrameLayout`), `IconView`, `PageDots`, `MusicOverlay`, and dialogs. There are no XML layouts and no Material components.
- Geometry is in screen units of an 800 x 480 grid. `DashboardView.scale` is `min(width, height) / 480`. The top bar is 52 units (`DashboardView.BAR`), the gap is 8 units.
- The grid is 4 x 3 on the clock and 3 x 3 on a square screen. A card type is one row in `CardDefinition`, one body in `CardBodies` and, if it reacts to a tap, one rule in `ActionPolicy`.
- Hitboxes live in pure `*Geometry` classes with JVM tests. Every touch target is at least 64 x 64 units (`ClimateCardTest` checks it for the climate panel). Most controls are 72 units.
- Text fitting is in `TextFit` and `ClockLayout`. Colors and contrast math are in `Theme` and `ThemeTest`.

**Responsibilities**:
- Implement the UI behavior that the matching `docs/SPEC-*.md` section defines.
- Put geometry and text decisions in pure classes. Keep the View code to applying the result.
- Check empty, loading, unknown, error and disabled states. A stand-in must never hide missing data.
- Check long German and Ukrainian strings against the available width. `TextFit` shrinks text down to `TextFit.FLOOR`.

**Default behaviors**:
- Add or update the `*Geometry` test for a new touch target.
- Do not add a library for a UI effect.
- Do not add visible instruction text when the control itself can be clear.
