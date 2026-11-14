package view;

import controller.ExpenseController;
import view.components.AuthPanel;

import javax.swing.*;
import java.awt.*;

public class AuthFrame extends JFrame {
    private ExpenseController controller;
    private AuthPanel authPanel;

    public AuthFrame(ExpenseController controller) {
        this.controller = controller;
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Expense Splitter - Login / Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setMinimumSize(new Dimension(400, 450));
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }
    }

    private void initializeComponents() {
        authPanel = new AuthPanel(controller);
        authPanel.setOnLoginSuccessCallback(this::onLoginSuccess);
        add(authPanel);
    }

    private void onLoginSuccess() {
        // Close auth frame and open main frame
        SwingUtilities.invokeLater(() -> {
            dispose();
            MainFrame mainFrame = new MainFrame(controller);
            mainFrame.setVisible(true);
        });
    }
}