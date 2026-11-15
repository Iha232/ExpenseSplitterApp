package view.components;

import controller.ExpenseController;
import model.Roommate;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class RoommateFormPanel extends JPanel {
    private ExpenseController controller;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<Roommate> payerComboBox;
    private JButton participantsButton;
    private JPopupMenu participantsPopup;
    private Map<Integer, Boolean> participantSelection = new LinkedHashMap<>();
    private List<Roommate> currentRoommates = new ArrayList<>();
    private JButton addExpenseButton;
    private JButton addRoommateButton;
    private JButton editRoommateButton;
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

        // Participants dropdown button
        participantsButton = new JButton("Select participants ▼");
        participantsButton.setFocusPainted(false);
        participantsPopup = new JPopupMenu();
        participantsButton.addActionListener(e -> participantsPopup.show(participantsButton, 0, participantsButton.getHeight()));

        addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setBackground(new Color(46, 125, 50));
        addExpenseButton.setForeground(Color.BLACK);
        addExpenseButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        addExpenseButton.setFocusPainted(false);

        this.addRoommateButton = new JButton("Add Roommate");
        this.addRoommateButton.setBackground(new Color(33, 150, 243));
        this.addRoommateButton.setForeground(Color.BLACK);
        this.addRoommateButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        this.addRoommateButton.setFocusPainted(false);
        this.addRoommateButton.addActionListener(e -> showAddRoommateDialog());

        this.editRoommateButton = new JButton("Edit Roommate");
        this.editRoommateButton.setBackground(new Color(255, 152, 0));
        this.editRoommateButton.setForeground(Color.BLACK);
        this.editRoommateButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        this.editRoommateButton.setFocusPainted(false);
        this.editRoommateButton.addActionListener(e -> showEditRoommateDialog());

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        addExpenseButton.addActionListener(e -> handleAddExpense());
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

        // Add / Edit Roommate Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addRoommateButton, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        add(editRoommateButton, gbc);

        // Participants dropdown
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Split Between:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(participantsButton, gbc);

        // Add Expense Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addExpenseButton, gbc);

        // Status Label
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(statusLabel, gbc);
    }

    public void loadRoommates() {
        try {
            List<Roommate> roommates = controller.getAllRoommates();
            currentRoommates = roommates;
            payerComboBox.removeAllItems();
            participantSelection.clear();
            participantsPopup.removeAll();

            for (Roommate r : roommates) {
                payerComboBox.addItem(r);
                participantSelection.put(r.getMemberId(), true);
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(r.toString());
                item.setSelected(true);
                item.addActionListener(ev -> {
                    participantSelection.put(r.getMemberId(), item.isSelected());
                    updateParticipantsButtonText();
                });
                participantsPopup.add(item);
            }
            payerComboBox.revalidate();
            updateParticipantsButtonText();
        } catch (Exception ex) {
            showStatus("Error loading roommates: " + ex.getMessage(), true);
        }
    }

    private void updateParticipantsButtonText() {
        long selectedCount = participantSelection.values().stream().filter(v -> v).count();
        participantsButton.setText("Split between (" + selectedCount + "/" + currentRoommates.size() + ") ▼");
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
        for (Map.Entry<Integer, Boolean> entry : participantSelection.entrySet()) {
            if (entry.getValue()) participantIds.add(entry.getKey());
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
        // reset all selections to true
        for (Integer id : participantSelection.keySet()) {
            participantSelection.put(id, true);
        }
        // update UI checkboxes
        for (Component comp : participantsPopup.getComponents()) {
            if (comp instanceof JCheckBoxMenuItem item) {
                item.setSelected(true);
            }
        }
        updateParticipantsButtonText();
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(46, 125, 50));
    }

    public void refreshRoommates() {
        loadRoommates();
    }

    private void showAddRoommateDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Roommate", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        dialog.add(nameField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField phoneField = new JTextField(20);
        dialog.add(phoneField, gbc);

        // UPI ID
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("UPI ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField upiField = new JTextField(20);
        dialog.add(upiField, gbc);

        // Status label
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        dialog.add(statusLabel, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 125, 50));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String upi = upiField.getText().trim();

            if (name.isEmpty()) {
                statusLabel.setText("Name is required");
                return;
            }

            try {
                Roommate roommate = new Roommate();
                roommate.setName(name);
                roommate.setPhone(phone);
                roommate.setUpiId(upi);
                controller.addRoommate(roommate);
                loadRoommates();
                dialog.dispose();
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditRoommateDialog() {
        if (currentRoommates.isEmpty()) {
            showStatus("No roommates to edit", true);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Roommate", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Roommate selector
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Roommate:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JComboBox<Roommate> roommateCombo = new JComboBox<>(currentRoommates.toArray(new Roommate[0]));
        dialog.add(roommateCombo, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        dialog.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        dialog.add(nameField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField phoneField = new JTextField(20);
        dialog.add(phoneField, gbc);

        // UPI ID
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        dialog.add(new JLabel("UPI ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField upiField = new JTextField(20);
        dialog.add(upiField, gbc);

        // Status label
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        dialog.add(statusLabel, gbc);

        // Fill fields when selection changes
        roommateCombo.addActionListener(e -> {
            Roommate selected = (Roommate) roommateCombo.getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.getName());
                phoneField.setText(selected.getPhone() != null ? selected.getPhone() : "");
                upiField.setText(selected.getUpiId() != null ? selected.getUpiId() : "");
            }
        });
        // Initialize with first
        if (roommateCombo.getItemCount() > 0) {
            Roommate first = (Roommate) roommateCombo.getItemAt(0);
            nameField.setText(first.getName());
            phoneField.setText(first.getPhone() != null ? first.getPhone() : "");
            upiField.setText(first.getUpiId() != null ? first.getUpiId() : "");
        }

        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 125, 50));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            Roommate selected = (Roommate) roommateCombo.getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Select a roommate");
                return;
            }
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String upi = upiField.getText().trim();

            if (name.isEmpty()) {
                statusLabel.setText("Name is required");
                return;
            }

            try {
                selected.setName(name);
                selected.setPhone(phone);
                selected.setUpiId(upi);
                controller.updateRoommate(selected);
                loadRoommates();
                dialog.dispose();
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
    
    /**
     * Gets the description field for external access.
     * 
     * @return the description field
     */
    public JTextField getDescriptionField() {
        return descriptionField;
    }
}