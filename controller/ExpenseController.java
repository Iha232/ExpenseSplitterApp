package controller;

import dao.DatabaseManager;
import dao.ExpenseDAO;
import dao.RoommateDAO;
import model.Balance;
import model.Expense;
import model.ExpenseSplit;
import model.Roommate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseController {
    private ExpenseDAO expenseDAO;
    private RoommateDAO roommateDAO;

    public ExpenseController() {
        this.expenseDAO = new ExpenseDAO();
        this.roommateDAO = new RoommateDAO();
    }

    public List<Roommate> getAllRoommates() throws Exception {
        return roommateDAO.getAllRoommates();
    }

    public Roommate getRoommateById(int memberId) throws Exception {
        return roommateDAO.getRoommateById(memberId);
    }

    public int addRoommate(Roommate roommate) throws Exception {
        return roommateDAO.addRoommate(roommate);
    }

    public boolean updateRoommate(Roommate roommate) throws Exception {
        return roommateDAO.updateRoommate(roommate);
    }

    public boolean deleteRoommate(int memberId) throws Exception {
        return roommateDAO.deleteRoommate(memberId);
    }

    public int addExpense(String description, BigDecimal totalAmount, int payerId,
                          List<Integer> participantIds, LocalDate expenseDate) throws Exception {

        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("At least one participant must be selected");
        }

        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Expense expense = new Expense();
        expense.setDescription(description.trim());
        expense.setTotalAmount(totalAmount);
        expense.setPayerId(payerId);
        expense.setExpenseDate(expenseDate);

        BigDecimal shareAmount = totalAmount.divide(new BigDecimal(participantIds.size()), 2, BigDecimal.ROUND_HALF_UP);

        List<ExpenseSplit> splits = new ArrayList<>();
        for (Integer memberId : participantIds) {
            ExpenseSplit split = new ExpenseSplit(expense.getExpenseId(), memberId, shareAmount);
            splits.add(split);
        }
        expense.setSplits(splits);

        return expenseDAO.addExpense(expense);
    }

    public List<Expense> getAllExpenses() throws Exception {
        return expenseDAO.getAllExpenses();
    }

    public List<ExpenseSplit> getSplitsForExpense(int expenseId) throws Exception {
        return expenseDAO.getSplitsForExpense(expenseId);
    }

    public List<Balance> getBalances() throws Exception {
        return expenseDAO.getBalances();
    }

    public boolean settleMemberDues(int memberId) throws Exception {
        return expenseDAO.settleMemberDues(memberId);
    }

    public boolean deleteExpense(int expenseId) throws Exception {
        return expenseDAO.deleteExpense(expenseId);
    }

    public void shutdown() {
        DatabaseManager.closeConnection();
    }
}