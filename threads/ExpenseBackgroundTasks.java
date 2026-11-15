package threads;

import controller.ExpenseController;
import model.Balance;
import model.Expense;
import model.Roommate;
import model.User;
import services.BackupService;
import services.ExportService;
import services.SearchService;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Concrete background tasks for expense application operations.
 * 
 * This class demonstrates practical SwingWorker implementations
 * for the ExpenseSplitterApp application.
 * 
 * Syllabus Topics:
 * - SwingWorker implementations
 * - Background database operations
 * - GUI updates from background threads
 * - Progress reporting
 * - Error handling
 * 
 * @see threads.BackgroundTask
 * @see javax.swing.SwingWorker
 */
public class ExpenseBackgroundTasks {
    
    private final ExpenseController controller;
    private final ExportService exportService;
    private final BackupService backupService;
    private final SearchService searchService;
    
    public ExpenseBackgroundTasks(ExpenseController controller) {
        this.controller = controller;
        this.exportService = new ExportService();
        this.backupService = new BackupService();
        this.searchService = new SearchService();
    }
    
    /**
     * Loads balances in background.
     */
    public BackgroundTask<List<model.Balance>, Void> createLoadBalancesTask() {
        return new BackgroundTask<List<model.Balance>, Void>("LoadBalances") {
            @Override
            protected List<model.Balance> doInBackground() throws Exception {
                return controller.getBalances();
            }
            
            @Override
            protected void onSuccess(List<model.Balance> result) {
                // UI update handled by callback
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Loads ledger (expenses) in background.
     */
    public BackgroundTask<List<Expense>, Void> createLoadLedgerTask() {
        return new BackgroundTask<List<Expense>, Void>("LoadLedger") {
            @Override
            protected List<Expense> doInBackground() throws Exception {
                return controller.getAllExpenses();
            }
            
            @Override
            protected void onSuccess(List<Expense> result) {
                // UI update handled by callback
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Loads roommates in background.
     */
    public BackgroundTask<List<Roommate>, Void> createLoadRoommatesTask() {
        return new BackgroundTask<List<Roommate>, Void>("LoadRoommates") {
            @Override
            protected List<Roommate> doInBackground() throws Exception {
                return controller.getAllRoommates();
            }
            
            @Override
            protected void onSuccess(List<Roommate> result) {
                // UI update handled by callback
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Adds expense in background.
     */
    public BackgroundTask<Integer, Void> createAddExpenseTask(String description, 
                                                               BigDecimal amount,
                                                               int payerId,
                                                               List<Integer> participantIds,
                                                               java.time.LocalDate date) {
        return new BackgroundTask<Integer, Void>("AddExpense") {
            @Override
            protected Integer doInBackground() throws Exception {
                return controller.addExpense(description, amount, payerId, participantIds, date);
            }
            
            @Override
            protected void onSuccess(Integer result) {
                // Expense added successfully
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Exports expense report in background.
     */
    public BackgroundTask<File, Integer> createExportReportTask() {
        return new BackgroundTask<File, Integer>("ExportReport") {
            private ExportService exportService = new ExportService();
            
            @Override
            protected File doInBackground() throws Exception {
                // Simulate progress updates
                publishProgress(10);
                Thread.sleep(100);
                
                // Get data
                User currentUser = new model.User(); // Would get from controller
                List<Roommate> roommates = controller.getAllRoommates();
                publishProgress(30);
                Thread.sleep(100);
                
                List<Expense> expenses = controller.getAllExpenses();
                publishProgress(60);
                Thread.sleep(100);
                
                List<model.Balance> balances = controller.getBalances();
                publishProgress(80);
                Thread.sleep(100);
                
                // Export
                File result = exportService.exportExpenseReport(null, roommates, expenses, 
                    controller.getBalances());
                
                publishProgress(100);
                return result;
            }
            
            @Override
            protected void onSuccess(File result) {
                // Show success message with file path
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Creates backup in background.
     */
    public BackgroundTask<File, Integer> createBackupTask() {
        return new BackgroundTask<File, Integer>("CreateBackup") {
            private BackupService backupService = new BackupService();
            
            @Override
            protected File doInBackground() throws Exception {
                publishProgress(20);
                Thread.sleep(100);
                
                List<Roommate> roommates = controller.getAllRoommates();
                publishProgress(40);
                Thread.sleep(100);
                
                List<Expense> expenses = controller.getAllExpenses();
                publishProgress(60);
                Thread.sleep(100);
                
                // Get splits for all expenses
                List<model.ExpenseSplit> allSplits = new java.util.ArrayList<>();
                for (Expense e : expenses) {
                    allSplits.addAll(controller.getSplitsForExpense(e.getExpenseId()));
                }
                publishProgress(80);
                Thread.sleep(100);
                
                List<model.Balance> balances = controller.getBalances();
                
                File result = backupService.createBackup(roommates, expenses, allSplits, balances);
                publishProgress(100);
                return result;
            }
            
            @Override
            protected void onSuccess(File result) {
                // Backup created successfully
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Restores backup in background.
     */
    public BackgroundTask<BackupService.BackupData, Integer> createRestoreBackupTask(File backupFile) {
        return new BackgroundTask<BackupService.BackupData, Integer>("RestoreBackup") {
            private BackupService backupService = new BackupService();
            
            @Override
            protected BackupService.BackupData doInBackground() throws Exception {
                publishProgress(30);
                Thread.sleep(100);
                
                BackupService.BackupData backupData = backupService.restoreBackup(backupFile);
                
                publishProgress(60);
                Thread.sleep(100);
                
                // Restore roommates
                for (model.Roommate r : backupData.getRoommates()) {
                    controller.addRoommate(r);
                }
                
                publishProgress(80);
                Thread.sleep(100);
                
                // Note: Expenses and splits would need special handling due to foreign keys
                // This is a simplified version
                
                publishProgress(100);
                return backupData;
            }
            
            @Override
            protected void onSuccess(BackupService.BackupData result) {
                // Restore completed
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Searches expenses in background.
     */
    public BackgroundTask<List<Expense>, Void> createSearchExpensesTask(String query) {
        return new BackgroundTask<List<Expense>, Void>("SearchExpenses") {
            private SearchService searchService = new SearchService();
            
            @Override
            protected List<Expense> doInBackground() throws Exception {
                List<Expense> allExpenses = controller.getAllExpenses();
                return searchService.searchByDescription(allExpenses, query);
            }
            
            @Override
            protected void onSuccess(List<Expense> result) {
                // Search results ready
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Settles member dues in background.
     */
    public BackgroundTask<Boolean, Void> createSettleDuesTask(int memberId) {
        return new BackgroundTask<Boolean, Void>("SettleDues") {
            @Override
            protected Boolean doInBackground() throws Exception {
                return controller.settleMemberDues(memberId);
            }
            
            @Override
            protected void onSuccess(Boolean result) {
                // Settlement recorded
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Deletes expense in background.
     */
    public BackgroundTask<Boolean, Void> createDeleteExpenseTask(int expenseId) {
        return new BackgroundTask<Boolean, Void>("DeleteExpense") {
            @Override
            protected Boolean doInBackground() throws Exception {
                return controller.deleteExpense(expenseId);
            }
            
            @Override
            protected void onSuccess(Boolean result) {
                // Expense deleted
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
    
    /**
     * Refreshes all data in background.
     */
    public BackgroundTask<Void, Integer> createRefreshAllTask() {
        return new BackgroundTask<Void, Integer>("RefreshAll") {
            @Override
            protected Void doInBackground() throws Exception {
                publishProgress(20);
                controller.getAllRoommates();
                
                publishProgress(40);
                controller.getAllExpenses();
                
                publishProgress(60);
                controller.getBalances();
                
                publishProgress(80);
                Thread.sleep(200);
                
                publishProgress(100);
                return null;
            }
            
            @Override
            protected void onSuccess(Void result) {
                // All data refreshed
            }
            
            @Override
            protected void onError(Throwable error) {
                // Error handling
            }
        };
    }
}