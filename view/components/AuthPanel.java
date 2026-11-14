package view.components;

import controller.ExpenseController;
import model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthPanel extends JPanel {
    private ExpenseController controller;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Login components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    private JButton switchToRegisterButton;
    private JLabel loginStatusLabel;
    
    // Register components
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JPasswordField registerConfirmPasswordField;
    private JTextField registerEmailField;
    private JButton registerButton;
    private JButton switchToLoginButton;
    private JLabel registerStatusLabel;
    
    private Runnable onLoginSuccessCallback;

    public AuthPanel(ExpenseController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
    }

    public void setOnLoginSuccessCallback(Runnable callback) {
        this.onLoginSuccessCallback = callback;
    }

    private void initializeComponents() {
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Login Panel
        JPanel loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "LOGIN");
        
        // Register Panel
        JPanel registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "REGISTER");
        
        cardLayout.show(cardPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Login",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        loginUsernameField = new JTextField(20);
        panel.add(loginUsernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        loginPasswordField = new JPasswordField(20);
        panel.add(loginPasswordField, gbc);
        
        // Login Button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(46, 125, 50));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 35));
        panel.add(loginButton, gbc);
        
        // Switch to Register
        gbc.gridy = 3;
        switchToRegisterButton = new JButton("Don't have an account? Register");
        switchToRegisterButton.setBorderPainted(false);
        switchToRegisterButton.setContentAreaFilled(false);
        switchToRegisterButton.setForeground(new Color(33, 150, 243));
        switchToRegisterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(switchToRegisterButton, gbc);
        
        // Status Label
        gbc.gridy = 4;
        loginStatusLabel = new JLabel(" ");
        loginStatusLabel.setForeground(Color.RED);
        panel.add(loginStatusLabel, gbc);
        
        // Action Listeners
        loginButton.addActionListener(this::handleLogin);
        switchToRegisterButton.addActionListener(e -> {
            clearLoginForm();
            cardLayout.show(cardPanel, "REGISTER");
        });
        
        // Enter key support
        loginPasswordField.addActionListener(this::handleLogin);
        loginUsernameField.addActionListener(this::handleLogin);
        
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Register",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        registerUsernameField = new JTextField(20);
        panel.add(registerUsernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        registerPasswordField = new JPasswordField(20);
        panel.add(registerPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        registerConfirmPasswordField = new JPasswordField(20);
        panel.add(registerConfirmPasswordField, gbc);
        
        // Email (optional)
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email (optional):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        registerEmailField = new JTextField(20);
        panel.add(registerEmailField, gbc);
        
        // Register Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(33, 150, 243));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 35));
        panel.add(registerButton, gbc);
        
        // Switch to Login
        gbc.gridy = 5;
        switchToLoginButton = new JButton("Already have an account? Login");
        switchToLoginButton.setBorderPainted(false);
        switchToLoginButton.setContentAreaFilled(false);
        switchToLoginButton.setForeground(new Color(33, 150, 243));
        switchToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(switchToLoginButton, gbc);
        
        // Status Label
        gbc.gridy = 6;
        registerStatusLabel = new JLabel(" ");
        registerStatusLabel.setForeground(Color.RED);
        panel.add(registerStatusLabel, gbc);
        
        // Action Listeners
        registerButton.addActionListener(this::handleRegister);
        switchToLoginButton.addActionListener(e -> {
            clearRegisterForm();
            cardLayout.show(cardPanel, "LOGIN");
        });
        
        // Enter key support
        registerConfirmPasswordField.addActionListener(this::handleRegister);
        registerEmailField.addActionListener(this::handleRegister);
        
        return panel;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Center the card panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(cardPanel);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void handleLogin(ActionEvent e) {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty()) {
            showLoginStatus("Please enter username", true);
            return;
        }
        if (password.isEmpty()) {
            showLoginStatus("Please enter password", true);
            return;
        }
        
        try {
            User user = controller.loginUser(username, password);
            if (user != null) {
                showLoginStatus("Login successful!", false);
                if (onLoginSuccessCallback != null) {
                    SwingUtilities.invokeLater(onLoginSuccessCallback);
                }
            } else {
                showLoginStatus("Invalid username or password", true);
            }
        } catch (Exception ex) {
            showLoginStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void handleRegister(ActionEvent e) {
        String username = registerUsernameField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(registerConfirmPasswordField.getPassword());
        String email = registerEmailField.getText().trim();
        
        if (username.isEmpty()) {
            showRegisterStatus("Please enter username", true);
            return;
        }
        if (password.isEmpty()) {
            showRegisterStatus("Please enter password", true);
            return;
        }
        if (password.length() < 4) {
            showRegisterStatus("Password must be at least 4 characters", true);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showRegisterStatus("Passwords do not match", true);
            return;
        }
        
        try {
            User user = controller.registerUser(username, password, email.isEmpty() ? null : email);
            showRegisterStatus("Registration successful! Please login.", false);
            clearRegisterForm();
            // Switch to login after short delay
            Timer timer = new Timer(1500, evt -> cardLayout.show(cardPanel, "LOGIN"));
            timer.setRepeats(false);
            timer.start();
        } catch (Exception ex) {
            showRegisterStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void showLoginStatus(String message, boolean isError) {
        loginStatusLabel.setText(message);
        loginStatusLabel.setForeground(isError ? Color.RED : new Color(46, 125, 50));
    }

    private void showRegisterStatus(String message, boolean isError) {
        registerStatusLabel.setText(message);
        registerStatusLabel.setForeground(isError ? Color.RED : new Color(46, 125, 50));
    }

    private void clearLoginForm() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        loginStatusLabel.setText(" ");
    }

    private void clearRegisterForm() {
        registerUsernameField.setText("");
        registerPasswordField.setText("");
        registerConfirmPasswordField.setText("");
        registerEmailField.setText("");
        registerStatusLabel.setText(" ");
    }
}