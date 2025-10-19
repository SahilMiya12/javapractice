package com.example.expensesplit.controller;

import com.example.expensesplit.dto.ExpenseDTO;
import com.example.expensesplit.dto.ExpenseParticipantDTO;
import com.example.expensesplit.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);
        return ResponseEntity.ok(createdExpense);
    }
    
    @GetMapping("/group/{id}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByGroup(@PathVariable("id") Long groupId) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByGroup(groupId);
        return ResponseEntity.ok(expenses);
    }
    
    @PostMapping("/{id}/participants")
    public ResponseEntity<Void> addParticipantsToExpense(
            @PathVariable("id") Long expenseId,
            @RequestBody List<ExpenseParticipantDTO> participants) {
        expenseService.addParticipantsToExpense(expenseId, participants);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ExpenseParticipantDTO>> getExpenseParticipants(
            @PathVariable("id") Long expenseId) {
        List<ExpenseParticipantDTO> participants = expenseService.getExpenseParticipants(expenseId);
        return ResponseEntity.ok(participants);
    }
    
    // ADD THIS DELETE ENDPOINT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable("id") Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok().build();
    }
}