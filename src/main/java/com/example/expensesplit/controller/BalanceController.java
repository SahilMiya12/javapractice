package com.example.expensesplit.controller;

import com.example.expensesplit.dto.BalanceDTO;
import com.example.expensesplit.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
public class BalanceController {
    
    @Autowired
    private BalanceService balanceService;
    
    @GetMapping("/group/{id}")
    public ResponseEntity<List<BalanceDTO>> getGroupBalances(@PathVariable("id") Long groupId) {
        try {
            List<BalanceDTO> balances = balanceService.getGroupBalances(groupId);
            return ResponseEntity.ok(balances);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}