package view.components;

import controller.ExpenseController;
import model.Roommate;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoommateFormPanel extends JPanel {
    private ExpenseController controller;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<Roommate> payerComboBox;
    private JPanel checkboxPanel;
    private JButton addExpenseButton;
    private JLabel statusLabel;
    private Runnable onExpenseAddedCallback;

    public RoommateFormPanel(ExpenseController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadRoommates();
    }

    public void setOnExpenseAddedCallback(Runnable callback) {
        this.onExpenseAddedCallback = callback;
    }

    private void initializeComponents() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Add New Expense",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)));

        descriptionField = new JTextField(20);
        amountField = new JTextField(10);
        payerComboBox = new JComboBox<>();
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setBackground(new Color(46, 125, 50));
        addExpenseButton.setForeground(Color.WHITE);
        addExpenseButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        addExpenseButton.setFocusPainted(false);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddExpense();
            }
        });
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Description
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        add(descriptionField, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        add(amountField, gbc);

        // Payer
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        add(new JLabel("Paid By:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        add(payerComboBox, gbc);

        // Participants
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        add(new JLabel("Split Between:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setPreferredSize(new Dimension(250, 120));
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        add(scrollPane, gbc);

        // Add Expense Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addExpenseButton, gbc);

        // Status Label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(statusLabel, gbc);
    }

    public void loadRoommates() {
        try {
            List<Roommate> roommates = controller.getAllRoommates();
            payerComboBox.removeAllItems();
            checkboxPanel.removeAll();

            for (Roommate r : roommates) {
                payerComboBox.addItem(r);
                JCheckBox checkBox = new JCheckBox(r.toString());
                checkBox.setName(String.valueOf(r.getMemberId()));
                checkBox.setSelected(true);
                checkboxPanel.add(checkBox);
            }

            checkboxPanel.revalidate();
            checkboxPanel.repaint();
            payerComboBox.revalidate();
        } catch (Exception ex) {
            showStatus("Error loading roommates: " + ex.getMessage(), true);
        }
    }

    private void handleAddExpense() {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        Roommate selectedPayer = (Roommate) payerComboBox.getSelectedItem();

        if (description.isEmpty()) {
            showStatus("Please enter a description", true);
            return;
        }

        if (amountText.isEmpty()) {
            showStatus("Please enter an amount", true);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException ex) {
            showStatus("Invalid amount format", true);
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showStatus("Amount must be greater than zero", true);
            return;
        }

        if (selectedPayer == null) {
            showStatus("Please select a payer", true);
            return;
        }

        List<Integer> participantIds = new ArrayList<>();
        for (Component comp : checkboxPanel.getComponents()) {
            if (comp instanceof JCheckBox checkBox && checkBox.isSelected()) {
                participantIds.add(Integer.parseInt(checkBox.getName()));
            }
        }

        if (participantIds.isEmpty()) {
            showStatus("Please select at least one participant", true);
            return;
        }

        try {
            int expenseId = controller.addExpense(
                    description,
                    amount,
                    selectedPayer.getMemberId(),
                    participantIds,
                    LocalDate.now()
            );

            showStatus("Expense added successfully (ID: " + expenseId + ")", false);
            clearForm();

            if (onExpenseAddedCallback != null) {
                onExpenseAddedCallback.run();
            }
        } catch (Exception ex) {
            showStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void clearForm() {
        descriptionField.setText("");
        amountField.setText("");
        for (Component comp : checkboxPanel.getComponents()) {
            if (comp instanceof JCheckBox checkBox) {
                checkBox.setSelected(true);
            }
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(46, 125, 50));
    }

    public void refreshRoommates() {
        loadRoommates();
    }
}