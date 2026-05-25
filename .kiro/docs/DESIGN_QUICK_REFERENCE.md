# Recyclr Design Quick Reference

## 🎨 Color Palette

### Primary Colors
```
Primary Green:     #2E7D32  ████████
Accent Teal:       #00897B  ████████
Warm Gold:         #FFA726  ████████
Soft Mint:         #E8F5E9  ████████
Deep Forest:       #1B5E20  ████████
Cloud White:       #FAFAFA  ████████
```

### Semantic Colors
```
Success:           #4CAF50  ████████
Warning:           #FF9800  ████████
Error:             #EF5350  ████████
Info:              #42A5F5  ████████
```

### Category Colors
```
Plastic:           #E8F5E9  ████████  (Light Green)
Paper:             #E3F2FD  ████████  (Light Blue)
Glass:             #FFF3E0  ████████  (Light Orange)
Metal:             #FCE4EC  ████████  (Light Pink)
Textile:           #FFFDE7  ████████  (Light Yellow)
```

---

## 📏 Spacing System

```
XS:    4dp   ▪
SM:    8dp   ▪▪
MD:   16dp   ▪▪▪▪
LG:   24dp   ▪▪▪▪▪▪
XL:   32dp   ▪▪▪▪▪▪▪▪
XXL:  48dp   ▪▪▪▪▪▪▪▪▪▪▪▪
```

---

## 🔤 Typography Scale

```
Display Large:    57sp / Bold      "Welcome"
Headline Large:   32sp / Bold      "Section Title"
Title Large:      22sp / SemiBold  "Card Title"
Body Large:       16sp / Regular   "Body text"
Label Medium:     12sp / Medium    "Button text"
Label Small:      11sp / Regular   "Caption"
```

---

## 📐 Corner Radius

```
Small:        8dp   ╭─╮
Medium:      12dp   ╭──╮
Large:       16dp   ╭───╮
Extra Large: 24dp   ╭────╮
Full:      9999dp   ●
```

---

## 🎭 Elevation Levels

```
None:         0dp   (Flat)
Low:          2dp   (Subtle)
Medium:       4dp   (Standard)
High:         8dp   (Prominent)
Very High:   16dp   (Modal)
```

---

## 🎬 Animation Durations

```
Fast:       150ms   (Micro-interactions)
Standard:   300ms   (Most transitions)
Slow:       500ms   (Complex animations)
Very Slow: 1000ms   (Celebration effects)
```

---

## 📱 Screen Breakpoints

```
Compact:     < 600dp   (Phones)
Medium:   600-840dp   (Tablets portrait)
Expanded:  > 840dp    (Tablets landscape)
```

---

## 🎯 Touch Targets

```
Minimum:     48dp × 48dp
Recommended: 56dp × 56dp
Large:       72dp × 72dp
```

---

## 🖼️ Common Component Sizes

### Buttons
```
Small:       Height 40dp
Medium:      Height 48dp
Large:       Height 56dp
Extra Large: Height 72dp
```

### Icons
```
Small:       16dp × 16dp
Medium:      24dp × 24dp
Large:       32dp × 32dp
Extra Large: 48dp × 48dp
```

### Avatar/Profile Pictures
```
Small:       32dp × 32dp
Medium:      48dp × 48dp
Large:       80dp × 80dp
Extra Large: 120dp × 120dp
```

### Cards
```
Padding:     16dp
Corner:      16dp
Elevation:   2-4dp
```

---

## 🎨 Component Patterns

### Primary Button
```kotlin
Button(
    modifier = Modifier.height(56.dp),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF2E7D32)
    )
) {
    Text("Action", fontWeight = FontWeight.Bold)
}
```

### Card
```kotlin
ElevatedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.elevatedCardElevation(4.dp)
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Content
    }
}
```

### Input Field
```kotlin
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Label") },
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth()
)
```

---

## 🎭 State Variations

### Button States
```
Default:    Primary color, no elevation
Pressed:    Darker shade, scale 0.95
Disabled:   Gray, 38% opacity
Loading:    Show progress indicator
```

### Card States
```
Default:    2dp elevation
Hover:      4dp elevation
Pressed:    1dp elevation
Selected:   Primary container color
```

---

## 📊 Data Visualization

### Progress Indicators
```
Linear:      Height 4-8dp, rounded corners
Circular:    Stroke width 4dp
Determinate: Show percentage
```

### Charts
```
Colors:      Use category colors
Grid:        Light gray, 1dp
Labels:      12sp, gray
```

---

## 🌙 Dark Mode Adjustments

```
Surface:     Lighter than background
Elevation:   Use tonal elevation
Text:        Reduce contrast slightly
Images:      Reduce brightness 10-20%
```

---

## ♿ Accessibility Guidelines

### Color Contrast Ratios (WCAG AA)
```
Normal Text:     4.5:1 minimum
Large Text:      3:1 minimum
UI Components:   3:1 minimum
```

### Touch Targets
```
Minimum:     48dp × 48dp
Spacing:     8dp between targets
```

### Text Scaling
```
Support:     Up to 200%
Test:        At 150% and 200%
```

---

## 🎯 Icon Usage

### System Icons (Material Icons)
```
Navigation:  Home, Back, Menu
Actions:     Add, Edit, Delete, Share
Status:      Check, Error, Warning, Info
```

### Custom Icons
```
Recycling:   Custom category icons
Rewards:     Coins, badges, trophies
Impact:      Trees, CO2, water drops
```

---

## 📸 Image Guidelines

### Aspect Ratios
```
Square:      1:1   (Profile pictures)
Landscape:   16:9  (Banners)
Portrait:    3:4   (Product images)
```

### Sizes
```
Thumbnail:   80dp × 80dp
Small:       120dp × 120dp
Medium:      240dp × 240dp
Large:       480dp × 480dp
```

### Optimization
```
Format:      WebP preferred
Quality:     80-90%
Lazy Load:   Yes
Caching:     Memory + Disk
```

---

## 🎬 Animation Easing

```
Linear:           No easing
EaseIn:           Slow start
EaseOut:          Slow end
EaseInOut:        Slow start and end
FastOutSlowIn:    Material standard
Spring:           Bouncy effect
```

---

## 📱 Bottom Navigation

```
Height:      80dp
Items:       3-5 items
Icons:       24dp
Labels:      12sp
Selected:    Primary color + bold
```

---

## 🎨 Gradient Usage

### Header Gradients
```kotlin
Brush.verticalGradient(
    listOf(
        Color(0xFF2E7D32),  // Primary Green
        Color(0xFF43A047)   // Lighter Green
    )
)
```

### Card Accents
```kotlin
Brush.horizontalGradient(
    listOf(
        Color(0xFF4CAF50),
        Color(0xFF66BB6A)
    )
)
```

---

## 🔔 Notification Badges

```
Size:        16dp × 16dp minimum
Position:    Top-right corner
Offset:      -4dp from edge
Max Number:  99+ for large counts
```

---

## 💡 Best Practices

### DO ✅
- Use consistent spacing
- Follow Material Design 3
- Test on multiple devices
- Support dark mode
- Ensure accessibility
- Optimize performance
- Use semantic colors
- Provide feedback

### DON'T ❌
- Mix different design systems
- Use too many colors
- Ignore accessibility
- Skip loading states
- Forget error states
- Use tiny touch targets
- Overcomplicate UI
- Ignore performance

---

## 🚀 Quick Implementation Tips

1. **Start with Design Tokens**: Define colors, spacing, typography first
2. **Build Component Library**: Create reusable components
3. **Test Early**: Test on real devices frequently
4. **Iterate**: Get feedback and improve
5. **Document**: Keep design decisions documented
6. **Collaborate**: Work closely with designers
7. **Optimize**: Profile and optimize performance
8. **Accessibility**: Test with screen readers

---

*Quick Reference Version: 1.0*
*Last Updated: 2026-05-23*
