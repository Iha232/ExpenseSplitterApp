package view.components;

import controller.ExpenseController;
import model.Balance;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DashboardPanel extends JPanel {
    private ExpenseController controller;
    private JTable balanceTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public DashboardPanel(ExpenseController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadBalances();
    }

    private void initializeComponents() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Settlement Dashboard",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)));

        String[] columns = {"Member", "Total Paid", "Total Owed", "Net Balance", "UPI ID", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3) {
                    return BigDecimal.class;
                }
                return String.class;
            }
        };

        balanceTable = new JTable(tableModel);
        balanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        balanceTable.setRowHeight(25);
        balanceTable.getTableHeader().setReorderingAllowed(false);
        balanceTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        // Custom renderer for net balance color
        balanceTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof BigDecimal bd) {
                    if (bd.compareTo(BigDecimal.ZERO) > 0) {
                        c.setForeground(new Color(46, 125, 50));
                    } else if (bd.compareTo(BigDecimal.ZERO) < 0) {
                        c.setForeground(new Color(198, 40, 40));
                    } else {
                        c.setForeground(Color.GRAY);
                    }
                    ((JLabel) c).setText("₹" + bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }
                return c;
            }
        });

        // Custom renderer for paid/owed columns
        balanceTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof BigDecimal bd) {
                    ((JLabel) c).setText("₹" + bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    c.setForeground(new Color(46, 125, 50));
                }
                return c;
            }
        });

        balanceTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof BigDecimal bd) {
                    ((JLabel) c).setText("₹" + bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    c.setForeground(new Color(198, 40, 40));
                }
                return c;
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBalances());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        JScrollPane scrollPane = new JScrollPane(balanceTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadBalances() {
        try {
            List<Balance> balances = controller.getBalances();
            tableModel.setRowCount(0);

            for (Balance b : balances) {
                Object[] row = {
                    b.getMemberName(),
                    b.getTotalPaid(),
                    b.getTotalOwed(),
                    b.getNetBalance(),
                    b.getUpiId() != null ? b.getUpiId() : "",
                    b.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading balances: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}