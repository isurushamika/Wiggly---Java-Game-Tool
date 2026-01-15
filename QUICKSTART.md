# Wiggly Launcher - Quick Start Guide

## Installation

1. Make sure Java 17+ is installed on your system
2. Download or build `wiggly-launcher.jar`

## Running

**Method 1 - Command Line:**
```bash
java -jar wiggly-launcher.jar
```

**Method 2 - Double Click:**
- Simply double-click the JAR file in Windows Explorer

## Using the Launcher

### Selecting Orientation
Before or after loading a game, choose your orientation:
- **📱 Portrait**: Vertical mode (240x320) - for traditional mobile games
- **📺 Landscape**: Horizontal mode (320x240) - for racing/action games
- **📱+**: Large portrait (320x480) - for newer games
- **📺+**: Large landscape (480x320) - for widescreen games

### Loading a Game
1. Click "Load JAR Game" button
2. Navigate to your game JAR file
3. Select and open

### Playing
Use these keys to control the game:

```
T9 Keypad Layout on QWERTY:

    Q(1)  W(2)  E(3)
    A(4)  S(5)  D(6)
    Z(7)  X(8)  C(9)
    V(*)  SPC(0)  B(#)

Additional:
- ENTER: OK/Select
- ESC: Back/Exit
- Arrow Keys: Navigate
```

### Customizing Controls

Click **⚙ Settings** to open the visual keyboard configurator:
1. See a virtual T9 phone with all buttons
2. Click any button to change its assignment
3. Press the keyboard key you want to use
4. See changes instantly with orange "→" indicators
5. Save or reset to defaults

Perfect for creating your own comfortable layout!

### Key Reference

| QWERTY Key | T9 Function |
|------------|-------------|
| Q | 1 |
| W | 2 |
| E | 3 |
| A | 4 |
| S | 5 (Select) |
| D | 6 |
| Z | 7 |
| X | 8 |
| C | 9 |
| V | * (Star) |
| SPACE | 0 |
| B | # (Hash) |

## Tips

- **Choose the right orientation**: Portrait for most classic mobile games, landscape for racing/sports
- **Switch anytime**: You can change orientation while the game is running
- Most games use key 5 (S) as the select/action button
- Some games may require specific key combinations
- If a game doesn't load, check its JAR manifest for main class
- Keep the key mapping panel visible for reference

## Common Issues

**Game doesn't load:**
- Verify the JAR is a valid Java game
- Check console for error messages

**Game orientation wrong:**
- Use the orientation buttons to switch between portrait/landscape
- Try the larger size options (📱+ or 📺+)

**Keys don't work:**
- Ensure game window has focus
- Click on the game area before playing

**Want different key mappings?**
- Modify the configuration in the source code
- Rebuild the application

---

Happy Gaming! 🎮
