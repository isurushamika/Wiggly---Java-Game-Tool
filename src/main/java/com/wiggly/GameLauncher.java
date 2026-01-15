package com.wiggly;

import javax.swing.*;
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
    private JTextArea keyMappingInfo;
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
        
        // Right panel with key mapping info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(200, 0));
        
        JLabel infoLabel = new JLabel("T9 Key Mapping:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(infoLabel, BorderLayout.NORTH);
        
        keyMappingInfo = new JTextArea();
        keyMappingInfo.setEditable(false);
        keyMappingInfo.setText(getKeyMappingText());
        keyMappingInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(keyMappingInfo);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(infoPanel, BorderLayout.EAST);
        
        // Add global key listener
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(keyMapper);
        
        // Update button states
        updateOrientationButtons();
    }
    
    private void openSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(this, keyMapper);
        settingsDialog.setVisible(true);
        // Update the key mapping display after settings dialog closes
        updateKeyMappingDisplay();
    }
    
    private void updateKeyMappingDisplay() {
        keyMappingInfo.setText(getKeyMappingText());
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
    
    private String getKeyMappingText() {
        StringBuilder sb = new StringBuilder();
        sb.append("QWERTY → T9 Keypad\n");
        sb.append("==================\n\n");
        
        // Get current mappings from keyMapper
        Map<Integer, Integer> mappings = keyMapper.getKeyMapping();
        
        // Group mappings by T9 key
        Map<Integer, String> t9ToQwerty = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : mappings.entrySet()) {
            int qwertyKey = entry.getKey();
            int t9Key = entry.getValue();
            String qwertyName = KeyEvent.getKeyText(qwertyKey);
            t9ToQwerty.put(t9Key, qwertyName);
        }
        
        // T9 Number pad
        sb.append("Number Pad:\n");
        for (int i = 1; i <= 9; i++) {
            int keyCode = KeyEvent.VK_NUMPAD0 + i;
            String qwerty = t9ToQwerty.getOrDefault(keyCode, "—");
            sb.append(qwerty).append(" → ").append(i);
            if (i == 5) sb.append(" (Select)");
            sb.append("\n");
            if (i % 3 == 0) sb.append("\n");
        }
        
        String zero = t9ToQwerty.getOrDefault(KeyEvent.VK_NUMPAD0, "—");
        String star = t9ToQwerty.getOrDefault(KeyEvent.VK_MULTIPLY, "—");
        String hash = t9ToQwerty.getOrDefault(KeyEvent.VK_ADD, "—");
        
        sb.append(star).append(" → *\n");
        sb.append(zero).append(" → 0\n");
        sb.append(hash).append(" → #\n\n");
        
        sb.append("==================\n");
        sb.append("Additional Keys:\n");
        sb.append("ESC → Back/Exit\n");
        sb.append("ENTER → OK/Select\n");
        sb.append("Arrow Keys → Navigation\n");
        
        return sb.toString();
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
