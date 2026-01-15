package com.wiggly;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Visual settings dialog with T9 keyboard layout showing key assignments
 */
public class SettingsDialog extends JDialog {
    private KeyboardMapper keyMapper;
    private Map<Integer, Integer> workingMapping;
    private Map<Integer, JButton> keyButtons;
    private Map<Integer, String> buttonBaseLabels;  // Store original labels
    private Integer pendingT9Key = null;
    
    // T9 key codes for the visual keyboard
    private static final int[] T9_KEYS = {
        KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3,
        KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,
        KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
        KeyEvent.VK_MULTIPLY, KeyEvent.VK_NUMPAD0, KeyEvent.VK_ADD
    };
    
    // Additional control keys
    private static final int KEY_SOFT_LEFT = 1000;  // Left soft key
    private static final int KEY_SOFT_RIGHT = 1001; // Right soft key
    private static final int KEY_CALL = 1002;       // Green call button
    private static final int KEY_DISCONNECT = 1003; // Red disconnect button
    private static final int KEY_UP = KeyEvent.VK_UP;
    private static final int KEY_DOWN = KeyEvent.VK_DOWN;
    private static final int KEY_LEFT = KeyEvent.VK_LEFT;
    private static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    private static final int KEY_OK = KeyEvent.VK_ENTER;
    
    public SettingsDialog(Frame parent, KeyboardMapper keyMapper) {
        super(parent, "T9 Keyboard Settings", true);
        this.keyMapper = keyMapper;
        this.workingMapping = new HashMap<>(keyMapper.getKeyMapping());
        this.keyButtons = new HashMap<>();
        this.buttonBaseLabels = new HashMap<>();
        
        setSize(750, 850);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        
        initUI();
        setLocationRelativeTo(parent);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JLabel headerLabel = new JLabel("Customize Your T9 Keyboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Center panel with keyboard visual
        JPanel keyboardPanel = createKeyboardPanel();
        mainPanel.add(keyboardPanel, BorderLayout.CENTER);
        
        // Instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        JTextArea instructions = new JTextArea(
            "Click any button on the virtual phone to reassign it.\n" +
            "Then press the keyboard key you want to use.\n" +
            "Press ESC to cancel assignment.");
        instructions.setEditable(false);
        instructions.setBackground(new Color(255, 255, 200));
        instructions.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructions.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionsPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(instructionsPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton resetButton = new JButton("Reset to Default");
        resetButton.addActionListener(e -> resetToDefault());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveSettings());
        saveButton.setBackground(new Color(100, 200, 100));
        
        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Global key listener for capturing key presses
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(this::handleKeyCapture);
    }
    
    private JPanel createKeyboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Phone body panel
        JPanel phonePanel = new JPanel();
        phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.Y_AXIS));
        phonePanel.setBackground(new Color(40, 40, 40));
        phonePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.DARK_GRAY, 15, true),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // Top row: Soft keys, D-pad, and action buttons
        JPanel topSection = createTopSection();
        topSection.setMaximumSize(new Dimension(650, 250));
        phonePanel.add(topSection);
        phonePanel.add(Box.createVerticalStrut(25));
        
        // T9 Keypad
        JPanel keypadPanel = createT9KeypadPanel();
        keypadPanel.setMaximumSize(new Dimension(650, 400));
        phonePanel.add(keypadPanel);
        
        mainPanel.add(phonePanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createTopSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Left column - soft key and call button
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 5, 15);
        JButton leftSoftKey = createSmallButton("═", KEY_SOFT_LEFT, "Left Soft Key");
        leftSoftKey.setPreferredSize(new Dimension(60, 35));
        panel.add(leftSoftKey, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 5, 15);
        JButton callButton = createControlButton("📞", KEY_CALL, "Call/Answer");
        callButton.setPreferredSize(new Dimension(60, 45));
        callButton.setBackground(new Color(50, 180, 50));
        panel.add(callButton, gbc);
        
        // Center - D-pad
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(5, 15, 5, 15);
        panel.add(createDPadPanel(), gbc);
        gbc.gridheight = 1;
        
        // Right column - soft key and disconnect button
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.insets = new Insets(5, 15, 5, 10);
        JButton rightSoftKey = createSmallButton("═", KEY_SOFT_RIGHT, "Right Soft Key");
        rightSoftKey.setPreferredSize(new Dimension(60, 35));
        panel.add(rightSoftKey, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 15, 5, 10);
        JButton disconnectButton = createControlButton("⊗", KEY_DISCONNECT, "End/Disconnect");
        disconnectButton.setPreferredSize(new Dimension(60, 45));
        disconnectButton.setBackground(new Color(200, 50, 50));
        panel.add(disconnectButton, gbc);
        
        return panel;
    }
    
    private JButton createSmallButton(String label, int keyCode, String tooltip) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(65, 35));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 100, 100), 1, true),
            new EmptyBorder(3, 3, 3, 3)
        ));
        
        button.addActionListener(e -> startKeyAssignment(keyCode, button));
        
        keyButtons.put(keyCode, button);
        buttonBaseLabels.put(keyCode, label);
        updateButtonLabel(button, keyCode);
        
        return button;
    }
    
    private JPanel createDPadPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Create a circular D-pad appearance
        JPanel dpadCircle = new JPanel(new GridBagLayout());
        dpadCircle.setPreferredSize(new Dimension(140, 140));
        dpadCircle.setBackground(new Color(50, 100, 160));
        dpadCircle.setBorder(new LineBorder(new Color(80, 130, 190), 3, true));
        
        GridBagConstraints dpadGbc = new GridBagConstraints();
        dpadGbc.insets = new Insets(1, 1, 1, 1);
        
        // Up
        dpadGbc.gridx = 1; dpadGbc.gridy = 0;
        JButton upBtn = createDPadButton("▲", KEY_UP, "Up");
        dpadCircle.add(upBtn, dpadGbc);
        
        // Left
        dpadGbc.gridx = 0; dpadGbc.gridy = 1;
        JButton leftBtn = createDPadButton("◄", KEY_LEFT, "Left");
        dpadCircle.add(leftBtn, dpadGbc);
        
        // OK (center)
        dpadGbc.gridx = 1; dpadGbc.gridy = 1;
        JButton okButton = createDPadButton("OK", KEY_OK, "OK/Select");
        okButton.setPreferredSize(new Dimension(45, 45));
        okButton.setBackground(new Color(30, 80, 140));
        okButton.setFont(new Font("Arial", Font.BOLD, 11));
        dpadCircle.add(okButton, dpadGbc);
        
        // Right
        dpadGbc.gridx = 2; dpadGbc.gridy = 1;
        JButton rightBtn = createDPadButton("►", KEY_RIGHT, "Right");
        dpadCircle.add(rightBtn, dpadGbc);
        
        // Down
        dpadGbc.gridx = 1; dpadGbc.gridy = 2;
        JButton downBtn = createDPadButton("▼", KEY_DOWN, "Down");
        dpadCircle.add(downBtn, dpadGbc);
        
        panel.add(dpadCircle);
        return panel;
    }
    
    private JButton createDPadButton(String label, int keyCode, String tooltip) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(40, 40));
        button.setBackground(new Color(60, 110, 170));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(new LineBorder(new Color(80, 130, 190), 1, true));
        
        button.addActionListener(e -> startKeyAssignment(keyCode, button));
        
        keyButtons.put(keyCode, button);
        buttonBaseLabels.put(keyCode, label);
        updateButtonLabel(button, keyCode);
        
        return button;
    }
    
    private JPanel createT9KeypadPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 8, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 30, 5, 30));
        
        String[] labels = {"1\n", "2\nABC", "3\nDEF", 
                          "4\nGHI", "5\nJKL", "6\nMNO",
                          "7\nPQRS", "8\nTUV", "9\nWXYZ",
                          "*\n+", "0\n ", "#\n"};
        
        for (int i = 0; i < T9_KEYS.length; i++) {
            int t9Key = T9_KEYS[i];
            JButton button = createT9Button(labels[i], t9Key);
            panel.add(button);
        }
        
        return panel;
    }
    
    private JButton createT9Button(String label, int t9Key) {
        // Store the base label
        buttonBaseLabels.put(t9Key, label);
        
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(85, 75));
        button.setMinimumSize(new Dimension(85, 75));
        button.setMaximumSize(new Dimension(85, 75));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 80), 2, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        button.addActionListener(e -> startKeyAssignment(t9Key, button));
        
        keyButtons.put(t9Key, button);
        updateButtonLabel(button, t9Key);
        
        return button;
    }
    
    private JButton createControlButton(String label, int keyCode, String tooltip) {
        // Store the base label
        buttonBaseLabels.put(keyCode, label);
        
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(60, 50));
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 100, 100), 2, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        button.addActionListener(e -> startKeyAssignment(keyCode, button));
        
        keyButtons.put(keyCode, button);
        updateButtonLabel(button, keyCode);
        
        return button;
    }
    
    private void startKeyAssignment(int t9Key, JButton button) {
        pendingT9Key = t9Key;
        
        // Highlight the button
        button.setBackground(new Color(255, 200, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.ORANGE, 3, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        // Show status
        String keyName = getT9KeyName(t9Key);
        JOptionPane pane = new JOptionPane(
            "Press a key to assign to: " + keyName + "\nPress ESC to cancel",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{},
            null
        );
        
        JDialog dialog = pane.createDialog(this, "Waiting for key...");
        dialog.setModal(false);
        dialog.setVisible(true);
        
        // Store dialog reference to close it later
        button.putClientProperty("assignDialog", dialog);
    }
    
    private boolean handleKeyCapture(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED || pendingT9Key == null) {
            return false;
        }
        
        int pressedKey = e.getKeyCode();
        
        // Close any open assignment dialog
        JButton button = keyButtons.get(pendingT9Key);
        JDialog dialog = (JDialog) button.getClientProperty("assignDialog");
        if (dialog != null) {
            dialog.dispose();
        }
        
        // Cancel on ESC
        if (pressedKey == KeyEvent.VK_ESCAPE) {
            resetButtonAppearance(button, pendingT9Key);
            pendingT9Key = null;
            return true;
        }
        
        // Update the mapping (reverse: QWERTY key -> T9 key)
        workingMapping.put(pressedKey, pendingT9Key);
        
        // Update button appearance
        resetButtonAppearance(button, pendingT9Key);
        updateButtonLabel(button, pendingT9Key);
        
        // Update all other buttons in case there was a conflict
        updateAllButtonLabels();
        
        pendingT9Key = null;
        e.consume();
        return true;
    }
    
    private void resetButtonAppearance(JButton button, int t9Key) {
        // Reset to original color based on button type
        if (t9Key == KEY_CALL) {
            button.setBackground(new Color(50, 180, 50));
        } else if (t9Key == KEY_DISCONNECT) {
            button.setBackground(new Color(200, 50, 50));
        } else if (t9Key == KEY_OK) {
            button.setBackground(new Color(30, 80, 140));
        } else if (t9Key == KEY_SOFT_LEFT || t9Key == KEY_SOFT_RIGHT) {
            button.setBackground(new Color(80, 80, 80));
        } else if (t9Key >= KeyEvent.VK_NUMPAD0 && t9Key <= KeyEvent.VK_NUMPAD9 || 
                   t9Key == KeyEvent.VK_MULTIPLY || t9Key == KeyEvent.VK_ADD) {
            button.setBackground(new Color(50, 50, 50));
        } else if (t9Key == KEY_UP || t9Key == KEY_DOWN || t9Key == KEY_LEFT || t9Key == KEY_RIGHT) {
            button.setBackground(new Color(60, 110, 170));
        } else {
            button.setBackground(new Color(70, 70, 70));
        }
        
        // Reset border based on button type
        if (t9Key >= KeyEvent.VK_NUMPAD0 && t9Key <= KeyEvent.VK_NUMPAD9 || 
            t9Key == KeyEvent.VK_MULTIPLY || t9Key == KeyEvent.VK_ADD) {
            button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(80, 80, 80), 2, true),
                new EmptyBorder(5, 5, 5, 5)
            ));
        } else if (t9Key == KEY_UP || t9Key == KEY_DOWN || t9Key == KEY_LEFT || t9Key == KEY_RIGHT || t9Key == KEY_OK) {
            button.setBorder(new LineBorder(new Color(80, 130, 190), 1, true));
        } else {
            button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                new EmptyBorder(3, 3, 3, 3)
            ));
        }
    }
    
    private void updateButtonLabel(JButton button, int t9Key) {
        // Get the base label
        String baseLabel = buttonBaseLabels.get(t9Key);
        if (baseLabel == null) {
            baseLabel = getT9KeyName(t9Key);
        }
        
        // Find which QWERTY key is mapped to this T9 key
        String assignedKey = "—";
        for (Map.Entry<Integer, Integer> entry : workingMapping.entrySet()) {
            if (entry.getValue() == t9Key) {
                assignedKey = KeyEvent.getKeyText(entry.getKey());
                break;
            }
        }
        
        // Update tooltip
        String t9Name = getT9KeyName(t9Key);
        button.setToolTipText(t9Name + " ← " + assignedKey);
        
        // Build the label based on button type
        String displayLabel;
        if (baseLabel.contains("\n")) {
            // T9 keypad button with multi-line text
            String[] parts = baseLabel.split("\n");
            displayLabel = "<html><center><b style='font-size:16px'>" + parts[0] + "</b>";
            if (parts.length > 1) {
                displayLabel += "<br><span style='font-size:9px'>" + parts[1] + "</span>";
            }
            displayLabel += "<br><span style='font-size:10px;color:#FFA500'>→ " + assignedKey + "</span></center></html>";
        } else {
            // Control button (D-pad, call, etc.)
            displayLabel = "<html><center>" + baseLabel + 
                         "<br><span style='font-size:9px;color:#FFA500'>→ " + assignedKey + "</span></center></html>";
        }
        
        button.setText(displayLabel);
    }
    
    private void updateAllButtonLabels() {
        for (Map.Entry<Integer, JButton> entry : keyButtons.entrySet()) {
            updateButtonLabel(entry.getValue(), entry.getKey());
        }
    }
    
    private String getT9KeyName(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_NUMPAD0 -> "0";
            case KeyEvent.VK_NUMPAD1 -> "1";
            case KeyEvent.VK_NUMPAD2 -> "2";
            case KeyEvent.VK_NUMPAD3 -> "3";
            case KeyEvent.VK_NUMPAD4 -> "4";
            case KeyEvent.VK_NUMPAD5 -> "5";
            case KeyEvent.VK_NUMPAD6 -> "6";
            case KeyEvent.VK_NUMPAD7 -> "7";
            case KeyEvent.VK_NUMPAD8 -> "8";
            case KeyEvent.VK_NUMPAD9 -> "9";
            case KeyEvent.VK_MULTIPLY -> "*";
            case KeyEvent.VK_ADD -> "#";
            case KEY_SOFT_LEFT -> "Left Soft Key";
            case KEY_SOFT_RIGHT -> "Right Soft Key";
            case KEY_CALL -> "Call/Answer";
            case KEY_DISCONNECT -> "Disconnect/End";
            case KeyEvent.VK_UP -> "Up";
            case KeyEvent.VK_DOWN -> "Down";
            case KeyEvent.VK_LEFT -> "Left";
            case KeyEvent.VK_RIGHT -> "Right";
            case KeyEvent.VK_ENTER -> "OK/Enter";
            default -> KeyEvent.getKeyText(keyCode);
        };
    }
    
    private void resetToDefault() {
        int result = JOptionPane.showConfirmDialog(this,
            "Reset all key mappings to default values?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            workingMapping.clear();
            workingMapping.putAll(getDefaultMapping());
            updateAllButtonLabels();
        }
    }
    
    private Map<Integer, Integer> getDefaultMapping() {
        Map<Integer, Integer> defaults = new HashMap<>();
        defaults.put(KeyEvent.VK_Q, KeyEvent.VK_NUMPAD1);
        defaults.put(KeyEvent.VK_W, KeyEvent.VK_NUMPAD2);
        defaults.put(KeyEvent.VK_E, KeyEvent.VK_NUMPAD3);
        defaults.put(KeyEvent.VK_A, KeyEvent.VK_NUMPAD4);
        defaults.put(KeyEvent.VK_S, KeyEvent.VK_NUMPAD5);
        defaults.put(KeyEvent.VK_D, KeyEvent.VK_NUMPAD6);
        defaults.put(KeyEvent.VK_Z, KeyEvent.VK_NUMPAD7);
        defaults.put(KeyEvent.VK_X, KeyEvent.VK_NUMPAD8);
        defaults.put(KeyEvent.VK_C, KeyEvent.VK_NUMPAD9);
        defaults.put(KeyEvent.VK_V, KeyEvent.VK_MULTIPLY);
        defaults.put(KeyEvent.VK_SPACE, KeyEvent.VK_NUMPAD0);
        defaults.put(KeyEvent.VK_B, KeyEvent.VK_ADD);
        // D-pad keys are direct mappings by default
        // Call/Disconnect would need custom handling if used
        return defaults;
    }
    
    private void saveSettings() {
        // Apply the working mapping to the actual keyboard mapper
        for (Map.Entry<Integer, Integer> entry : workingMapping.entrySet()) {
            keyMapper.updateMapping(entry.getKey(), entry.getValue());
        }
        
        JOptionPane.showMessageDialog(this,
            "Key mappings saved successfully!",
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
}
