package utils;

/**
 * Utility class for string handling operations.
 * 
 * This class demonstrates various String operations from the Java library:
 * - String constructors
 * - String length()
 * - Special String operations
 * - Character extraction (charAt, getChars)
 * - String comparison (equals, compareTo)
 * - Searching (contains, indexOf, startsWith)
 * - Modifying strings (replace, trim, toUpperCase, toLowerCase)
 * - String.valueOf()
 * - StringBuffer vs String comparison
 * 
 * Syllabus Topics Covered:
 * - String handling
 * - String operations
 * - String vs StringBuffer
 * - Character extraction
 * - String comparison
 * - String searching
 * - String modification
 * - String.valueOf()
 * 
 * @see java.lang.String
 * @see java.lang.StringBuffer
 */
public final class StringUtils {
    
    // Private constructor to prevent instantiation
    private StringUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates and normalizes a username.
     * 
     * Demonstrates: trim(), toLowerCase(), length(), isEmpty()
     * 
     * @param username the username to validate
     * @return normalized username or null if invalid
     */
    public static String normalizeUsername(String username) {
        if (username == null) {
            return null;
        }
        String normalized = username.trim().toLowerCase();
        return normalized.isEmpty() ? null : normalized;
    }
    
    /**
     * Validates a roommate name.
     * 
     * Demonstrates: trim(), length(), isEmpty(), matches()
     * 
     * @param name the name to validate
     * @return true if valid
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 100 && trimmed.matches("[\\p{L}\\p{N} .'-]+");
    }
    
    /**
     * Validates an expense description.
     * 
     * Demonstrates: trim(), length(), isEmpty()
     * 
     * @param description the description to validate
     * @return true if valid
     */
    public static boolean isValidDescription(String description) {
        if (description == null) {
            return false;
        }
        String trimmed = description.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 255;
    }
    
    /**
     * Validates a UPI ID format.
     * 
     * Demonstrates: contains(), indexOf(), endsWith()
     * 
     * @param upiId the UPI ID to validate
     * @return true if valid format
     */
    public static boolean isValidUpiId(String upiId) {
        if (upiId == null || upiId.trim().isEmpty()) {
            return true; // UPI ID is optional
        }
        String trimmed = upiId.trim();
        // Basic UPI format: user@bank or phone@bank
        return trimmed.contains("@") && trimmed.indexOf("@") > 0 
               && trimmed.indexOf("@") < trimmed.length() - 1;
    }
    
    /**
     * Validates a phone number.
     * 
     * Demonstrates: matches(), length()
     * 
     * @param phone the phone number to validate
     * @return true if valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        String trimmed = phone.trim();
        return trimmed.matches("[0-9+\\-\\s()]{10,20}");
    }
    
    /**
     * Normalizes search input for expense/roommate search.
     * 
     * Demonstrates: trim(), toLowerCase(), replace()
     * 
     * @param input the search input
     * @return normalized search term
     */
    public static String normalizeSearchInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase().replaceAll("\\s+", " ");
    }
    
    /**
     * Checks if a string contains a search term (case-insensitive).
     * 
     * Demonstrates: toLowerCase(), contains(), indexOf()
     * 
     * @param text the text to search in
     * @param searchTerm the term to search for
     * @return true if found
     */
    public static boolean containsIgnoreCase(String text, String searchTerm) {
        if (text == null || searchTerm == null) {
            return false;
        }
        return text.toLowerCase().contains(searchTerm.toLowerCase());
    }
    
    /**
     * Formats an expense description for display.
     * 
     * Demonstrates: String.valueOf(), trim(), length()
     * 
     * @param description the description
     * @param maxLength maximum length
     * @return formatted description
     */
    public static String formatDescription(String description, int maxLength) {
        if (description == null) {
            return "";
        }
        String trimmed = description.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Formats a currency amount for display.
     * 
     * Demonstrates: String.valueOf(), String.format()
     * 
     * @param amount the amount
     * @return formatted currency string
     */
    public static String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) {
            return "₹0.00";
        }
        return String.format("₹%.2f", amount);
    }
    
    /**
     * Compares two strings naturally (case-insensitive).
     * 
     * Demonstrates: compareTo(), compareToIgnoreCase()
     * 
     * @param s1 first string
     * @param s2 second string
     * @return comparison result
     */
    public static int compareNatural(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareToIgnoreCase(s2);
    }
    
    /**
     * Extracts initials from a name.
     * 
     * Demonstrates: charAt(), split(), toUpperCase()
     * 
     * @param name the full name
     * @return initials
     */
    public static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return initials.toString();
    }
    
    /**
     * Demonstrates String vs StringBuffer for report building.
     * 
     * String is immutable - each concatenation creates a new object.
     * StringBuffer is mutable - efficient for repeated modifications.
     * 
     * Syllabus Topics:
     * - String immutability
     * - StringBuffer mutability
     * - Performance comparison
     * 
     * @param parts string parts to join
     * @return joined string
     */
    public static String buildReportWithStringBuffer(String... parts) {
        // StringBuffer is thread-safe and mutable - efficient for building
        StringBuffer buffer = new StringBuffer();
        for (String part : parts) {
            if (part != null) {
                buffer.append(part);
            }
        }
        return buffer.toString();
    }
    
    /**
     * Demonstrates StringBuilder (non-thread-safe, faster) vs StringBuffer.
     * 
     * @param parts string parts to join
     * @return joined string
     */
    public static String buildReportWithStringBuilder(String... parts) {
        // StringBuilder is non-thread-safe but faster - preferred for single-threaded use
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part != null) {
                builder.append(part);
            }
        }
        return builder.toString();
    }
    
    /**
     * Extracts characters from a string using getChars().
     * 
     * Demonstrates: getChars(), charAt()
     * 
     * @param source the source string
     * @param start start index
     * @param end end index
     * @return character array
     */
    public static char[] extractCharacters(String source, int start, int end) {
        if (source == null || start < 0 || end > source.length() || start >= end) {
            return new char[0];
        }
        char[] destination = new char[end - start];
        source.getChars(start, end, destination, 0);
        return destination;
    }
    
    /**
     * Checks if a string starts with a prefix (case-insensitive).
     * 
     * Demonstrates: startsWith(), toLowerCase()
     * 
     * @param string the string
     * @param prefix the prefix
     * @return true if starts with prefix
     */
    public static boolean startsWithIgnoreCase(String string, String prefix) {
        if (string == null || prefix == null) {
            return false;
        }
        return string.toLowerCase().startsWith(prefix.toLowerCase());
    }
    
    /**
     * Replaces multiple whitespace characters with single space.
     * 
     * Demonstrates: replaceAll(), trim()
     * 
     * @param input the input string
     * @return normalized string
     */
    public static String normalizeWhitespace(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }
}