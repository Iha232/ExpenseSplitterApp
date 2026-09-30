package view;

import controller.ExpenseController;
import view.components.BottomPanel;
import view.components.DashboardPanel;
import view.components.LedgerPanel;
import view.components.RoommateFormPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private ExpenseController controller;
    private RoommateFormPanel formPanel;
    private DashboardPanel dashboardPanel;
    private LedgerPanel ledgerPanel;
    private BottomPanel bottomPanel;

    public MainFrame(ExpenseController controller) {
        this.controller = controller;
        initializeFrame();
        initializeComponents();
        layoutComponents();
    }

    private void initializeFrame() {
        setTitle("Apartment/Hostel Expense Splitter & Settlement Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);

        // Set default look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }
    }

    private void initializeComponents() {
        formPanel = new RoommateFormPanel(controller);
        dashboardPanel = new DashboardPanel(controller);
        ledgerPanel = new LedgerPanel(controller);
        bottomPanel = new BottomPanel(controller);

        // Set up callbacks for cross-component refresh
        formPanel.setOnExpenseAddedCallback(() -> {
            dashboardPanel.loadBalances();
            ledgerPanel.loadLedger();
            bottomPanel.loadSettleOptions();
        });

        bottomPanel.setOnRefreshAll(() -> {
            dashboardPanel.loadBalances();
            ledgerPanel.loadLedger();
            formPanel.refreshRoommates();
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // West Panel - Form
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(350, 0));
        westPanel.add(formPanel, BorderLayout.NORTH);

        // Center Panel - Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Ledger", ledgerPanel);
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Bottom Panel
        bottomPanel.setPreferredSize(new Dimension(0, 80));

        // Add to main frame
        add(westPanel, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add padding around edges
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}