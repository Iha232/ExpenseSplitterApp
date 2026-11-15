package view.components;

import controller.ExpenseController;
import model.Balance;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BottomPanel extends JPanel {
    private ExpenseController controller;
    private JComboBox<Balance> settleComboBox;
    private JButton settleButton;
    private JButton refreshButton;
    private JLabel statusLabel;

    public BottomPanel(ExpenseController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadSettleOptions();
    }

    private void initializeComponents() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Quick Actions",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)));

        settleComboBox = new JComboBox<>();
        settleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Balance balance) {
                    String status = balance.getNetBalance().compareTo(java.math.BigDecimal.ZERO) < 0 ? " (Owes)" : " (Gets back)";
                    setText(balance.getMemberName() + status);
                }
                return this;
            }
        });

        settleButton = new JButton("Settle Up");
        settleButton.setBackground(new Color(46, 125, 50));
        settleButton.setForeground(Color.BLACK);
        settleButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        settleButton.setFocusPainted(false);

        refreshButton = new JButton("Refresh All");
        refreshButton.addActionListener(e -> {
            loadSettleOptions();
            if (onRefreshAll != null) onRefreshAll.run();
        });

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.GRAY);

        settleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSettleUp();
            }
        });
    }

    private Runnable onRefreshAll;

    public void setOnRefreshAll(Runnable callback) {
        this.onRefreshAll = callback;
    }

    private void layoutComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        add(new JLabel("Settle for:"));
        add(settleComboBox);
        add(settleButton);
        add(refreshButton);
        add(Box.createHorizontalGlue());
        add(statusLabel);
    }

    public void loadSettleOptions() {
        try {
            List<Balance> balances = controller.getBalances();
            settleComboBox.removeAllItems();

            for (Balance b : balances) {
                if (b.getNetBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
                    settleComboBox.addItem(b);
                }
            }
        } catch (Exception ex) {
            showStatus("Error loading settle options: " + ex.getMessage(), true);
        }
    }

    private void handleSettleUp() {
        Balance selected = (Balance) settleComboBox.getSelectedItem();
        if (selected == null) {
            showStatus("No member selected or all settled", true);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark all dues for " + selected.getMemberName() + " as settled?\n" +
                        "Amount: " + selected.getStatus(),
                "Confirm Settlement", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.settleMemberDues(selected.getMemberId());
                if (success) {
                    showStatus("Settlement recorded for " + selected.getMemberName(), false);
                    loadSettleOptions();
                    if (onRefreshAll != null) onRefreshAll.run();
                } else {
                    showStatus("No pending dues to settle", true);
                }
            } catch (Exception ex) {
                showStatus("Error: " + ex.getMessage(), true);
            }
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(46, 125, 50));
    }
    
    /**
     * Gets the settle combo box for external access.
     * 
     * @return the settle combo box
     */
    public JComboBox<Balance> getSettleComboBox() {
        return settleComboBox;
    }
    
    /**
     * Triggers the settle up action programmatically.
     * 
     * Demonstrates: Programmatic event triggering
     */
    public void triggerSettleUp() {
        handleSettleUp();
    }
}