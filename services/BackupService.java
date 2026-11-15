package services;

import model.Balance;
import model.Expense;
import model.ExpenseSplit;
import model.Roommate;
import utils.FileUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for backing up and restoring application data.
 * 
 * This class demonstrates:
 * - ObjectOutputStream for serialization
 * - ObjectInputStream for deserialization
 * - Serializable interface implementation
 * - Object streams for data persistence
 * - File I/O with object streams
 * - Backup and restore functionality
 * 
 * Syllabus Topics Covered:
 * - ObjectOutputStream
 * - ObjectInputStream
 * - Serializable interface
 * - Object serialization
 * - Object deserialization
 * - File I/O with object streams
 * - Exception handling
 * - Resource management (try-with-resources)
 * - Version compatibility (serialVersionUID)
 * 
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 * @see java.io.Serializable
 * @see utils.FileUtils
 */
public class BackupService {
    
    /**
     * Data model for backup serialization.
     * 
     * This class implements Serializable to enable object serialization.
     * 
     * Syllabus Topics:
     * - Serializable interface
     * - serialVersionUID for version control
     * - Transient fields (not serialized)
     * - Object graph serialization
     */
    public static class BackupData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private final LocalDateTime backupDate;
        private final String version;
        private final List<Roommate> roommates;
        private final List<Expense> expenses;
        private final List<ExpenseSplit> expenseSplits;
        private final List<model.Balance> balances;
        
        // Transient fields are not serialized
        private transient String checksum;
        
        public BackupData(List<Roommate> roommates, List<Expense> expenses,
                         List<ExpenseSplit> expenseSplits, List<model.Balance> balances) {
            this.backupDate = LocalDateTime.now();
            this.version = "1.0";
            this.roommates = roommates;
            this.expenses = expenses;
            this.expenseSplits = expenseSplits;
            this.balances = balances;
            this.checksum = calculateChecksum();
        }
        
        private String calculateChecksum() {
            // Simple checksum for data integrity verification
            int hash = 17;
            hash = 31 * hash + (roommates != null ? roommates.size() : 0);
            hash = 31 * hash + (expenses != null ? expenses.size() : 0);
            hash = 31 * hash + (expenseSplits != null ? expenseSplits.size() : 0);
            hash = 31 * hash + (balances != null ? balances.size() : 0);
            return Integer.toHexString(hash);
        }
        
        // Getters
        public LocalDateTime getBackupDate() { return backupDate; }
        public String getVersion() { return version; }
        public List<Roommate> getRoommates() { return roommates; }
        public List<Expense> getExpenses() { return expenses; }
        public List<ExpenseSplit> getExpenseSplits() { return expenseSplits; }
        public List<model.Balance> getBalances() { return balances; }
        public String getChecksum() { return checksum; }
        
        public void setChecksum(String checksum) { this.checksum = checksum; }
        
        public boolean verifyChecksum() {
            return checksum != null && checksum.equals(calculateChecksum());
        }
        
        @Override
        public String toString() {
            return String.format("BackupData{date=%s, version=%s, roommates=%d, expenses=%d, splits=%d, balances=%d}",
                backupDate, version, 
                roommates != null ? roommates.size() : 0,
                expenses != null ? expenses.size() : 0,
                expenseSplits != null ? expenseSplits.size() : 0,
                balances != null ? balances.size() : 0);
        }
    }
    
    /**
     * Creates a backup of all application data.
     * 
     * This method demonstrates:
     * - ObjectOutputStream for serialization
     * - try-with-resources for resource management
     * - File I/O with object streams
     * - Exception handling
     * 
     * Syllabus Topics:
     * - ObjectOutputStream
     * - Serialization
     * - try-with-resources
     * - File I/O
     * - IOException handling
     * 
     * @param roommates list of roommates
     * @param expenses list of expenses
     * @param expenseSplits list of expense splits
     * @param balances list of balances
     * @return the backup file
     * @throws IOException if backup fails
     */
    public File createBackup(List<Roommate> roommates, List<Expense> expenses,
                            List<ExpenseSplit> expenseSplits, List<model.Balance> balances) throws IOException {
        
        File backupFile = FileUtils.createBackupFile(
            utils.FileUtils.generateTimestampedFilename("backup", "ser"));
        
        BackupData backupData = new BackupData(roommates, expenses, expenseSplits, balances);
        
        // Serialize using ObjectOutputStream
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(backupFile)))) {
            
            oos.writeObject(backupData);
            oos.flush();
        }
        
        return backupFile;
    }
    
    /**
     * Restores data from a backup file.
     * 
     * This method demonstrates:
     * - ObjectInputStream for deserialization
     * - try-with-resources for resource management
     * - Type casting after deserialization
     * - Data validation after restore
     * - Exception handling for corrupted files
     * 
     * Syllabus Topics:
     * - ObjectInputStream
     * - Deserialization
     * - Type casting
     * - Data validation
     * - Exception handling
     * - ClassNotFoundException handling
     * 
     * @param backupFile the backup file to restore from
     * @return the restored backup data
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if class definition not found
     * @throws IllegalArgumentException if backup data is invalid
     */
    public BackupData restoreBackup(File backupFile) throws IOException, ClassNotFoundException {
        if (!utils.FileUtils.isReadableFile(backupFile)) {
            throw new FileNotFoundException("Backup file not found or not readable: " + backupFile.getAbsolutePath());
        }
        
        // Deserialize using ObjectInputStream
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(backupFile)))) {
            
            Object obj = ois.readObject();
            
            if (!(obj instanceof BackupData)) {
                throw new IllegalArgumentException("Invalid backup file format");
            }
            
            BackupData backupData = (BackupData) obj;
            
            // Validate restored data
            validateBackupData(backupData);
            
            return backupData;
        }
    }
    
    /**
     * Validates restored backup data.
     * 
     * @param backupData the backup data to validate
     * @throws IllegalArgumentException if data is invalid
     */
    private void validateBackupData(BackupData backupData) {
        if (backupData.getRoommates() == null || backupData.getRoommates().isEmpty()) {
            throw new IllegalArgumentException("Backup contains no roommates");
        }
        
        if (backupData.getExpenses() == null) {
            throw new IllegalArgumentException("Backup contains no expenses list");
        }
        
        if (backupData.getExpenseSplits() == null) {
            throw new IllegalArgumentException("Backup contains no expense splits list");
        }
        
        if (backupData.getBalances() == null) {
            throw new IllegalArgumentException("Backup contains no balances list");
        }
        
        // Verify checksum if available
        if (backupData.getChecksum() != null && !backupData.verifyChecksum()) {
            throw new IllegalArgumentException("Backup data integrity check failed - possible corruption");
        }
    }
    
    /**
     * Lists all available backup files.
     * 
     * @return array of backup files (most recent first)
     */
    public File[] listBackups() {
        try {
            File dir = utils.FileUtils.ensureBackupsDirectory();
            File[] backups = utils.FileUtils.listFilesByExtension(dir, "ser");
            if (backups != null && backups.length > 0) {
                // Sort by last modified (most recent first)
                java.util.Arrays.sort(backups, (f1, f2) -> 
                    Long.compare(f2.lastModified(), f1.lastModified()));
                return backups;
            }
        } catch (IOException e) {
            // Ignore
        }
        return new File[0];
    }
    
    /**
     * Gets the most recent backup.
     * 
     * @return most recent backup file or null
     */
    public File getMostRecentBackup() {
        return utils.FileUtils.getMostRecentBackup();
    }
    
    /**
     * Deletes a backup file.
     * 
     * @param backupFile the backup file to delete
     * @return true if deleted
     */
    public boolean deleteBackup(File backupFile) {
        return utils.FileUtils.deleteIfExists(backupFile);
    }
    
    /**
     * Gets backup file information without restoring.
     * 
     * @param backupFile the backup file
     * @return string representation of backup info
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if class not found
     */
    public String getBackupInfo(File backupFile) throws IOException, ClassNotFoundException {
        BackupData data = restoreBackup(backupFile);
        return data.toString() + " | Checksum: " + data.getChecksum() + " | Valid: " + data.verifyChecksum();
    }
}