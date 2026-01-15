# Wiggly JAR Game Launcher - Configuration Guide

## Default Key Mappings

### T9 Keypad Layout
```
Physical Phone:         QWERTY Keyboard:
+-------+-------+-------+
|   1   |   2   |   3   |      Q  W  E
+-------+-------+-------+
|   4   |   5   |   6   |      A  S  D
+-------+-------+-------+
|   7   |   8   |   9   |      Z  X  C
+-------+-------+-------+
|   *   |   0   |   #   |      V  SP B
+-------+-------+-------+
```

### Mapping Details

| QWERTY | T9 Key | Common Use in Games |
|--------|--------|---------------------|
| Q | 1 | Menu navigation, secondary action |
| W | 2 | Up/Navigate up |
| E | 3 | Menu option |
| A | 4 | Left/Navigate left |
| S | 5 | **Select/OK/Fire** (most common action) |
| D | 6 | Right/Navigate right |
| Z | 7 | Secondary action |
| X | 8 | Down/Navigate down |
| C | 9 | Secondary action |
| V | * | Options/Special |
| SPACE | 0 | Alternative action |
| B | # | Menu/Pause |

### Additional Controls

| Key | Function |
|-----|----------|
| ENTER | OK/Confirm/Select |
| ESC | Back/Exit/Pause |
| ↑ | Up |
| ↓ | Down |
| ← | Left |
| → | Right |

## Customizing Key Mappings

### Method 1: Visual Settings Dialog (Recommended)

1. Launch the application
2. Click the **⚙ Settings** button in the toolbar
3. You'll see a visual T9 phone keyboard with:
   - D-Pad (Up/Down/Left/Right arrows with OK in center)
   - Call (green) and Disconnect (red) buttons
   - Complete T9 number pad (1-9, *, 0, #)
4. Each button shows its current assignment in orange (e.g., "→ Q")
5. To reassign a button:
   - Click the button you want to change
   - It will highlight in yellow
   - Press any keyboard key you want to assign
   - Press ESC to cancel if you change your mind
6. Click **Save** to apply your changes
7. Click **Reset to Default** to restore original mappings

The visual dialog makes it easy to:
- See all your mappings at once
- Understand the phone layout
- Make changes without editing code
- Avoid key conflicts

### Method 2: Edit Source Code

To permanently change default mappings:

1. Open [GameLauncher.java](src/main/java/com/wiggly/GameLauncher.java#L21-L34)

2. Modify the `DEFAULT_KEY_MAPPING` HashMap:

```java
private static final Map<Integer, Integer> DEFAULT_KEY_MAPPING = new HashMap<Integer, Integer>() {{
    put(KeyEvent.VK_Q, KeyEvent.VK_NUMPAD1);  // Change VK_Q to your preferred key
    put(KeyEvent.VK_W, KeyEvent.VK_NUMPAD2);
    // ... etc
}};
```

3. Rebuild the application using `build.bat`

### Visual Settings Features

The settings dialog displays a realistic T9 phone layout:

```
        ┌─────────────┐
        │   ▲ D-Pad   │
        │  ◄ OK ►     │
        │     ▼       │
        ├─────────────┤
        │  📞    📵   │  (Call/Disconnect)
        ├─────────────┤
        │ 1   2   3   │
        │ 4   5   6   │  T9 Keypad
        │ 7   8   9   │
        │ *   0   #   │
        └─────────────┘
```

Each button shows:
- **Main label**: The T9 key function (e.g., "5 JKL")
- **Assignment**: Current keyboard key (e.g., "→ S")
- **Tooltip**: Full mapping on hover

### Method 3: Alternative Key Layouts

#### WASD + NumPad Layout (for FPS gamers)
```java
put(KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD1);  // Direct numpad
put(KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD2);
put(KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD3);
put(KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD4);
put(KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD5);
put(KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD6);
put(KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD7);
put(KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD8);
put(KeyEvent.VK_NUMPAD9, KeyEvent.VK_NUMPAD9);
```

#### Gaming Layout (WASD + JKL)
```java
put(KeyEvent.VK_U, KeyEvent.VK_NUMPAD1);  // U=1
put(KeyEvent.VK_I, KeyEvent.VK_NUMPAD2);  // I=2
put(KeyEvent.VK_O, KeyEvent.VK_NUMPAD3);  // O=3
put(KeyEvent.VK_J, KeyEvent.VK_NUMPAD4);  // J=4
put(KeyEvent.VK_K, KeyEvent.VK_NUMPAD5);  // K=5
put(KeyEvent.VK_L, KeyEvent.VK_NUMPAD6);  // L=6
put(KeyEvent.VK_M, KeyEvent.VK_NUMPAD7);  // M=7
put(KeyEvent.VK_COMMA, KeyEvent.VK_NUMPAD8);  // ,=8
put(KeyEvent.VK_PERIOD, KeyEvent.VK_NUMPAD9); // .=9
```

## Available KeyEvent Constants

Common keys you can use in your mappings:

```java
// Letter keys
KeyEvent.VK_A through KeyEvent.VK_Z

// Number keys (top row)
KeyEvent.VK_0 through KeyEvent.VK_9

// Numpad keys
KeyEvent.VK_NUMPAD0 through KeyEvent.VK_NUMPAD9
KeyEvent.VK_MULTIPLY  // *
KeyEvent.VK_ADD       // +
KeyEvent.VK_SUBTRACT  // -
KeyEvent.VK_DIVIDE    // /

// Special keys
KeyEvent.VK_SPACE
KeyEvent.VK_ENTER
KeyEvent.VK_ESCAPE
KeyEvent.VK_SHIFT
KeyEvent.VK_CONTROL
KeyEvent.VK_ALT

// Arrow keys
KeyEvent.VK_UP
KeyEvent.VK_DOWN
KeyEvent.VK_LEFT
KeyEvent.VK_RIGHT

// Function keys
KeyEvent.VK_F1 through KeyEvent.VK_F12
```

## Game-Specific Tips

### Snake Games
- Primary: S (5) for direction changes
- Arrow keys for navigation

### Racing Games
- S (5) or SPACE for accelerate
- Z (7), C (9) for steering

### Puzzle Games
- S (5) for select/confirm
- Arrow keys for navigation
- B (#) for menu

### Action Games
- S (5) for fire/action
- Arrow keys for movement
- B (#) for special weapon

## Troubleshooting

### Keys Not Working?
1. Ensure the game window has focus (click on it)
2. Check if the game uses non-standard input
3. Try the arrow keys and ENTER as fallbacks

### Wrong Key Detected?
Some games may interpret keys differently. Try:
1. Using the actual numpad on your keyboard
2. Modifying the key mappings
3. Checking the game's documentation

### Multiple Keys at Once?
The key mapper should handle this, but if you experience issues:
1. Press one key at a time
2. Ensure clean key releases
3. Check for key ghosting on your keyboard

## Advanced Configuration

### Enabling/Disabling Key Mapping
The `KeyboardMapper` class has methods:
```java
keyMapper.setEnabled(false);  // Disable mapping
keyMapper.setEnabled(true);   // Enable mapping
```

### Runtime Key Updates
```java
keyMapper.updateMapping(KeyEvent.VK_Q, KeyEvent.VK_NUMPAD7);
keyMapper.removeMapping(KeyEvent.VK_Q);
```

---

For more information, see [README.md](README.md)
