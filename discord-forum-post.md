# 🍅 Pomodoro Timer Glyph Toy for Nothing Phone

A custom Glyph Toy that transforms your Nothing Phone's back display into a productivity timer using the Pomodoro Technique!

## 📱 What it does
- **25-minute work sessions** followed by **5-minute breaks**
- Displays countdown timer directly on the **25x25 circular Glyph Matrix**
- Visual indicators: **●** for work sessions, **○** for break sessions
- Automatically cycles between work and break periods
- Skip functionality to jump between phases

## 🎮 How to use
1. **Switch to toy**: Use Glyph Button to cycle to "Pomodoro Timer"
2. **Start timer**: Long press to begin your first 25-minute work session
3. **Skip phases**: Long press anytime to skip to next timer (work→break or break→work)
4. **Auto-loop**: Timer automatically alternates until you switch to another toy

## 🖥️ Display Layout (optimized for circular display)
```
    WORK     ← Phase indicator
   25:00     ← Countdown timer
     ●       ← Work/break indicator
```

## 🛠️ Technical Details
- **Language**: Java (Android Service)
- **SDK**: Nothing GlyphMatrix Developer Kit
- **Display**: 25x25 pixel circular matrix
- **Font**: Monospace for consistent number alignment
- **Brightness**: Full white (255) on black background for maximum contrast

## 📁 Project Structure
- `PomodoroTimerService.java` - Main timer logic and Glyph Matrix display
- `AndroidManifest.xml` - Service registration with long press support
- `pomodoro-timer-simulator.js` - JavaScript version for [glyph.andreibanu.com](https://glyph.andreibanu.com) simulator
- Resources: strings, preview drawable for settings page

## 🧪 Testing
Includes a JavaScript version compatible with the community Glyph simulator for development and testing without a physical device.

## 🚀 Installation
1. Copy `GlyphMatrixSDK.aar` to `app/libs/` directory
2. Build APK with `./gradlew build`
3. Install on Nothing Phone
4. Enable in Settings → Glyph → Glyph Toys

## 💡 Features
- ✅ Clean numerical countdown display
- ✅ Phase indicators (WORK/BREAK)
- ✅ Skip functionality with long press
- ✅ Automatic phase transitions
- ✅ Circular display optimization
- ✅ Simulator compatibility for testing

Perfect for productivity enthusiasts who want to use their Nothing Phone's unique Glyph interface for time management! 

Built with the Nothing GlyphMatrix Developer Kit following the official documentation and best practices.

---
*Feel free to ask questions or suggest improvements! This is my first Glyph Toy project.* 🎯
