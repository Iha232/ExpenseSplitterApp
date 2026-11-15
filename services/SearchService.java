package services;

import model.Expense;
import model.Roommate;
import utils.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for searching expenses and roommates.
 * 
 * This class demonstrates:
 * - String handling and searching
 * - Collections Framework (List, ArrayList, Iterator)
 * - Stream API for filtering
 * - String comparison and matching
 * - Case-insensitive search
 * 
 * Syllabus Topics Covered:
 * - String handling (contains, indexOf, startsWith, equals, compareTo)
 * - Collections Framework (List, ArrayList)
 * - Iterator interface
 * - String comparison (equals, compareTo, equalsIgnoreCase)
 * - String searching (contains, indexOf, startsWith)
 * - Case-insensitive operations
 * - Stream API for collection processing
 * 
 * @see utils.StringUtils
 * @see java.util.List
 * @see java.util.ArrayList
 * @see java.util.Iterator
 */
public class SearchService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Searches expenses by description.
     * 
     * Demonstrates: String.contains(), case-insensitive search, Iterator
     * 
     * @param expenses list of expenses to search
     * @param query search query
     * @return filtered list of expenses
     */
    public List<Expense> searchByDescription(List<Expense> expenses, String query) {
        if (expenses == null || expenses.isEmpty() || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = StringUtils.normalizeSearchInput(query);
        
        // Using Iterator to demonstrate Iterator interface
        List<Expense> results = new ArrayList<>();
        Iterator<Expense> iterator = expenses.iterator();
        while (iterator.hasNext()) {
            Expense expense = iterator.next();
            if (expense.getDescription() != null && 
                StringUtils.containsIgnoreCase(expense.getDescription(), normalizedQuery)) {
                results.add(expense);
            }
        }
        return results;
    }
    
    /**
     * Searches expenses by payer name.
     * 
     * Demonstrates: String.equals(), compareTo(), case-insensitive comparison
     * 
     * @param expenses list of expenses to search
     * @param payerName payer name to search for
     * @return filtered list of expenses
     */
    public List<Expense> searchByPayer(List<Expense> expenses, String payerName) {
        if (expenses == null || expenses.isEmpty() || payerName == null || payerName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedPayer = payerName.trim().toLowerCase();
        
        // Using Stream API for filtering
        return expenses.stream()
            .filter(e -> e.getPayerName() != null && 
                    e.getPayerName().toLowerCase().contains(normalizedPayer))
            .collect(Collectors.toList());
    }
    
    /**
     * Searches expenses by date range.
     * 
     * Demonstrates: Date parsing, comparison
     * 
     * @param expenses list of expenses to search
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return filtered list of expenses
     */
    public List<Expense> searchByDateRange(List<Expense> expenses, String startDate, String endDate) {
        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }
        
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);
        
        if (start == null && end == null) {
            return new ArrayList<>();
        }
        
        return expenses.stream()
            .filter(e -> {
                LocalDate expenseDate = e.getExpenseDate();
                if (expenseDate == null) return false;
                if (start != null && expenseDate.isBefore(start)) return false;
                if (end != null && expenseDate.isAfter(end)) return false;
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Searches expenses by exact date.
     * 
     * @param expenses list of expenses
     * @param dateStr date string (yyyy-MM-dd)
     * @return filtered list
     */
    public List<Expense> searchByDate(List<Expense> expenses, String dateStr) {
        LocalDate date = parseDate(dateStr);
        if (date == null) {
            return new ArrayList<>();
        }
        
        return expenses.stream()
            .filter(e -> date.equals(e.getExpenseDate()))
            .collect(Collectors.toList());
    }
    
    /**
     * Searches expenses by amount range.
     * 
     * Demonstrates: BigDecimal comparison
     * 
     * @param expenses list of expenses
     * @param minAmount minimum amount
     * @param maxAmount maximum amount
     * @return filtered list
     */
    public List<Expense> searchByAmountRange(List<Expense> expenses, String minAmount, String maxAmount) {
        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }
        
        BigDecimal min = parseAmount(minAmount);
        BigDecimal max = parseAmount(maxAmount);
        
        if (min == null && max == null) {
            return new ArrayList<>();
        }
        
        return expenses.stream()
            .filter(e -> {
                BigDecimal amount = e.getTotalAmount();
                if (amount == null) return false;
                if (min != null && amount.compareTo(min) < 0) return false;
                if (max != null && amount.compareTo(max) > 0) return false;
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Searches expenses by exact amount.
     * 
     * @param expenses list of expenses
     * @param amountStr amount string
     * @return filtered list
     */
    public List<Expense> searchByAmount(List<Expense> expenses, String amountStr) {
        BigDecimal amount = parseAmount(amountStr);
        if (amount == null) {
            return new ArrayList<>();
        }
        
        return expenses.stream()
            .filter(e -> amount.compareTo(e.getTotalAmount()) == 0)
            .collect(Collectors.toList());
    }
    
    /**
     * Searches roommates by name.
     * 
     * Demonstrates: String.contains(), case-insensitive search
     * 
     * @param roommates list of roommates
     * @param query search query
     * @return filtered list
     */
    public List<Roommate> searchRoommatesByName(List<Roommate> roommates, String query) {
        if (roommates == null || roommates.isEmpty() || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = StringUtils.normalizeSearchInput(query);
        
        return roommates.stream()
            .filter(r -> r.getName() != null && 
                    StringUtils.containsIgnoreCase(r.getName(), normalizedQuery))
            .collect(Collectors.toList());
    }
    
    /**
     * Searches roommates by phone or UPI ID.
     * 
     * @param roommates list of roommates
     * @param query search query
     * @return filtered list
     */
    public List<Roommate> searchRoommatesByContact(List<Roommate> roommates, String query) {
        if (roommates == null || roommates.isEmpty() || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = query.trim().toLowerCase();
        
        return roommates.stream()
            .filter(r -> (r.getPhone() != null && r.getPhone().toLowerCase().contains(normalizedQuery)) ||
                    (r.getUpiId() != null && r.getUpiId().toLowerCase().contains(normalizedQuery)))
            .collect(Collectors.toList());
    }
    
    /**
     * Searches expenses by multiple criteria (advanced search).
     * 
     * Demonstrates: Multiple criteria filtering, combining conditions
     * 
     * @param expenses list of expenses
     * @param description description query
     * @param payerName payer name query
     * @param startDate start date
     * @param endDate end date
     * @param minAmount minimum amount
     * @param maxAmount maximum amount
     * @return filtered list
     */
    public List<Expense> advancedSearch(List<Expense> expenses, String description, String payerName,
                                        String startDate, String endDate, String minAmount, String maxAmount) {
        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }
        
        String normDesc = description != null ? description.trim().toLowerCase() : null;
        String normPayer = payerName != null ? payerName.trim().toLowerCase() : null;
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);
        BigDecimal minAmt = parseAmount(minAmount);
        BigDecimal maxAmt = parseAmount(maxAmount);
        
        return expenses.stream()
            .filter(e -> {
                // Description filter
                if (normDesc != null && !normDesc.isEmpty()) {
                    if (e.getDescription() == null || 
                        !e.getDescription().toLowerCase().contains(normDesc)) {
                        return false;
                    }
                }
                
                // Payer filter
                if (normPayer != null && !normPayer.isEmpty()) {
                    if (e.getPayerName() == null || 
                        !e.getPayerName().toLowerCase().contains(normPayer)) {
                        return false;
                    }
                }
                
                // Date range filter
                LocalDate expenseDate = e.getExpenseDate();
                if (expenseDate != null) {
                    if (start != null && expenseDate.isBefore(start)) return false;
                    if (end != null && expenseDate.isAfter(end)) return false;
                }
                
                // Amount range filter
                BigDecimal amount = e.getTotalAmount();
                if (amount != null) {
                    if (minAmt != null && amount.compareTo(minAmt) < 0) return false;
                    if (maxAmt != null && amount.compareTo(maxAmt) > 0) return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Parses a date string in various formats.
     * 
     * @param dateStr date string
     * @return LocalDate or null
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = dateStr.trim();
        // Try multiple formats
        String[] formats = {"yyyy-MM-dd", "dd MMM yyyy", "dd/MM/yyyy", "MM/dd/yyyy"};
        
        for (String format : formats) {
            try {
                return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {
            }
        }
        return null;
    }
    
    /**
     * Parses an amount string to BigDecimal.
     * 
     * @param amountStr amount string
     * @return BigDecimal or null
     */
    private BigDecimal parseAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(amountStr.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Searches using Iterator explicitly (demonstrates Iterator interface).
     * 
     * @param expenses list of expenses
     * @param predicate filter predicate
     * @return filtered list
     */
    public List<Expense> searchWithIterator(List<Expense> expenses, java.util.function.Predicate<Expense> predicate) {
        if (expenses == null) {
            return new ArrayList<>();
        }
        
        List<Expense> results = new ArrayList<>();
        Iterator<Expense> iterator = expenses.iterator();
        while (iterator.hasNext()) {
            Expense expense = iterator.next();
            if (predicate.test(expense)) {
                results.add(expense);
            }
        }
        return results;
    }
    
    /**
     * Gets unique payers from expenses.
     * 
     * Demonstrates: Set operations, distinct
     * 
     * @param expenses list of expenses
     * @return list of unique payer names
     */
    public List<String> getUniquePayers(List<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }
        
        return expenses.stream()
            .map(Expense::getPayerName)
            .filter(Objects::nonNull)
            .distinct()
            .sorted(StringUtils::compareNatural)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets expense count by payer.
     * 
     * Demonstrates: Map, groupingBy
     * 
     * @param expenses list of expenses
     * @return map of payer name to count
     */
    public Map<String, Long> getExpenseCountByPayer(List<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return new HashMap<>();
        }
        
        return expenses.stream()
            .filter(e -> e.getPayerName() != null)
            .collect(Collectors.groupingBy(Expense::getPayerName, Collectors.counting()));
    }
    
    /**
     * Gets total amount by payer.
     * 
     * Demonstrates: BigDecimal sum, grouping
     * 
     * @param expenses list of expenses
     * @return map of payer name to total amount
     */
    public Map<String, BigDecimal> getTotalAmountByPayer(List<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return new HashMap<>();
        }
        
        return expenses.stream()
            .filter(e -> e.getPayerName() != null && e.getTotalAmount() != null)
            .collect(Collectors.groupingBy(
                Expense::getPayerName,
                Collectors.reducing(BigDecimal.ZERO, Expense::getTotalAmount, BigDecimal::add)
            ));
    }
}