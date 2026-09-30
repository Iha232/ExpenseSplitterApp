package view.components;

import controller.ExpenseController;
import model.Expense;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LedgerPanel extends JPanel {
    private ExpenseController controller;
    private JTable ledgerTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton deleteButton;

    public LedgerPanel(ExpenseController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadLedger();
    }

    private void initializeComponents() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Transaction Ledger",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)));

        String[] columns = {"ID", "Date", "Description", "Payer", "Amount (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return BigDecimal.class;
                }
                return String.class;
            }
        };

        ledgerTable = new JTable(tableModel);
        ledgerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ledgerTable.setRowHeight(25);
        ledgerTable.getTableHeader().setReorderingAllowed(false);
        ledgerTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        ledgerTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        ledgerTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        ledgerTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        ledgerTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        ledgerTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Right-align amount column
        ledgerTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof BigDecimal bd) {
                    ((JLabel) c).setText("₹" + bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                }
                return c;
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadLedger());

        deleteButton = new JButton("Delete Selected");
        deleteButton.setBackground(new Color(198, 40, 40));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> handleDeleteExpense());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        JScrollPane scrollPane = new JScrollPane(ledgerTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadLedger() {
        try {
            List<Expense> expenses = controller.getAllExpenses();
            tableModel.setRowCount(0);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

            for (Expense e : expenses) {
                Object[] row = {
                    e.getExpenseId(),
                    e.getExpenseDate().format(dateFormatter),
                    e.getDescription(),
                    e.getPayerName(),
                    e.getTotalAmount()
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading ledger: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteExpense() {
        int selectedRow = ledgerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String description = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete expense: \"" + description + "\" (ID: " + expenseId + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.deleteExpense(expenseId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadLedger();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete expense",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting expense: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}