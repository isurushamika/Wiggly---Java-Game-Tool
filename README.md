# Wiggly - JAR Game Launcher

A Windows application that runs Java JAR games with T9 keypad emulation through QWERTY keyboard mapping.

## Features

- 🎮 Load and play JAR-based games
- ⌨️ QWERTY to T9 keypad mapping
- 🖥️ User-friendly GUI interface
- 📱 Support for both portrait and landscape games
- 🔄 Easy orientation switching with multiple size presets
- ⚙️ Visual settings with interactive T9 keyboard
- 🎯 Fully customizable key bindings with live preview
- 📱 Mobile game compatibility

## Default Key Mapping

The application maps your QWERTY keyboard to a T9 keypad layout:

```
QWERTY Layout:        T9 Keypad:
Q  W  E               1  2  3
A  S  D        →      4  5  6
Z  X  C               7  8  9
V  SPACE  B           *  0  #
```

### Complete Key Mapping:
- **Q** → 1
- **W** → 2
- **E** → 3
- **A** → 4
- **S** → 5 (often used as Select)
- **D** → 6
- **Z** → 7
- **X** → 8
- **C** → 9
- **V** → * (Star key)
- **SPACE** → 0
- **B** → # (Hash key)

### Additional Controls:
- **ESC** → Back/Exit
- **ENTER** → OK/Select
- **Arrow Keys** → Navigation

## Requirements

- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- Windows OS

## Building the Application

1. **Clone or navigate to the project directory:**
   ```bash
   cd d:\Projects\Dev\wiggly
   ```

2. **Build the project using Maven:**
   ```bash
   mvn clean package
   ```

3. **The executable JAR will be created at:**
   ```
   target/wiggly-launcher.jar
   ```

## Running the Application

### Option 1: Using Java
```bash
java -jar target/wiggly-launcher.jar
```

### Option 2: Double-click
Simply double-click the `wiggly-launcher.jar` file in Windows Explorer.

### Option 3: Create a Windows Shortcut
1. Right-click on `wiggly-launcher.jar`
2. Select "Create shortcut"
3. Rename to "Wiggly Game Launcher"
4. (Optional) Add a custom icon

## How to Use

1. **Launch the application**
   - Run the JAR file using one of the methods above

2. **Select orientation**
   - Click 📱 Portrait for vertical games (240x320)
   - Click 📺 Landscape for horizontal games (320x240)
   - Click 📱+ or 📺+ for larger screen sizes (320x480 or 480x320)
   - You can switch orientation at any time, even while a game is running

3. **Load a game**
   - Click the "Load JAR Game" button
   - Browse and select your game's JAR file
   - The game will load in the main window with the selected orientation

4. **Play the game**
   - Use the QWERTY keys as mapped to T9 keypad
   - Refer to the key mapping panel on the right side

5. **Customize controls** (⚙ Settings button)
   - Click the Settings button to open the visual keyboard configurator
   - See the virtual T9 phone with all buttons displayed
   - Click any button on the virtual phone to reassign it
   - Press the keyboard key you want to use for that button
   - Changes show in real-time with orange indicators
   - Save your custom layout or reset to defaults

## Troubleshooting

### Game doesn't load
- Ensure the JAR file contains a valid main class
- Check that the game is designed for J2ME or similar platforms
- Verify Java version compatibility

### Game looks stretched or squished
- Try switching between portrait and landscape modes
- Use the larger size options (📱+ or 📺+) for bigger games
- Some games may have fixed aspect ratios

### Keys not responding
- Make sure the game window has focus
- Check if the game uses standard Java key events
- Try restarting the application

### Performance issues
- Close other applications to free up memory
- Ensure your Java installation is up to date
- Check game-specific requirements

## Project Structure

```
wiggly/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── wiggly/
│                   ├── GameLauncher.java      # Main application window
│                   ├── KeyboardMapper.java    # Key mapping logic
│                   └── ConfigPanel.java       # Configuration UI
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

## Development

### Adding New Features

1. **Custom Key Mappings:**
   - Modify `DEFAULT_KEY_MAPPING` in `GameLauncher.java`
   - Add UI controls in `ConfigPanel.java`

2. **Supporting More Game Types:**
   - Extend the `findMainClass()` method in `GameLauncher.java`
   - Add specific game type handlers

3. **Enhanced UI:**
   - Modify the Swing components in `GameLauncher.java`
   - Add new panels and controls as needed

## Known Limitations

- Some JAR games may not be compatible with direct loading
- Key mapping works best with games using standard Java key events
- Games with custom input handling may require additional configuration

## License

This project is provided as-is for personal use.

## Contributing

Feel free to fork and modify for your needs. Suggestions and improvements are welcome!

## Support

For issues or questions, please create an issue in the project repository.

---

**Enjoy your retro gaming experience! 🎮**
