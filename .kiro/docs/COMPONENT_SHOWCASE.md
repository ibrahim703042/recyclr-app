# Component Showcase - Visual Reference

## 🎨 Overview

This document provides a visual reference for all integrated UI components in the Recyclr app.

---

## 🏠 HOME SCREEN COMPONENTS

### HomeQuickActionsRow

**Visual Description**:
```
┌─────────────────────────────────────────────────────┐
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │  📷      │  │  🗺️      │  │  📦      │          │
│  │  Scan    │  │  Map     │  │  Pickup  │          │
│  │  Item    │  │  View    │  │  Request │          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
```

**Features**:
- Three equal-width cards
- Icon at top, text below
- Elevated surface with shadow
- Primary green color for Scan
- Smooth press animation

**When to Use**:
- Main navigation shortcuts
- Quick access to core features
- Above-the-fold placement

---

## 📸 SCAN SCREEN COMPONENTS

### ScanningFrame

**Visual Description**:
```
┌─────────────────────────────────────────────────────┐
│                                                     │
│         ┏━━━━━━━━━━━━━━━━━━━━━┓                    │
│         ┃                     ┃                    │
│         ┃                     ┃                    │
│         ┃    ═══════════      ┃  ← Scan line       │
│         ┃                     ┃     (animated)     │
│         ┃                     ┃                    │
│         ┗━━━━━━━━━━━━━━━━━━━━━┛                    │
│          ↑ Corner brackets (animated)              │
└─────────────────────────────────────────────────────┘
```

**Features**:
- Animated corner brackets (pulsing)
- Horizontal scan line (moving up/down)
- White color for visibility
- Responds to `isScanning` state
- 60fps smooth animation

**When to Use**:
- Camera preview overlay
- Barcode/QR code scanning
- Visual feedback during capture

---

## 🎉 RESULTS SCREEN COMPONENTS

### CelebrationConfetti

**Visual Description**:
```
┌─────────────────────────────────────────────────────┐
│  ●                    ▲                             │
│        ■                     ●                      │
│                  ●                  ▲               │
│     ▲                                    ■          │
│              ●         ■                            │
│  ■                           ●                      │
│         ●        ▲                    ■             │
│                        ●                            │
│  ▲         ■                    ●                   │
│                  ●                        ▲         │
└─────────────────────────────────────────────────────┘
```

**Features**:
- 50 animated particles
- Random shapes (circle, square, triangle)
- Colors: Green, Blue, Yellow, Orange
- Falling + rotating animation
- Auto-plays on mount

**When to Use**:
- Success celebrations
- Achievement unlocks
- Milestone completions
- Positive feedback moments

---

## 🛍️ SHOP SCREEN COMPONENTS

### EnhancedProductCard

**Visual Description**:
```
┌──────────────────┐
│  ┌────────────┐  │
│  │            │  │ ← Product image
│  │   IMAGE    │  │
│  │            │  │
│  └────────────┘  │
│                  │
│  Product Name    │ ← Title
│                  │
│  ⭐ 500 pts     │ ← Price
│                  │
│  [Low Stock]  ♥ │ ← Badge + Wishlist
└──────────────────┘
```

**Features**:
- Product image with loading state
- Title (2 lines max)
- Price with points icon
- Stock badges (Low Stock, Out of Stock)
- Wishlist heart icon (animated)
- Smooth press animation

**Stock Badge Colors**:
- **Low Stock**: Orange background
- **Out of Stock**: Red background

**When to Use**:
- Product grids
- Shop marketplace
- Reward catalogs

---

### ShimmerEffect (Grid Type)

**Visual Description**:
```
┌──────────────────────────────────────────────────────┐
│  ┌────────┐  ┌────────┐  ┌────────┐                 │
│  │░░░░░░░░│  │░░░░░░░░│  │░░░░░░░░│  ← Pulsing      │
│  │░░░░░░░░│  │░░░░░░░░│  │░░░░░░░░│     animation   │
│  └────────┘  └────────┘  └────────┘                 │
│                                                      │
│  ┌────────┐  ┌────────┐  ┌────────┐                 │
│  │░░░░░░░░│  │░░░░░░░░│  │░░░░░░░░│                 │
│  │░░░░░░░░│  │░░░░░░░░│  │░░░░░░░░│                 │
│  └────────┘  └────────┘  └────────┘                 │
└──────────────────────────────────────────────────────┘
```

**Features**:
- Multiple layout types (Grid, List, Card)
- Smooth pulsing animation
- Material Design 3 colors
- Customizable item count

**Types Available**:
1. **GRID**: 3-column grid (shop, gallery)
2. **LIST**: Vertical list (feed, history)
3. **CARD**: Single card (profile, details)

**When to Use**:
- Data loading states
- Network requests
- Initial screen load
- Pagination loading

---

### ErrorState

**Visual Description**:
```
┌─────────────────────────────────────────────────────┐
│                                                     │
│                      ⚠️                             │
│                                                     │
│              Failed to load items                   │
│                                                     │
│              ┌──────────────┐                       │
│              │  Try Again   │                       │
│              └──────────────┘                       │
│                                                     │
└─────────────────────────────────────────────────────┘
```

**Features**:
- Error icon (customizable)
- Error message text
- Retry button
- Centered layout
- Material Design 3 styling

**When to Use**:
- Network failures
- API errors
- Empty states
- Permission denials

---

## 👤 PROFILE SCREEN COMPONENTS

### AchievementShowcase

**Visual Description**:
```
┌─────────────────────────────────────────────────────┐
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │   🏆     │  │   🌟     │  │   🔒     │          │
│  │  Earned  │  │  Earned  │  │  Locked  │          │
│  │          │  │          │  │ ▓▓▓░░░░  │ ← Progress│
│  │First Scan│  │Eco Hero  │  │Tree Saver│          │
│  └──────────┘  └──────────┘  └──────────┘          │
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │   🔒     │  │   🔒     │  │   🔒     │          │
│  │  Locked  │  │  Locked  │  │  Locked  │          │
│  │ ▓░░░░░░░ │  │ ░░░░░░░░ │  │ ▓▓▓▓░░░░ │          │
│  │Champion  │  │Legend    │  │Master    │          │
│  └──────────┘  └──────────┘  └──────────┘          │
│                                                     │
│              [ Show More ▼ ]                        │
└─────────────────────────────────────────────────────┘
```

**Features**:
- 3-column grid layout
- Earned badges (colored icon)
- Locked badges (gray icon)
- Progress bars for locked badges
- Expandable "Show More" button
- Smooth expand/collapse animation

**Badge States**:
1. **Earned**: Full color icon, no progress bar
2. **Locked**: Gray icon, progress bar showing completion

**When to Use**:
- Achievement displays
- Gamification features
- User progress tracking
- Milestone showcases

---

## 🎨 COMMON COMPONENTS

### AnimatedStatCard (Available, Not Yet Integrated)

**Visual Description**:
```
┌──────────────────┐
│       🌱         │ ← Icon
│                  │
│      1,234       │ ← Animated counter
│                  │
│   Trees Saved    │ ← Label
└──────────────────┘
```

**Features**:
- Count-up animation
- Icon at top
- Large number display
- Label at bottom
- Customizable colors

**When to Use**:
- Statistics displays
- Impact metrics
- Achievement counters
- Dashboard cards

---

## 📐 Layout Patterns

### Grid Layout (3 Columns)
```
┌────┐ ┌────┐ ┌────┐
│ 1  │ │ 2  │ │ 3  │
└────┘ └────┘ └────┘

┌────┐ ┌────┐ ┌────┐
│ 4  │ │ 5  │ │ 6  │
└────┘ └────┘ └────┘
```
**Used In**: Shop products, achievements

### Horizontal Row (Equal Width)
```
┌──────────┐ ┌──────────┐ ┌──────────┐
│    1     │ │    2     │ │    3     │
└──────────┘ └──────────┘ └──────────┘
```
**Used In**: Quick actions, stat cards

### Vertical List
```
┌─────────────────────────┐
│         Item 1          │
└─────────────────────────┘
┌─────────────────────────┐
│         Item 2          │
└─────────────────────────┘
┌─────────────────────────┐
│         Item 3          │
└─────────────────────────┘
```
**Used In**: Settings, notifications

### Overlay Pattern
```
┌─────────────────────────┐
│                         │
│   Base Content          │
│                         │
│   ┌─────────────────┐   │
│   │  Overlay        │   │
│   │  Component      │   │
│   └─────────────────┘   │
└─────────────────────────┘
```
**Used In**: Scanning frame, confetti

---

## 🎭 Animation Types

### 1. Pulsing Animation
- **Used In**: Scanning frame corners, shimmer effect
- **Duration**: 1000ms
- **Easing**: Linear
- **Effect**: Scale 0.95 → 1.0 → 0.95

### 2. Falling Animation
- **Used In**: Confetti particles
- **Duration**: 3000-5000ms (random)
- **Easing**: Linear
- **Effect**: Top → Bottom with rotation

### 3. Fade In/Out
- **Used In**: Error states, loading states
- **Duration**: 300ms
- **Easing**: FastOutSlowIn
- **Effect**: Alpha 0 → 1 or 1 → 0

### 4. Scale Animation
- **Used In**: Wishlist heart, button presses
- **Duration**: 200ms
- **Easing**: FastOutSlowIn
- **Effect**: Scale 1.0 → 1.2 → 1.0

### 5. Slide Animation
- **Used In**: Scan line, expandable sections
- **Duration**: 2000ms (scan), 300ms (expand)
- **Easing**: Linear (scan), FastOutSlowIn (expand)
- **Effect**: Position change with smooth transition

---

## 🎨 Color Palette

### Primary Colors
```
Primary:           #4CAF50 (Green)
On Primary:        #FFFFFF (White)
Primary Container: #C8E6C9 (Light Green)
```

### Surface Colors
```
Surface:           #FFFFFF (Light) / #1C1B1F (Dark)
Surface Variant:   #F5F5F5 (Light) / #2B2930 (Dark)
On Surface:        #1C1B1F (Light) / #E6E1E5 (Dark)
```

### Semantic Colors
```
Error:             #B3261E (Red)
Warning:           #F57C00 (Orange)
Success:           #4CAF50 (Green)
Info:              #2196F3 (Blue)
```

### Confetti Colors
```
Green:   #4CAF50
Blue:    #2196F3
Yellow:  #FFC107
Orange:  #FF9800
```

---

## 📏 Spacing Scale

```
4dp   ▪        Extra Small
8dp   ▪▪       Small
12dp  ▪▪▪      Medium-Small
16dp  ▪▪▪▪     Medium (Default)
24dp  ▪▪▪▪▪▪   Large
32dp  ▪▪▪▪▪▪▪▪ Extra Large
```

---

## 🔤 Typography Scale

```
Display Large:    57sp / Bold
Headline Medium:  28sp / Bold
Title Large:      22sp / SemiBold
Body Medium:      14sp / Regular
Label Small:      11sp / Medium
```

---

## 📱 Responsive Breakpoints

```
Compact:   < 600dp  (Phones)
Medium:    600-840dp (Tablets)
Expanded:  > 840dp  (Large tablets, foldables)
```

**Component Adaptations**:
- Quick Actions: Stack vertically on compact
- Product Grid: 2 columns on compact, 3 on medium+
- Achievements: 2 columns on compact, 3 on medium+

---

## ✅ Accessibility Features

### Touch Targets
- Minimum size: **48dp × 48dp**
- Buttons: **48dp height minimum**
- Icons: **24dp with 12dp padding**

### Content Descriptions
- All icons have descriptions
- Images have alt text
- Buttons have semantic labels

### Color Contrast
- Text: **4.5:1 minimum** (WCAG AA)
- Large text: **3:1 minimum**
- Icons: **3:1 minimum**

### Screen Reader Support
- Proper heading hierarchy
- Semantic structure
- Announcement for state changes

---

## 🎯 Usage Guidelines

### Do's ✅
- Use components as-is for consistency
- Customize colors via theme
- Add modifiers for layout control
- Handle loading and error states
- Provide content descriptions

### Don'ts ❌
- Don't modify component internals
- Don't hard-code colors
- Don't skip loading states
- Don't ignore accessibility
- Don't block animations

---

## 📚 Related Documentation

- **Implementation Guide**: `INTEGRATION_COMPLETE.md`
- **Quick Start**: `QUICK_START_GUIDE.md`
- **Design Tokens**: `DESIGN_QUICK_REFERENCE.md`
- **Full Specifications**: `UI_UX_ENHANCEMENTS.md`

---

**Last Updated**: May 23, 2026
**Components**: 7 integrated, 1 available
**Status**: Production Ready ✅
