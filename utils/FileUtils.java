package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for file operations.
 * 
 * This class demonstrates file handling concepts:
 * - Creating directories
 * - Writing files
 * - Reading files
 * - Checking file existence
 * - Handling file permissions and I/O errors
 * - Path operations
 * 
 * Syllabus Topics Covered:
 * - File class
 * - File operations
 * - Directory creation
 * - File existence checking
 * - File permissions
 * - I/O error handling
 * - Path operations (java.nio.file)
 * 
 * @see java.io.File
 * @see java.nio.file.Files
 * @see java.nio.file.Path
 */
public final class FileUtils {
    
    private static final String EXPORTS_DIR = "exports";
    private static final String BACKUPS_DIR = "backups";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    // Private constructor to prevent instantiation
    private FileUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Ensures the exports directory exists.
     * 
     * Demonstrates: File.mkdirs(), Files.createDirectories()
     * 
     * @return the exports directory File
     * @throws IOException if directory cannot be created
     */
    public static File ensureExportsDirectory() throws IOException {
        File dir = new File(EXPORTS_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create exports directory: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }
    
    /**
     * Ensures the backups directory exists.
     * 
     * @return the backups directory File
     * @throws IOException if directory cannot be created
     */
    public static File ensureBackupsDirectory() throws IOException {
        File dir = new File(BACKUPS_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create backups directory: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }
    
    /**
     * Generates a timestamped filename.
     * 
     * @param prefix the filename prefix
     * @param extension the file extension
     * @return timestamped filename
     */
    public static String generateTimestampedFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return prefix + "_" + timestamp + "." + extension;
    }
    
    /**
     * Creates an export file in the exports directory.
     * 
     * Demonstrates: File creation, path resolution
     * 
     * @param filename the filename
     * @return the File object
     * @throws IOException if directory cannot be created
     */
    public static File createExportFile(String filename) throws IOException {
        File dir = ensureExportsDirectory();
        return new File(dir, filename);
    }
    
    /**
     * Creates a backup file in the backups directory.
     * 
     * @param filename the filename
     * @return the File object
     * @throws IOException if directory cannot be created
     */
    public static File createBackupFile(String filename) throws IOException {
        File dir = ensureBackupsDirectory();
        return new File(dir, filename);
    }
    
    /**
     * Checks if a file exists and is readable.
     * 
     * Demonstrates: File.exists(), File.canRead()
     * 
     * @param file the file to check
     * @return true if file exists and is readable
     */
    public static boolean isReadableFile(File file) {
        return file != null && file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * Checks if a file exists and is writable.
     * 
     * @param file the file to check
     * @return true if file exists and is writable
     */
    public static boolean isWritableFile(File file) {
        return file != null && file.exists() && file.isFile() && file.canWrite();
    }
    
    /**
     * Gets the file size in human-readable format.
     * 
     * @param file the file
     * @return formatted file size
     */
    public static String getFileSizeString(File file) {
        if (file == null || !file.exists()) {
            return "0 B";
        }
        long bytes = file.length();
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * Reads all lines from a file.
     * 
     * Demonstrates: Files.readAllLines(), try-with-resources
     * 
     * @param file the file to read
     * @return list of lines
     * @throws IOException if reading fails
     */
    public static java.util.List<String> readAllLines(File file) throws IOException {
        if (!isReadableFile(file)) {
            throw new FileNotFoundException("File not found or not readable: " + file.getAbsolutePath());
        }
        return Files.readAllLines(file.toPath());
    }
    
    /**
     * Writes lines to a file.
     * 
     * Demonstrates: Files.write(), StandardOpenOption, try-with-resources
     * 
     * @param file the file to write
     * @param lines the lines to write
     * @throws IOException if writing fails
     */
    public static void writeLines(File file, java.util.List<String> lines) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create parent directories: " + parent.getAbsolutePath());
            }
        }
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8, 
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    /**
     * Appends a line to a file.
     * 
     * Demonstrates: Files.write(), StandardOpenOption.APPEND
     * 
     * @param file the file to append to
     * @param line the line to append
     * @throws IOException if writing fails
     */
    public static void appendLine(File file, String line) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create parent directories: " + parent.getAbsolutePath());
            }
        }
        Files.write(file.toPath(), (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                     StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    /**
     * Copies a file.
     * 
     * Demonstrates: Files.copy(), StandardCopyOption
     * 
     * @param source source file
     * @param target target file
     * @throws IOException if copy fails
     */
    public static void copyFile(File source, File target) throws IOException {
        if (!isReadableFile(source)) {
            throw new FileNotFoundException("Source file not found: " + source.getAbsolutePath());
        }
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create parent directories: " + parent.getAbsolutePath());
            }
        }
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Deletes a file if it exists.
     * 
     * @param file the file to delete
     * @return true if deleted
     */
    public static boolean deleteIfExists(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * Lists all files in a directory with a specific extension.
     * 
     * Demonstrates: File.listFiles(), FilenameFilter
     * 
     * @param directory the directory
     * @param extension the file extension (without dot)
     * @return list of files
     */
    public static File[] listFilesByExtension(File directory, String extension) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return new File[0];
        }
        return directory.listFiles((dir, name) -> name.toLowerCase().endsWith("." + extension.toLowerCase()));
    }
    
    /**
     * Gets the most recent backup file.
     * 
     * @return the most recent backup file or null
     */
    public static File getMostRecentBackup() {
        try {
            File dir = ensureBackupsDirectory();
            File[] backups = listFilesByExtension(dir, "ser");
            if (backups == null || backups.length == 0) {
                return null;
            }
            // Sort by last modified (most recent first)
            java.util.Arrays.sort(backups, (f1, f2) -> 
                Long.compare(f2.lastModified(), f1.lastModified()));
            return backups[0];
        } catch (IOException e) {
            return null;
        }
    }
}