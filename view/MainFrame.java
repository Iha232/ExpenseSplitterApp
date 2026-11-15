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
    private JTextField searchField;
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
        setTitle("Apartment/Hostel Expense Splitter & Settlement Manager");
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
        
        JMenuItem backupItem = new JMenuItem("Backup Data");
        backupItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK));
        backupItem.addActionListener(e -> backupData());
        fileMenu.add(backupItem);
        
        JMenuItem restoreItem = new JMenuItem("Restore Data");
        restoreItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        restoreItem.addActionListener(e -> restoreData());
        fileMenu.add(restoreItem);
        
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
        
        editMenu.addSeparator();
        
        JMenuItem searchItem = new JMenuItem("Search Expenses");
        searchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        searchItem.addActionListener(e -> searchField.requestFocusInWindow());
        editMenu.add(searchItem);
        
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
    
    /**
     * Creates the toolbar with quick action buttons.
     * 
     * Demonstrates:
     * - JToolBar creation
     * - JButton with icons (text only for simplicity)
     * - ActionListener for buttons
     * - Separators
     */
    private void createToolBar() {
        toolBar = new JToolBar("Main Toolbar");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        
        // New Expense button
        JButton newExpenseBtn = new JButton("New Expense");
        newExpenseBtn.setToolTipText("Add new expense (Ctrl+N)");
        newExpenseBtn.addActionListener(e -> formPanel.getDescriptionField().requestFocusInWindow());
        toolBar.add(newExpenseBtn);
        
        // New Roommate button
        JButton newRoommateBtn = new JButton("Add Roommate");
        newRoommateBtn.setToolTipText("Add new roommate");
        newRoommateBtn.addActionListener(e -> {
            try {
                java.lang.reflect.Method method = formPanel.getClass().getDeclaredMethod("showAddRoommateDialog");
                method.setAccessible(true);
                method.invoke(formPanel);
            } catch (Exception ex) {
                // Ignore
            }
        });
        toolBar.add(newRoommateBtn);
        
        toolBar.addSeparator();
        
        // Refresh button
        JButton refreshBtn = new JButton("Refresh All");
        refreshBtn.setToolTipText("Refresh all data (F5)");
        refreshBtn.addActionListener(e -> refreshAll());
        toolBar.add(refreshBtn);
        
        toolBar.addSeparator();
        
        // Export button
        JButton exportBtn = new JButton("Export Report");
        exportBtn.setToolTipText("Export expense report (Ctrl+E)");
        exportBtn.addActionListener(e -> exportExpenseReport());
        toolBar.add(exportBtn);
        
        // Backup button
        JButton backupBtn = new JButton("Backup");
        backupBtn.setToolTipText("Backup data (Ctrl+B)");
        backupBtn.addActionListener(e -> backupData());
        toolBar.add(backupBtn);
        
        // Restore button
        JButton restoreBtn = new JButton("Restore");
        restoreBtn.setToolTipText("Restore from backup (Ctrl+R)");
        restoreBtn.addActionListener(e -> restoreData());
        toolBar.add(restoreBtn);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Search field
        searchField = new JTextField(30);
        searchField.setToolTipText("Search expenses by description, payer, date, or amount (Ctrl+F)");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            ledgerPanel.loadLedger();
        });
        searchPanel.add(clearBtn);
        searchPanel.add(Box.createHorizontalGlue());
        
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchPanel.add(statusLabel);

        // Tabbed pane
        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.addTab("Dashboard", dashboardPanel);
        mainTabbedPane.addTab("Ledger", ledgerPanel);
        mainTabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Main center panel with search at top, tabs below
        JPanel mainCenter = new JPanel(new BorderLayout());
        mainCenter.add(searchPanel, BorderLayout.NORTH);
        mainCenter.add(mainTabbedPane, BorderLayout.CENTER);

        // Main center wrapper with search at top
        JPanel mainCenterWrapper = new JPanel(new BorderLayout());
        mainCenterWrapper.add(searchPanel, BorderLayout.NORTH);
        mainCenterWrapper.add(mainTabbedPane, BorderLayout.CENTER);

        // Top section with toolbar
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(toolBar, BorderLayout.NORTH);

        // Main center wrapper with toolbar
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(toolBar, BorderLayout.NORTH);
        centerWrapper.add(mainCenterWrapper, BorderLayout.CENTER);

        // Bottom Panel
        bottomPanel.setPreferredSize(new Dimension(0, 80));

        // Add to main frame - simple layout
        add(formPanel, BorderLayout.WEST);
        add(mainCenterWrapper, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add padding around edges
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
    
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            ledgerPanel.loadLedger();
            return;
        }
        
        setStatus("Searching...");
        try {
            threads.BackgroundTask<List<model.Expense>, Void> task = 
                new threads.BackgroundTask<List<model.Expense>, Void>("SearchExpenses") {
                    private services.SearchService searchService = new services.SearchService();
                    
                    @Override
                    protected List<model.Expense> doInBackground() throws Exception {
                        List<model.Expense> allExpenses = controller.getAllExpenses();
                        return new services.SearchService().searchByDescription(allExpenses, query);
                    }
                    
                    @Override
                    protected void onSuccess(List<model.Expense> result) {
                        // Update ledger with search results
                        ledgerPanel.updateLedger(result);
                        setStatus("Found " + result.size() + " matching expenses");
                    }
                    
                    @Override
                    protected void onError(Throwable error) {
                        setStatus("Search failed: " + error.getMessage());
                    }
                };
            task.execute();
        } catch (Exception e) {
            setStatus("Search failed: " + e.getMessage());
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
            "Expense Splitter & Settlement Manager\n" +
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
            "About Expense Splitter", JOptionPane.INFORMATION_MESSAGE);
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
    
    // Getter for search field (for menu access)
    public JTextField getSearchField() {
        return searchField;
    }
    
    // Add method to LedgerPanel to update with search results
    public void updateLedgerWithSearch(List<model.Expense> expenses) {
        ledgerPanel.updateLedger(expenses);
    }
}