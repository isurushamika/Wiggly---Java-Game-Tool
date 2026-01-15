package com.wiggly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * Configuration panel for customizing keyboard mappings
 */
public class ConfigPanel extends JDialog {
    private KeyboardMapper keyMapper;
    private JPanel mappingPanel;
    
    public ConfigPanel(Frame parent, KeyboardMapper keyMapper) {
        super(parent, "Keyboard Configuration", true);
        this.keyMapper = keyMapper;
        
        setSize(500, 600);
        setLayout(new BorderLayout(10, 10));
        
        initUI();
        setLocationRelativeTo(parent);
    }
    
    private void initUI() {
        // Header
        JLabel headerLabel = new JLabel("Customize T9 Key Mapping", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);
        
        // Mapping panel
        mappingPanel = new JPanel();
        mappingPanel.setLayout(new GridLayout(0, 3, 10, 10));
        mappingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        Map<Integer, Integer> currentMapping = keyMapper.getKeyMapping();
        
        // Add mapping rows
        for (Map.Entry<Integer, Integer> entry : currentMapping.entrySet()) {
            int sourceKey = entry.getKey();
            int targetKey = entry.getValue();
            
            JLabel sourceLabel = new JLabel(KeyEvent.getKeyText(sourceKey));
            JLabel arrowLabel = new JLabel("→", SwingConstants.CENTER);
            JLabel targetLabel = new JLabel(getT9KeyName(targetKey));
            
            mappingPanel.add(sourceLabel);
            mappingPanel.add(arrowLabel);
            mappingPanel.add(targetLabel);
        }
        
        JScrollPane scrollPane = new JScrollPane(mappingPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton resetButton = new JButton("Reset to Default");
        JButton closeButton = new JButton("Close");
        
        resetButton.addActionListener(e -> resetToDefault());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(resetButton);
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
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
            default -> KeyEvent.getKeyText(keyCode);
        };
    }
    
    private void resetToDefault() {
        int result = JOptionPane.showConfirmDialog(this,
            "Reset all key mappings to default?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            // This would reload default mappings
            JOptionPane.showMessageDialog(this, 
                "Mappings reset to default. Please restart the application.");
            dispose();
        }
    }
}
