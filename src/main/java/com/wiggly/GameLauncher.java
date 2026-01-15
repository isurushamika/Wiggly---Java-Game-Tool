package com.wiggly;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Main application window for launching and playing JAR games
 * with T9 keyboard mapping
 */
public class GameLauncher extends JFrame {
    private JPanel gamePanel;
    private JButton loadGameButton;
    private JButton portraitButton;
    private JButton landscapeButton;
    private JLabel statusLabel;
    private JPanel keyMappingPanel;
    private Map<Integer, JLabel> keyDisplayLabels;
    private File currentGameJar;
    private KeyboardMapper keyMapper;
    private GameOrientation currentOrientation;
    
    // Game orientation modes
    public enum GameOrientation {
        PORTRAIT(240, 320),   // Common J2ME portrait size
        LANDSCAPE(320, 240),  // Common J2ME landscape size
        PORTRAIT_LARGE(320, 480),  // Larger portrait
        LANDSCAPE_LARGE(480, 320); // Larger landscape
        
        private final int width;
        private final int height;
        
        GameOrientation(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public boolean isPortrait() { return height > width; }
    }
    
    // Additional control keys
    private static final int KEY_SOFT_LEFT = 1000;  // Left soft key
    private static final int KEY_SOFT_RIGHT = 1001; // Right soft key
    private static final int KEY_CALL = 1002;       // Green call button
    private static final int KEY_DISCONNECT = 1003; // Red disconnect button

    // Default T9 keypad mapping
    // Q=1, W=2, E=3, A=4, S=5, D=6, Z=7, X=8, C=9, V=*, Space=0, B=#
    private static final Map<Integer, Integer> DEFAULT_KEY_MAPPING = new HashMap<Integer, Integer>() {{
        put(KeyEvent.VK_Q, KeyEvent.VK_NUMPAD1);
        put(KeyEvent.VK_W, KeyEvent.VK_NUMPAD2);
        put(KeyEvent.VK_E, KeyEvent.VK_NUMPAD3);
        put(KeyEvent.VK_A, KeyEvent.VK_NUMPAD4);
        put(KeyEvent.VK_S, KeyEvent.VK_NUMPAD5);
        put(KeyEvent.VK_D, KeyEvent.VK_NUMPAD6);
        put(KeyEvent.VK_Z, KeyEvent.VK_NUMPAD7);
        put(KeyEvent.VK_X, KeyEvent.VK_NUMPAD8);
        put(KeyEvent.VK_C, KeyEvent.VK_NUMPAD9);
        put(KeyEvent.VK_V, KeyEvent.VK_MULTIPLY);  // * key
        put(KeyEvent.VK_SPACE, KeyEvent.VK_NUMPAD0);
        put(KeyEvent.VK_B, KeyEvent.VK_ADD);  // # key (using + as substitute)
    }};
    
    public GameLauncher() {
        setTitle("Wiggly - JAR Game Launcher");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        keyMapper = new KeyboardMapper(DEFAULT_KEY_MAPPING);
        currentOrientation = GameOrientation.PORTRAIT;
        keyDisplayLabels = new HashMap<>();
        
        initUI();
        
        setLocationRelativeTo(null);
    }
    
    private void initUI() {
        // Top panel with controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loadGameButton = new JButton("Load JAR Game");
        loadGameButton.addActionListener(e -> loadGame());
        controlPanel.add(loadGameButton);
        
        JButton settingsButton = new JButton("⚙ Settings");
        settingsButton.addActionListener(e -> openSettings());
        settingsButton.setToolTipText("Configure keyboard mappings");
        controlPanel.add(settingsButton);
        
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Orientation controls
        JLabel orientationLabel = new JLabel("Orientation:");
        controlPanel.add(orientationLabel);
        
        portraitButton = new JButton("📱 Portrait");
        portraitButton.addActionListener(e -> setOrientation(GameOrientation.PORTRAIT));
        portraitButton.setToolTipText("Switch to portrait mode (240x320)");
        controlPanel.add(portraitButton);
        
        landscapeButton = new JButton("📺 Landscape");
        landscapeButton.addActionListener(e -> setOrientation(GameOrientation.LANDSCAPE));
        landscapeButton.setToolTipText("Switch to landscape mode (320x240)");
        controlPanel.add(landscapeButton);
        
        JButton portraitLargeButton = new JButton("📱+");
        portraitLargeButton.addActionListener(e -> setOrientation(GameOrientation.PORTRAIT_LARGE));
        portraitLargeButton.setToolTipText("Large portrait (320x480)");
        controlPanel.add(portraitLargeButton);
        
        JButton landscapeLargeButton = new JButton("📺+");
        landscapeLargeButton.addActionListener(e -> setOrientation(GameOrientation.LANDSCAPE_LARGE));
        landscapeLargeButton.setToolTipText("Large landscape (480x320)");
        controlPanel.add(landscapeLargeButton);
        
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        statusLabel = new JLabel("No game loaded | Portrait mode");
        controlPanel.add(statusLabel);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Center panel for game display with preferred size
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        gamePanel.setPreferredSize(new Dimension(
            currentOrientation.getWidth(), 
            currentOrientation.getHeight()
        ));
        
        // Wrapper panel to center the game panel
        JPanel gamePanelWrapper = new JPanel(new GridBagLayout());
        gamePanelWrapper.setBackground(Color.DARK_GRAY);
        gamePanelWrapper.add(gamePanel);
        
        add(gamePanelWrapper, BorderLayout.CENTER);
        
        // Right panel with key mapping visual
        keyMappingPanel = createKeyMappingPanel();
        keyMappingPanel.setPreferredSize(new Dimension(280, 0));
        add(keyMappingPanel, BorderLayout.EAST);
        
        // Add global key listener
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(keyMapper);
        
        // Update button states
        updateOrientationButtons();
    }
    
    private JPanel createKeyMappingPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(45, 45, 45));
        
        JLabel headerLabel = new JLabel("T9 Key Mapping", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // T9 Visual keyboard
        JPanel t9Panel = new JPanel();
        t9Panel.setLayout(new BoxLayout(t9Panel, BoxLayout.Y_AXIS));
        t9Panel.setBackground(new Color(35, 35, 35));
        t9Panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Control section with soft keys, D-pad, and call buttons arranged around D-pad
        JPanel controlSection = createControlSection();
        controlSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        t9Panel.add(controlSection);
        t9Panel.add(Box.createVerticalStrut(10));
        
        // T9 Keypad
        JPanel keypadPanel = createCompactKeypad();
        keypadPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        t9Panel.add(keypadPanel);
        
        mainPanel.add(t9Panel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createControlSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        // Left soft key (left of D-pad up)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel leftSoftKey = createKeyDisplay("L", KEY_SOFT_LEFT);
        leftSoftKey.setPreferredSize(new Dimension(60, 60));
        panel.add(leftSoftKey, gbc);
        
        // D-pad Up
        gbc.gridx = 1; gbc.gridy = 0;
        JLabel upButton = createKeyDisplay("▲", KeyEvent.VK_UP);
        upButton.setPreferredSize(new Dimension(60, 60));
        panel.add(upButton, gbc);
        
        // Right soft key (right of D-pad up)
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel rightSoftKey = createKeyDisplay("R", KEY_SOFT_RIGHT);
        rightSoftKey.setPreferredSize(new Dimension(60, 60));
        panel.add(rightSoftKey, gbc);
        
        // D-pad Left, OK, Right
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel leftButton = createKeyDisplay("◄", KeyEvent.VK_LEFT);
        leftButton.setPreferredSize(new Dimension(60, 60));
        panel.add(leftButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        JLabel okLabel = createKeyDisplay("OK", KeyEvent.VK_ENTER);
        okLabel.setPreferredSize(new Dimension(60, 60));
        okLabel.setBackground(new Color(60, 100, 150));
        panel.add(okLabel, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        JLabel rightButton = createKeyDisplay("►", KeyEvent.VK_RIGHT);
        rightButton.setPreferredSize(new Dimension(60, 60));
        panel.add(rightButton, gbc);
        
        // Call button (left of down)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel callButton = createKeyDisplay("📞", KEY_CALL);
        callButton.setPreferredSize(new Dimension(60, 60));
        callButton.setBackground(new Color(34, 139, 34));
        panel.add(callButton, gbc);
        
        // D-pad Down
        gbc.gridx = 1; gbc.gridy = 2;
        JLabel downButton = createKeyDisplay("▼", KeyEvent.VK_DOWN);
        downButton.setPreferredSize(new Dimension(60, 60));
        panel.add(downButton, gbc);
        
        // Disconnect button (right of down)
        gbc.gridx = 2; gbc.gridy = 2;
        JLabel disconnectButton = createKeyDisplay("✖", KEY_DISCONNECT);
        disconnectButton.setPreferredSize(new Dimension(60, 60));
        disconnectButton.setBackground(new Color(178, 34, 34));
        panel.add(disconnectButton, gbc);
        
        return panel;
    }

    private JPanel createCompactKeypad() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 4, 4));
        panel.setOpaque(false);
        
        int[] keys = {
            KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3,
            KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,
            KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
            KeyEvent.VK_MULTIPLY, KeyEvent.VK_NUMPAD0, KeyEvent.VK_ADD
        };
        
        String[] labels = {"1", "2\nABC", "3\nDEF", 
                          "4\nGHI", "5\nJKL", "6\nMNO",
                          "7\nPQRS", "8\nTUV", "9\nWXYZ",
                          "*", "0", "#"};
        
        for (int i = 0; i < keys.length; i++) {
            JLabel keyLabel = createKeyDisplay(labels[i], keys[i]);
            keyLabel.setPreferredSize(new Dimension(60, 55));
            panel.add(keyLabel);
        }
        
        return panel;
    }
    
    private JLabel createKeyDisplay(String label, int t9Key) {
        JLabel keyLabel = new JLabel();
        keyLabel.setOpaque(true);
        keyLabel.setBackground(new Color(50, 50, 50));
        keyLabel.setForeground(Color.WHITE);
        keyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        keyLabel.setVerticalAlignment(SwingConstants.CENTER);
        keyLabel.setFont(new Font("Arial", Font.BOLD, 11));
        keyLabel.setBorder(new LineBorder(new Color(80, 80, 80), 1, true));
        keyLabel.setPreferredSize(new Dimension(45, 40));
        
        keyDisplayLabels.put(t9Key, keyLabel);
        updateKeyDisplayLabel(keyLabel, label, t9Key);
        
        return keyLabel;
    }
    
    private void updateKeyDisplayLabel(JLabel keyLabel, String baseLabel, int t9Key) {
        // Find which QWERTY key is mapped to this T9 key
        String assignedKey = "—";
        for (Map.Entry<Integer, Integer> entry : keyMapper.getKeyMapping().entrySet()) {
            if (entry.getValue() == t9Key) {
                assignedKey = KeyEvent.getKeyText(entry.getKey());
                break;
            }
        }
        
        // Build HTML label
        String displayText;
        if (baseLabel.contains("\n")) {
            String[] parts = baseLabel.split("\n");
            displayText = "<html><center><b>" + parts[0] + "</b>";
            if (parts.length > 1) {
                displayText += "<br><span style='font-size:8px'>" + parts[1] + "</span>";
            }
            displayText += "<br><span style='font-size:9px;color:#FFA500'>" + assignedKey + "</span></center></html>";
        } else {
            displayText = "<html><center><b>" + baseLabel + "</b>" +
                         "<br><span style='font-size:9px;color:#FFA500'>" + assignedKey + "</span></center></html>";
        }
        
        keyLabel.setText(displayText);
    }
    
    private void openSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(this, keyMapper);
        settingsDialog.setVisible(true);
        // Update the key mapping display after settings dialog closes
        updateKeyMappingDisplay();
    }
    
    private void updateKeyMappingDisplay() {
        // Update all key display labels
        for (Map.Entry<Integer, JLabel> entry : keyDisplayLabels.entrySet()) {
            int t9Key = entry.getKey();
            JLabel label = entry.getValue();
            
            // Determine base label based on key type
            String baseLabel = getBaseLabelForKey(t9Key);
            updateKeyDisplayLabel(label, baseLabel, t9Key);
        }
    }
    
    private String getBaseLabelForKey(int t9Key) {
        return switch (t9Key) {
            case KeyEvent.VK_NUMPAD1 -> "1";
            case KeyEvent.VK_NUMPAD2 -> "2\nABC";
            case KeyEvent.VK_NUMPAD3 -> "3\nDEF";
            case KeyEvent.VK_NUMPAD4 -> "4\nGHI";
            case KeyEvent.VK_NUMPAD5 -> "5\nJKL";
            case KeyEvent.VK_NUMPAD6 -> "6\nMNO";
            case KeyEvent.VK_NUMPAD7 -> "7\nPQRS";
            case KeyEvent.VK_NUMPAD8 -> "8\nTUV";
            case KeyEvent.VK_NUMPAD9 -> "9\nWXYZ";
            case KeyEvent.VK_NUMPAD0 -> "0";
            case KeyEvent.VK_MULTIPLY -> "*";
            case KeyEvent.VK_ADD -> "#";
            case KeyEvent.VK_UP -> "▲";
            case KeyEvent.VK_DOWN -> "▼";
            case KeyEvent.VK_LEFT -> "◄";
            case KeyEvent.VK_RIGHT -> "►";
            case KeyEvent.VK_ENTER -> "OK";
            case KEY_SOFT_LEFT -> "L";
            case KEY_SOFT_RIGHT -> "R";
            case KEY_CALL -> "📞";
            case KEY_DISCONNECT -> "✖";
            default -> "";
        };
    }
    
    private void setOrientation(GameOrientation orientation) {
        currentOrientation = orientation;
        
        // Update game panel size
        gamePanel.setPreferredSize(new Dimension(
            orientation.getWidth(),
            orientation.getHeight()
        ));
        
        // Update status
        String orientationType = orientation.isPortrait() ? "Portrait" : "Landscape";
        String sizeInfo = orientation.getWidth() + "x" + orientation.getHeight();
        
        if (currentGameJar != null) {
            statusLabel.setText("Game: " + currentGameJar.getName() + 
                              " | " + orientationType + " (" + sizeInfo + ")");
        } else {
            statusLabel.setText("No game loaded | " + orientationType + 
                              " mode (" + sizeInfo + ")");
        }
        
        // Revalidate and repaint
        gamePanel.revalidate();
        gamePanel.repaint();
        
        // Update button states
        updateOrientationButtons();
        
        // Adjust window size if needed
        pack();
    }
    
    private void updateOrientationButtons() {
        portraitButton.setEnabled(currentOrientation != GameOrientation.PORTRAIT);
        landscapeButton.setEnabled(currentOrientation != GameOrientation.LANDSCAPE);
    }
    
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
            }
            public String getDescription() {
                return "JAR Files (*.jar)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentGameJar = fileChooser.getSelectedFile();
            launchGame(currentGameJar);
        }
    }
    
    private void launchGame(File jarFile) {
        try {
            String orientationType = currentOrientation.isPortrait() ? "Portrait" : "Landscape";
            String sizeInfo = currentOrientation.getWidth() + "x" + currentOrientation.getHeight();
            statusLabel.setText("Loading: " + jarFile.getName() + " | " + 
                              orientationType + " (" + sizeInfo + ")");
            
            // Clear previous game
            gamePanel.removeAll();
            
            // Load JAR and find main class
            URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL()},
                this.getClass().getClassLoader()
            );
            
            // Try to find and instantiate the main class
            // This is a basic implementation - may need adjustment based on game type
            String mainClass = findMainClass(jarFile);
            
            if (mainClass != null) {
                Class<?> gameClass = classLoader.loadClass(mainClass);
                Object gameInstance = gameClass.getDeclaredConstructor().newInstance();
                
                // If it's a JPanel or JFrame, add it to our display
                if (gameInstance instanceof JPanel) {
                    gamePanel.add((JPanel) gameInstance, BorderLayout.CENTER);
                } else if (gameInstance instanceof JApplet) {
                    JApplet applet = (JApplet) gameInstance;
                    applet.init();
                    applet.start();
                    gamePanel.add(applet, BorderLayout.CENTER);
                }
                
                gamePanel.revalidate();
                gamePanel.repaint();
                
                statusLabel.setText("Game loaded: " + jarFile.getName() + 
                                  " | " + orientationType + " (" + sizeInfo + ")");
            } else {
                statusLabel.setText("Error: Could not find main class | " + orientationType);
                JOptionPane.showMessageDialog(this, 
                    "Could not find a valid main class in the JAR file.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            String errOrientationType = currentOrientation.isPortrait() ? "Portrait" : "Landscape";
            statusLabel.setText("Error loading game | " + errOrientationType);
            JOptionPane.showMessageDialog(this, 
                "Error loading game: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String findMainClass(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            var manifest = jar.getManifest();
            if (manifest != null) {
                String mainClass = manifest.getMainAttributes().getValue("Main-Class");
                if (mainClass != null) {
                    return mainClass;
                }
            }
            
            // If no manifest, try common patterns
            // This would need to be expanded based on your specific games
            return null;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameLauncher launcher = new GameLauncher();
            launcher.setVisible(true);
        });
    }
}
