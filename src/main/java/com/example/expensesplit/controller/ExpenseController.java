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
    public ResponseEntity<?> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        try {
            ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);
            return ResponseEntity.ok(createdExpense);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllExpenses() {
        try {
            List<ExpenseDTO> expenses = expenseService.getAllExpenses();
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error loading expenses: " + e.getMessage());
        }
    }
    
    @GetMapping("/group/{id}")
    public ResponseEntity<?> getExpensesByGroup(@PathVariable("id") Long groupId) {
        try {
            List<ExpenseDTO> expenses = expenseService.getExpensesByGroup(groupId);
            return ResponseEntity.ok(expenses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error loading group expenses: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/participants")
    public ResponseEntity<?> addParticipantsToExpense(
            @PathVariable("id") Long expenseId,
            @RequestBody List<ExpenseParticipantDTO> participants) {
        try {
            expenseService.addParticipantsToExpense(expenseId, participants);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding participants: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/participants")
    public ResponseEntity<?> getExpenseParticipants(
            @PathVariable("id") Long expenseId) {
        try {
            List<ExpenseParticipantDTO> participants = expenseService.getExpenseParticipants(expenseId);
            return ResponseEntity.ok(participants);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error loading participants: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("id") Long expenseId) {
        try {
            expenseService.deleteExpense(expenseId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting expense: " + e.getMessage());
        }
    }
}