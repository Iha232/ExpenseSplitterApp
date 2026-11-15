package view.components;

import controller.ExpenseController;
import model.Balance;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard panel showing member balances with integrated search, filter and sort.
 *
 * New controls (all independent of the Ledger):
 *   - Member search field  : filters by name or ID
 *   - Balance status filter: All / Owes Money / Is Owed Money / Settled
 *   - Sort dropdown        : 9 sorting options
 *   - Settlement filter    : All / Pending / Settled
 *   - Reset Filters button : clears every control, restores full list
 *
 * All filters compose – changing one does not reset the others.
 * UI updates always run on the Event Dispatch Thread.
 */
public class DashboardPanel extends JPanel {

    // ── MVC ────────────────────────────────────────────────────────────────
    private final ExpenseController controller;

    // ── Table ──────────────────────────────────────────────────────────────
    private JTable balanceTable;
    private DefaultTableModel tableModel;

    // ── Filter / search controls ───────────────────────────────────────────
    private JTextField memberSearchField;
    private JComboBox<String> balanceStatusCombo;
    private JComboBox<String> sortCombo;
    private JComboBox<String> settlementCombo;
    private JButton resetFiltersButton;
    private JButton refreshButton;

    // ── Status label ───────────────────────────────────────────────────────
    private JLabel resultLabel;

    // ── Balance Status filter options ──────────────────────────────────────
    private static final String BS_ALL     = "All Members";
    private static final String BS_OWES    = "Owes Money";
    private static final String BS_OWED    = "Is Owed Money";
    private static final String BS_SETTLED = "Settled";

    // ── Sort options ───────────────────────────────────────────────────────
    private static final String SORT_DEFAULT  = "Default Order";
    private static final String SORT_AZ       = "Name: A-Z";
    private static final String SORT_ZA       = "Name: Z-A";
    private static final String SORT_HIGH_OWE = "Highest Amount Owed";
    private static final String SORT_LOW_OWE  = "Lowest Amount Owed";
    private static final String SORT_HIGH_REC = "Highest Amount to Receive";
    private static final String SORT_LOW_REC  = "Lowest Amount to Receive";
    private static final String SORT_HIGH_SPD = "Highest Total Spending";
    private static final String SORT_LOW_SPD  = "Lowest Total Spending";

    // ── Settlement filter options ──────────────────────────────────────────
    private static final String ST_ALL     = "All";
    private static final String ST_PENDING = "Pending";
    private static final String ST_SETTLED = "Settled";

    // ── Master data (full unfiltered list from DB) ─────────────────────────
    private List<Balance> masterBalances = new ArrayList<>();

    // ══════════════════════════════════════════════════════════════════════
    public DashboardPanel(ExpenseController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadBalances();
    }

    // ── Component setup ────────────────────────────────────────────────────

    private void initializeComponents() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Settlement Dashboard",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));

        // ── Table model (ID column hidden via width=0) ──
        String[] columns = {"ID", "Member", "Total Paid", "Total Owed", "Net Balance", "UPI ID", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0)                          return Integer.class;
                if (col == 2 || col == 3 || col == 4)  return BigDecimal.class;
                return String.class;
            }
        };

        balanceTable = new JTable(tableModel);
        balanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        balanceTable.setRowHeight(26);
        balanceTable.getTableHeader().setReorderingAllowed(false);
        balanceTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        // Hide ID column
        balanceTable.getColumnModel().getColumn(0).setMinWidth(0);
        balanceTable.getColumnModel().getColumn(0).setMaxWidth(0);
        balanceTable.getColumnModel().getColumn(0).setWidth(0);

        balanceTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        balanceTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        balanceTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        balanceTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        balanceTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        balanceTable.getColumnModel().getColumn(6).setPreferredWidth(160);

        // Net balance renderer
        balanceTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (value instanceof BigDecimal bd) {
                    int cmp = bd.compareTo(BigDecimal.ZERO);
                    if (!sel) {
                        setForeground(cmp > 0 ? new Color(27, 94, 32)
                                    : cmp < 0 ? new Color(183, 28, 28)
                                    : Color.GRAY);
                    }
                    setText("Rs." + bd.abs().setScale(2, BigDecimal.ROUND_HALF_UP));
                    setHorizontalAlignment(JLabel.RIGHT);
                }
                return c;
            }
        });

        // Total Paid renderer
        balanceTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (value instanceof BigDecimal bd) {
                    setText("Rs." + bd.setScale(2, BigDecimal.ROUND_HALF_UP));
                    if (!sel) setForeground(new Color(27, 94, 32));
                    setHorizontalAlignment(JLabel.RIGHT);
                }
                return c;
            }
        });

        // Total Owed renderer
        balanceTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (value instanceof BigDecimal bd) {
                    setText("Rs." + bd.setScale(2, BigDecimal.ROUND_HALF_UP));
                    if (!sel) setForeground(new Color(183, 28, 28));
                    setHorizontalAlignment(JLabel.RIGHT);
                }
                return c;
            }
        });

        // Status renderer
        balanceTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (!sel && value instanceof String s) {
                    if (s.startsWith("Gets back"))    setForeground(new Color(27, 94, 32));
                    else if (s.startsWith("Owes"))    setForeground(new Color(183, 28, 28));
                    else                              setForeground(Color.GRAY);
                }
                return c;
            }
        });

        // ── Filter controls ──
        memberSearchField = new JTextField(16);
        memberSearchField.setToolTipText("Search by member name or ID");

        balanceStatusCombo = new JComboBox<>(
                new String[]{BS_ALL, BS_OWES, BS_OWED, BS_SETTLED});

        sortCombo = new JComboBox<>(new String[]{
                SORT_DEFAULT, SORT_AZ, SORT_ZA,
                SORT_HIGH_OWE, SORT_LOW_OWE,
                SORT_HIGH_REC, SORT_LOW_REC,
                SORT_HIGH_SPD, SORT_LOW_SPD});

        settlementCombo = new JComboBox<>(
                new String[]{ST_ALL, ST_PENDING, ST_SETTLED});

        resetFiltersButton = new JButton("Reset Filters");
        resetFiltersButton.setFocusPainted(false);
        resetFiltersButton.setToolTipText("Clear all filters and show all members");
        resetFiltersButton.addActionListener(e -> resetFilters());

        refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadBalances());

        resultLabel = new JLabel(" ");
        resultLabel.setForeground(Color.GRAY);
        resultLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));

        // Wire every control to applyFilters()
        memberSearchField.getDocument().addDocumentListener(filterDocListener);
        balanceStatusCombo.addActionListener(e -> applyFilters());
        sortCombo.addActionListener(e -> applyFilters());
        settlementCombo.addActionListener(e -> applyFilters());
    }

    // ── Layout ─────────────────────────────────────────────────────────────

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        // ── Filter section ──
        JPanel filterSection = new JPanel(new GridBagLayout());
        filterSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "Search & Filter",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 11)),
                BorderFactory.createEmptyBorder(2, 4, 4, 4)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0 — Search + Balance Status
        g.gridy = 0; g.gridx = 0; g.weightx = 0;
        filterSection.add(new JLabel("Search Member:"), g);
        g.gridx = 1; g.weightx = 1.0;
        filterSection.add(memberSearchField, g);
        g.gridx = 2; g.weightx = 0;
        filterSection.add(new JLabel("Balance Status:"), g);
        g.gridx = 3; g.weightx = 0.6;
        filterSection.add(balanceStatusCombo, g);

        // Row 1 — Sort + Settlement
        g.gridy = 1; g.gridx = 0; g.weightx = 0;
        filterSection.add(new JLabel("Sort By:"), g);
        g.gridx = 1; g.weightx = 1.0;
        filterSection.add(sortCombo, g);
        g.gridx = 2; g.weightx = 0;
        filterSection.add(new JLabel("Settlement:"), g);
        g.gridx = 3; g.weightx = 0.6;
        filterSection.add(settlementCombo, g);

        // Row 2 — Buttons + result label
        g.gridy = 2; g.gridx = 0; g.gridwidth = 2; g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.add(resetFiltersButton);
        btnPanel.add(refreshButton);
        filterSection.add(btnPanel, g);

        g.gridx = 2; g.gridwidth = 2; g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        filterSection.add(resultLabel, g);

        add(filterSection, BorderLayout.NORTH);

        // ── Table ──
        JScrollPane scrollPane = new JScrollPane(balanceTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);
    }

    // ── Data loading ───────────────────────────────────────────────────────

    /**
     * Fetches all balances from the database and re-runs the filter pipeline.
     * Called on startup, Refresh click, and after any settlement/expense event.
     */
    public void loadBalances() {
        try {
            masterBalances = controller.getBalances();
            applyFilters();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading balances: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Filter + sort pipeline ─────────────────────────────────────────────

    /**
     * Applies all active filters and sorting to {@code masterBalances}
     * and re-renders the table. All four controls compose: changing any
     * single one does not reset the others.
     *
     * Pipeline:
     *   1. Name/ID text search
     *   2. Balance status (All / Owes / Is Owed / Settled)
     *   3. Settlement status (All / Pending / Settled)
     *   4. Sort
     *   5. Render
     */
    private void applyFilters() {
        List<Balance> result = new ArrayList<>(masterBalances);

        // ── 1. Text search (name or ID) ──
        String searchText = memberSearchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            List<Balance> searched = new ArrayList<>();
            for (Balance b : result) {
                boolean nameMatch = b.getMemberName() != null
                        && b.getMemberName().toLowerCase().contains(searchText);
                boolean idMatch = false;
                try {
                    idMatch = (b.getMemberId() == Integer.parseInt(searchText));
                } catch (NumberFormatException ignored) {}

                if (nameMatch || idMatch) searched.add(b);
            }
            result = searched;
        }

        // ── 2. Balance status filter ──
        String bsFilter = (String) balanceStatusCombo.getSelectedItem();
        if (bsFilter != null) {
            if (BS_OWES.equals(bsFilter)) {
                result = result.stream()
                        .filter(b -> b.getNetBalance().compareTo(BigDecimal.ZERO) < 0)
                        .collect(Collectors.toList());
            } else if (BS_OWED.equals(bsFilter)) {
                result = result.stream()
                        .filter(b -> b.getNetBalance().compareTo(BigDecimal.ZERO) > 0)
                        .collect(Collectors.toList());
            } else if (BS_SETTLED.equals(bsFilter)) {
                result = result.stream()
                        .filter(b -> b.getNetBalance().compareTo(BigDecimal.ZERO) == 0)
                        .collect(Collectors.toList());
            }
            // BS_ALL: no filter
        }

        // ── 3. Settlement status filter ──
        // "Pending"  = net balance != 0 (money still owed in either direction)
        // "Settled"  = net balance == 0 (fully balanced out)
        String stFilter = (String) settlementCombo.getSelectedItem();
        if (stFilter != null) {
            if (ST_PENDING.equals(stFilter)) {
                result = result.stream()
                        .filter(b -> b.getNetBalance().compareTo(BigDecimal.ZERO) != 0)
                        .collect(Collectors.toList());
            } else if (ST_SETTLED.equals(stFilter)) {
                result = result.stream()
                        .filter(b -> b.getNetBalance().compareTo(BigDecimal.ZERO) == 0)
                        .collect(Collectors.toList());
            }
            // ST_ALL: no filter
        }

        // ── 4. Sort ──
        String sortKey = (String) sortCombo.getSelectedItem();
        if (sortKey != null && !SORT_DEFAULT.equals(sortKey)) {
            Comparator<Balance> cmp = null;

            if (SORT_AZ.equals(sortKey)) {
                cmp = Comparator.comparing(
                        b -> (b.getMemberName() != null ? b.getMemberName() : ""));

            } else if (SORT_ZA.equals(sortKey)) {
                cmp = Comparator.comparing(
                        (Balance b) -> (b.getMemberName() != null ? b.getMemberName() : ""))
                        .reversed();

            } else if (SORT_HIGH_OWE.equals(sortKey)) {
                // Most negative net balance first (owes most)
                cmp = Comparator.comparing(b -> b.getNetBalance());          // ascending
                cmp = cmp.reversed();                                        // most negative last → need abs
                // Rewrite: sort by netBalance ascending (most negative = highest debt first)
                cmp = Comparator.comparing(Balance::getNetBalance);

            } else if (SORT_LOW_OWE.equals(sortKey)) {
                // Least negative net balance first (owes least)
                cmp = Comparator.comparing(Balance::getNetBalance).reversed();

            } else if (SORT_HIGH_REC.equals(sortKey)) {
                // Most positive net balance first (receives most)
                cmp = Comparator.comparing(
                        (Balance b) -> b.getNetBalance()).reversed();

            } else if (SORT_LOW_REC.equals(sortKey)) {
                // Least positive net balance first (receives least)
                cmp = Comparator.comparing(Balance::getNetBalance);

            } else if (SORT_HIGH_SPD.equals(sortKey)) {
                cmp = Comparator.comparing(
                        (Balance b) -> b.getTotalPaid()).reversed();

            } else if (SORT_LOW_SPD.equals(sortKey)) {
                cmp = Comparator.comparing(Balance::getTotalPaid);
            }

            if (cmp != null) result.sort(cmp);
        }

        // ── 5. Render ──
        renderToTable(result);
    }

    /** Populates the JTable from the filtered+sorted list. */
    private void renderToTable(List<Balance> list) {
        tableModel.setRowCount(0);
        for (Balance b : list) {
            tableModel.addRow(new Object[]{
                b.getMemberId(),
                b.getMemberName(),
                b.getTotalPaid(),
                b.getTotalOwed(),
                b.getNetBalance(),
                b.getUpiId() != null ? b.getUpiId() : "",
                b.getStatus()
            });
        }
        int total = masterBalances.size();
        int shown = list.size();
        resultLabel.setText(shown == total
                ? "Showing all " + total + " member" + (total != 1 ? "s" : "")
                : "Showing " + shown + " of " + total + " member" + (total != 1 ? "s" : ""));
    }

    // ── Reset ──────────────────────────────────────────────────────────────

    /**
     * Clears all filter controls and restores the full member list.
     * Temporarily removes the DocumentListener to avoid triple-firing applyFilters.
     */
    private void resetFilters() {
        memberSearchField.getDocument().removeDocumentListener(filterDocListener);
        memberSearchField.setText("");
        memberSearchField.getDocument().addDocumentListener(filterDocListener);

        balanceStatusCombo.setSelectedIndex(0);
        sortCombo.setSelectedIndex(0);
        settlementCombo.setSelectedIndex(0);
        // applyFilters() is triggered by the combo setSelectedIndex calls above;
        // the last one will render the full list.
    }

    // ── Shared DocumentListener ────────────────────────────────────────────

    /**
     * Named so resetFilters() can detach/reattach it safely.
     */
    private final javax.swing.event.DocumentListener filterDocListener =
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilters(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilters(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            };
}
