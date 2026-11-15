package view;

import controller.ExpenseController;
import services.BackupService;
import services.ExportService;
import services.SearchService;
import view.components.BottomPanel;
import view.components.DashboardPanel;
import view.components.LedgerPanel;
import view.components.RoommateFormPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;

/**
 * Main application frame with enhanced features.
 * 
 * This class demonstrates:
 * - Menu bar with file operations
 * - Toolbar with quick actions
 * - Search functionality
 * - Event handling (ActionListener, KeyListener)
 * - File chooser dialogs
 * - Integration with services
 * - Event handling (Delegation Event Model)
 * 
 * Syllabus Topics:
 * - Swing event handling
 * - Delegation Event Model
 * - Event sources (JButton, JMenuItem, JTextField)
 * - Event listeners (ActionListener, KeyListener)
 * - Event objects (ActionEvent, KeyEvent)
 * - Event handling methods
 * - File chooser dialogs
 * - Menu and toolbar creation
 * 
 * @see java.awt.event.ActionListener
 * @see java.awt.event.KeyListener
 * @see javax.swing.JFileChooser
 * @see javax.swing.JMenuBar
 */
public class MainFrame extends JFrame {
    private ExpenseController controller;
    private RoommateFormPanel formPanel;
    private DashboardPanel dashboardPanel;
    private LedgerPanel ledgerPanel;
    private BottomPanel bottomPanel;
    
    // Services
    private services.ExportService exportService;
    private services.BackupService backupService;
    private services.SearchService searchService;
    
    // UI Components
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JLabel statusLabel;

    public MainFrame(ExpenseController controller) {
        this.controller = controller;
        this.exportService = new services.ExportService();
        this.backupService = new services.BackupService();
        this.searchService = new services.SearchService();
        
        initializeFrame();
        initializeComponents();
        layoutComponents();
    }

    private void initializeFrame() {
        setTitle("ExpenseSplitterApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 750));
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

        // Initialize services
        exportService = new services.ExportService();
        backupService = new services.BackupService();
        searchService = new services.SearchService();

        // Create menu bar
        createMenuBar();
        
        // Create toolbar
        createToolBar();

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
    
    /**
     * Creates the menu bar with File, Edit, Tools, and Help menus.
     * 
     * Demonstrates:
     * - JMenuBar creation
     * - JMenu and JMenuItem creation
     * - ActionListener for menu items
     * - Keyboard shortcuts (accelerators)
     * - Separators
     * 
     * Syllabus Topics:
     * - Swing event handling
     * - ActionListener
     * - Event sources (JMenuItem)
     * - Event objects (ActionEvent)
     * - Keyboard accelerators
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem exportItem = new JMenuItem("Export Expense Report");
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        exportItem.addActionListener(e -> exportExpenseReport());
        fileMenu.add(exportItem);
        
        JMenuItem exportSummaryItem = new JMenuItem("Export Simple Summary");
        exportSummaryItem.addActionListener(e -> exportSimpleSummary());
        fileMenu.add(exportSummaryItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> handleLogout());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.addActionListener(e -> refreshAll());
        editMenu.add(refreshItem);
        
        menuBar.add(editMenu);
        
        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        
        JMenuItem settleItem = new JMenuItem("Settle Dues");
        settleItem.addActionListener(e -> showSettleDialog());
        toolsMenu.add(settleItem);
        
        toolsMenu.addSeparator();
        
        JMenuItem demoItem = new JMenuItem("String vs StringBuffer Demo");
        demoItem.addActionListener(e -> showStringBufferDemo());
        toolsMenu.add(demoItem);
        
        menuBar.add(toolsMenu);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        JMenuItem syllabusItem = new JMenuItem("Syllabus Implementation");
        syllabusItem.addActionListener(e -> showSyllabusInfo());
        helpMenu.add(syllabusItem);
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    // ── Toolbar button styling constants (neutral, modern appearance) ──────────
    // Neutral buttons: white/light grey background, subtle grey border, dark text
    private static final Color BTN_ACTION_BG      = new Color(250, 250, 250);   // white/light grey
    private static final Color BTN_ACTION_HOVER   = new Color(235, 235, 235);   // light grey hover
    private static final Color BTN_PRESSED        = new Color(220, 220, 220);   // slightly darker grey pressed
    private static final Color BTN_FG             = new Color(51, 51, 51);      // dark grey text

    private static final Color BTN_BORDER         = new Color(200, 200, 200);   // subtle grey border

    /**
     * Creates the bottom toolbar with two neutral-styled JButton components:
     * Refresh All and Export Report.
     * 
     * Demonstrates:
     * - JToolBar / JButton creation
     * - Custom painting (icons drawn with Java2D, no external library)
     * - MouseListener for hover / pressed visual feedback
     * - Hand cursor on hover
     * - ActionListener — all existing handler methods are reused unchanged
     */
    private void createToolBar() {
        toolBar = new JToolBar("Main Toolbar");
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BTN_BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        toolBar.setBackground(new Color(252, 252, 253));

        // ── Refresh All ──
        JButton refreshBtn = makeActionButton(
                "Refresh All", "refresh",
                "Refresh all data (F5)");
        refreshBtn.addActionListener(e -> refreshAll());
        toolBar.add(refreshBtn);

        toolBar.add(makeToolbarSeparator());

        // ── Export Report ──
        JButton exportBtn = makeActionButton(
                "Export Report", "export",
                "Export expense report (Ctrl+E)");
        exportBtn.addActionListener(e -> exportExpenseReport());
        toolBar.add(exportBtn);

        // Push status label to the right
        toolBar.add(Box.createHorizontalGlue());
    }

    /**
     * Builds a single styled JButton for the toolbar matching the "Add Expense" button style.
     * 
     * @param label   visible button text
     * @param iconKey key that selects which icon to draw
     * @param tip     tooltip text
     * @return a styled JButton
     */
    private JButton makeActionButton(String label, String iconKey, String tip) {
        
        // State holder for hover/pressed colors
        final Color[] currentBg = { BTN_ACTION_BG };

        Icon icon = makeActionIcon(iconKey);

        JButton btn = new JButton(label, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Rounded fill
                g2.setColor(currentBg[0]);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                // Border
                g2.setColor(BTN_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setToolTipText(tip);
        btn.setForeground(BTN_FG);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));  // Bold like Add Expense
        btn.setIconTextGap(6);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);

        // Transparent defaults so our custom paint shows through
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        // Padding - match Add Expense button
        btn.setMargin(new Insets(6, 14, 6, 14));

        // Hand cursor
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover / press effects
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                currentBg[0] = BTN_ACTION_HOVER;
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                currentBg[0] = BTN_ACTION_BG;
                btn.repaint();
            }
            @Override public void mousePressed(MouseEvent e) {
                currentBg[0] = BTN_PRESSED;
                btn.repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                currentBg[0] = btn.contains(e.getPoint()) ? BTN_ACTION_HOVER : BTN_ACTION_BG;
                btn.repaint();
            }
        });

        return btn;
    }

    /**
     * Returns a thin separator component for spacing between toolbar groups.
     */
    private Component makeToolbarSeparator() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BTN_BORDER);
                int cx = getWidth() / 2;
                g.drawLine(cx, 6, cx, getHeight() - 6);
            }
        };
        sep.setPreferredSize(new Dimension(14, 32));
        sep.setOpaque(false);
        return sep;
    }

    /**
     * Draws a simple vector icon for each toolbar button using Java2D.
     * No external icon library is required.
     *
     * @param key   one of the iconKey strings passed to makeActionButton
     * @return      a 20×20 Icon
     */
    private Icon makeActionIcon(String key) {
        final int SIZE = 20;
        Color iconColor = BTN_FG;
        
        return new Icon() {
            @Override public int getIconWidth()  { return SIZE; }
            @Override public int getIconHeight() { return SIZE; }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                                    RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(iconColor);
                g2.translate(x, y);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
                                             BasicStroke.JOIN_ROUND));

                switch (key) {

                    // ➕  New Expense — plus sign inside a circle
                    case "new_expense" -> {
                        g2.drawOval(1, 1, SIZE - 2, SIZE - 2);
                        g2.drawLine(SIZE / 2, 5, SIZE / 2, SIZE - 5);
                        g2.drawLine(5, SIZE / 2, SIZE - 5, SIZE / 2);
                    }

                    // 👤+  Add Roommate — person silhouette with a small +
                    case "add_roommate" -> {
                        // Head
                        g2.drawOval(3, 1, 8, 7);
                        // Body arc
                        g2.drawArc(1, 10, 12, 10, 0, 180);
                        // Plus sign (top-right)
                        g2.drawLine(15, 7,  15, 13);
                        g2.drawLine(12, 10, 18, 10);
                    }

                    // ↺  Refresh — circular arrows
                    case "refresh" -> {
                        // Outer arc (top-right portion)
                        g2.drawArc(2, 2, SIZE - 4, SIZE - 4, 30, 270);
                        // Arrow head at the end of the arc
                        int[] ax = { SIZE - 5, SIZE - 2, SIZE - 1 };
                        int[] ay = { 4,        2,        7        };
                        g2.fillPolygon(ax, ay, 3);
                    }

                    // 📄  Export Report — page with an upward arrow
                    case "export" -> {
                        // Page outline
                        g2.drawRect(3, 1, 11, 15);
                        // Fold corner
                        g2.drawLine(10, 1, 14, 5);
                        g2.drawLine(10, 1, 10, 5);
                        g2.drawLine(10, 5, 14, 5);
                        // Up-arrow inside page
                        g2.drawLine(8, 13, 8, 7);
                        g2.drawLine(8, 7, 6, 10);
                        g2.drawLine(8, 7, 10, 10);
                    }

                    default -> {
                        // Fallback: simple square
                        g2.drawRect(3, 3, SIZE - 6, SIZE - 6);
                    }
                }
                g2.dispose();
            }
        };
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Tabbed pane
        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.addTab("Dashboard", dashboardPanel);
        mainTabbedPane.addTab("Ledger", ledgerPanel);
        mainTabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Bottom wrapper: toolbar at top, bottom panel at bottom
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.add(toolBar, BorderLayout.NORTH);
        bottomWrapper.add(bottomPanel, BorderLayout.SOUTH);

        // Centre wrapper: tabs in center, toolbar + bottom panel at bottom
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(mainTabbedPane, BorderLayout.CENTER);
        centerWrapper.add(bottomWrapper, BorderLayout.SOUTH);

        // Add to main frame
        add(formPanel, BorderLayout.WEST);
        add(centerWrapper, BorderLayout.CENTER);

        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    // Event handler methods
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.logout();
            dispose();
            
            SwingUtilities.invokeLater(() -> {
                AuthFrame authFrame = new AuthFrame(controller);
                authFrame.setVisible(true);
            });
        }
    }
    
    private void refreshAll() {
        setStatus("Refreshing...");
        try {
            dashboardPanel.loadBalances();
            ledgerPanel.loadLedger();
            formPanel.refreshRoommates();
            bottomPanel.loadSettleOptions();
            setStatus("Refreshed successfully");
        } catch (Exception e) {
            setStatus("Refresh failed: " + e.getMessage());
        }
    }
    
    private void exportExpenseReport() {
        setStatus("Exporting report...");
        try {
            threads.BackgroundTask<File, Integer> task = new threads.BackgroundTask<File, Integer>("ExportReport") {
                private services.ExportService exportService = new services.ExportService();
                
                @Override
                protected File doInBackground() throws Exception {
                    publishProgress(10);
                    Thread.sleep(100);
                    
                    List<model.Roommate> roommates = controller.getAllRoommates();
                    publishProgress(30);
                    Thread.sleep(100);
                    
                    List<model.Expense> expenses = controller.getAllExpenses();
                    publishProgress(60);
                    Thread.sleep(100);
                    
                    List<model.Balance> balances = controller.getBalances();
                    publishProgress(80);
                    Thread.sleep(100);
                    
                    File result = new services.ExportService().exportExpenseReport(null, 
                        controller.getAllRoommates(), controller.getAllExpenses(), controller.getBalances());
                    publishProgress(100);
                    return result;
                }
                
                @Override
                protected void onSuccess(File result) {
                    setStatus("Report exported to: " + result.getName());
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Expense report exported successfully!\nFile: " + result.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                }
                
                @Override
                protected void onError(Throwable error) {
                    setStatus("Export failed: " + error.getMessage());
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Export failed: " + error.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            };
            task.execute();
        } catch (Exception e) {
            setStatus("Export failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportSimpleSummary() {
        setStatus("Exporting summary...");
        try {
            threads.BackgroundTask<File, Integer> task = new threads.BackgroundTask<File, Integer>("ExportSummary") {
                private services.ExportService exportService = new services.ExportService();
                
                @Override
                protected File doInBackground() throws Exception {
                    List<model.Expense> expenses = controller.getAllExpenses();
                    return exportService.exportSimpleSummary(expenses);
                }
                
                @Override
                protected void onSuccess(File result) {
                    setStatus("Summary exported to: " + result.getName());
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Summary exported successfully!\nFile: " + result.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                }
                
                @Override
                protected void onError(Throwable error) {
                    setStatus("Export failed: " + error.getMessage());
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Export failed: " + error.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            };
            task.execute();
        } catch (Exception e) {
            setStatus("Export failed: " + e.getMessage());
        }
    }
    
    private void backupData() {
        setStatus("Creating backup...");
        try {
            threads.BackgroundTask<File, Integer> task = new threads.BackgroundTask<File, Integer>("BackupData") {
                private services.BackupService backupService = new services.BackupService();
                
                @Override
                protected File doInBackground() throws Exception {
                    publishProgress(20);
                    Thread.sleep(100);
                    
                    List<model.Roommate> roommates = controller.getAllRoommates();
                    publishProgress(40);
                    Thread.sleep(100);
                    
                    List<model.Expense> expenses = controller.getAllExpenses();
                    publishProgress(60);
                    Thread.sleep(100);
                    
                    List<model.ExpenseSplit> allSplits = new java.util.ArrayList<>();
                    for (model.Expense e : controller.getAllExpenses()) {
                        allSplits.addAll(controller.getSplitsForExpense(e.getExpenseId()));
                    }
                    publishProgress(80);
                    Thread.sleep(100);
                    
                    List<model.Balance> balances = controller.getBalances();
                    
                    File result = backupService.createBackup(
                        controller.getAllRoommates(), controller.getAllExpenses(), allSplits, controller.getBalances());
                    publishProgress(100);
                    return result;
                }
                
                @Override
                protected void onSuccess(File result) {
                    setStatus("Backup created: " + result.getName());
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Backup created successfully!\nFile: " + result.getAbsolutePath(),
                        "Backup Successful", JOptionPane.INFORMATION_MESSAGE);
                }
                
                @Override
                protected void onError(Throwable error) {
                    setStatus("Backup failed: " + error.getMessage());
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Backup failed: " + error.getMessage(),
                        "Backup Error", JOptionPane.ERROR_MESSAGE);
                }
            };
            task.execute();
        } catch (Exception e) {
            setStatus("Backup failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Backup failed: " + e.getMessage(),
                    "Backup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void restoreData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File to Restore");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Backup Files (*.ser)", "ser"));
        fileChooser.setCurrentDirectory(new File("backups"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File backupFile = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                    "This will restore data from the backup file.\nExisting data may be overwritten.\n\nContinue?",
                    "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                setStatus("Restoring backup...");
                try {
                    threads.BackgroundTask<services.BackupService.BackupData, Integer> task = 
                        new threads.BackgroundTask<services.BackupService.BackupData, Integer>("RestoreData") {
                            private services.BackupService backupService = new services.BackupService();
                            
                            @Override
                            protected services.BackupService.BackupData doInBackground() throws Exception {
                                return backupService.restoreBackup(new File("backups"));
                            }
                            
                            @Override
                            protected void onSuccess(services.BackupService.BackupData result) {
                                refreshAll();
                                setStatus("Backup restored successfully");
                                JOptionPane.showMessageDialog(MainFrame.this,
                                    "Data restored from backup successfully!",
                                    "Restore Successful", JOptionPane.INFORMATION_MESSAGE);
                            }
                            
                            @Override
                            protected void onError(Throwable error) {
                                setStatus("Restore failed: " + error.getMessage());
                                JOptionPane.showMessageDialog(MainFrame.this,
                                    "Restore failed: " + error.getMessage(),
                                    "Restore Error", JOptionPane.ERROR_MESSAGE);
                            }
                        };
                    task.execute();
                } catch (Exception e) {
                    setStatus("Restore failed: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, "Restore failed: " + e.getMessage(),
                            "Restore Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showSettleDialog() {
        if (bottomPanel.getSettleComboBox().getItemCount() > 0) {
            // Trigger settle action via reflection
            try {
                java.lang.reflect.Method method = bottomPanel.getClass().getDeclaredMethod("triggerSettleUp");
                method.setAccessible(true);
                method.invoke(bottomPanel);
            } catch (Exception ex) {
                // Ignore
            }
        } else {
            JOptionPane.showMessageDialog(this, "No members with pending dues to settle.",
                    "No Dues", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showStringBufferDemo() {
        services.ExportService exportService = new services.ExportService();
        String demo = exportService.demonstrateStringVsStringBuffer(10000);
        JOptionPane.showMessageDialog(this, demo, 
            "String vs StringBuffer Performance Demo", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "ExpenseSplitterApp\n" +
            "Version 1.0\n\n" +
            "A Java Swing desktop application for managing shared expenses.\n\n" +
            "Features:\n" +
            "- User authentication\n" +
            "- Roommate management\n" +
            "- Expense tracking with equal splitting\n" +
            "- Balance dashboard\n" +
            "- Transaction ledger\n" +
            "- Settlement management\n" +
            "- Data export/import\n" +
            "- Backup & restore\n\n" +
            "Built with Java Swing, JDBC, and MySQL\n" +
            "Demonstrates Java Units 3 & 4 syllabus concepts",
            "About ExpenseSplitterApp", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSyllabusInfo() {
        String info = "JAVA SYLLABUS IMPLEMENTATION\n\n" +
            "UNIT 3 - PACKAGES AND INTERFACES:\n" +
            "• Packages: model, dao, controller, view, view.components, interfaces, exceptions, utils, services, threads, events\n" +
            "• Interfaces: ExpenseRepository, RoommateRepository, Exportable, NotificationListener\n" +
            "• Custom Exceptions: InvalidExpenseException, InvalidRoommateException, DuplicateUsernameException,\n" +
            "  DatabaseConnectionException, InsufficientParticipantException\n" +
            "• Exception Handling: try-catch-finally, multiple catch, throw/throws, custom exceptions\n" +
            "• I/O: PrintWriter, ObjectOutputStream, ObjectInputStream, File I/O\n" +
            "• Serialization: BackupData implements Serializable\n\n" +
            "UNIT 4 - JAVA LIBRARY:\n" +
            "• String Handling: StringUtils with 20+ string operations\n" +
            "• StringBuffer: ExportService uses StringBuffer for report building\n" +
            "• Collections: ArrayList, List, Iterator in SearchService and DAOs\n" +
            "• Event Handling: ActionListener, KeyListener, Delegation Event Model\n" +
            "• Multithreading: TaskExecutor, BackgroundTask, SwingWorker\n" +
            "• Synchronization: ConcurrentHashMap, CopyOnWriteArrayList, AtomicBoolean\n\n" +
            "DATABASE:\n" +
            "• MySQL with JDBC\n" +
            "• PreparedStatement, Batch Operations, Transactions\n" +
            "• Foreign Keys, Cascade Delete\n\n" +
            "ARCHITECTURE: MVC with Interface-based design";
        
        JOptionPane.showMessageDialog(this, info, 
            "Java Syllabus Implementation", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
        System.out.println("[STATUS] " + message);
    }
    
    }