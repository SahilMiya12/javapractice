package com.example.expensesplit.repository;

import com.example.expensesplit.entity.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {
    List<ExpenseParticipant> findByExpenseExpenseId(Long expenseId);
    List<ExpenseParticipant> findByUserUserIdAndExpenseGroupGroupId(Long userId, Long groupId);
}
