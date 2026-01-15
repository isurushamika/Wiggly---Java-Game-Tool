package com.wiggly;

import java.awt.AWTException;
import java.awt.KeyEventDispatcher;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps QWERTY keyboard keys to T9 keypad keys
 * Intercepts key events and translates them for game compatibility
 */
public class KeyboardMapper implements KeyEventDispatcher {
    private Map<Integer, Integer> keyMapping;
    private Robot robot;
    private Set<Integer> pressedKeys;
    private boolean enabled;
    
    public KeyboardMapper(Map<Integer, Integer> keyMapping) {
        this.keyMapping = new HashMap<>(keyMapping);
        this.pressedKeys = new HashSet<>();
        this.enabled = true;
        
        try {
            this.robot = new Robot();
            this.robot.setAutoDelay(0);
        } catch (AWTException e) {
            System.err.println("Warning: Could not create Robot for key mapping");
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (!enabled || robot == null) {
            return false;
        }
        
        int keyCode = e.getKeyCode();
        
        // Check if this key should be mapped
        if (keyMapping.containsKey(keyCode)) {
            int mappedKey = keyMapping.get(keyCode);
            
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                // Prevent duplicate presses
                if (!pressedKeys.contains(keyCode)) {
                    pressedKeys.add(keyCode);
                    // Note: Direct robot key events might not work for all games
                    // This is a basic implementation
                    simulateKeyPress(mappedKey, true);
                }
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (pressedKeys.contains(keyCode)) {
                    pressedKeys.remove(keyCode);
                    simulateKeyPress(mappedKey, false);
                }
            }
            
            // Consume the original event to prevent double input
            e.consume();
            return true;
        }
        
        // Allow unmapped keys to pass through
        return false;
    }
    
    private void simulateKeyPress(int keyCode, boolean press) {
        try {
            if (press) {
                robot.keyPress(keyCode);
            } else {
                robot.keyRelease(keyCode);
            }
        } catch (Exception e) {
            System.err.println("Error simulating key: " + keyCode);
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            pressedKeys.clear();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void updateMapping(int sourceKey, int targetKey) {
        keyMapping.put(sourceKey, targetKey);
    }
    
    public void removeMapping(int sourceKey) {
        keyMapping.remove(sourceKey);
    }
    
    public Map<Integer, Integer> getKeyMapping() {
        return new HashMap<>(keyMapping);
    }
    
    public String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }
}
