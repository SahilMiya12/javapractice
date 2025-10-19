package com.example.expensesplit.controller;

import com.example.expensesplit.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private ExpenseService expenseService;
    
    @GetMapping("/")
    public String index(Model model) {
        try {
            model.addAttribute("totalUsers", userService.getAllUsers().size());
            model.addAttribute("totalGroups", groupService.getAllGroups().size());
            
            // Calculate total expenses across all groups
            long totalExpenses = 0;
            var groups = groupService.getAllGroups();
            for (var group : groups) {
                try {
                    totalExpenses += expenseService.getExpensesByGroup(group.getGroupId()).size();
                } catch (Exception e) {
                    // Skip if no expenses in group
                }
            }
            model.addAttribute("totalExpenses", totalExpenses);
        } catch (Exception e) {
            // If database is not ready, set defaults
            model.addAttribute("totalUsers", 0);
            model.addAttribute("totalGroups", 0);
            model.addAttribute("totalExpenses", 0);
        }
        return "index";
    }
    
    @GetMapping("/users")
    public String users() {
        return "users";
    }
    
    @GetMapping("/groups")
    public String groups() {
        return "groups";
    }
    
    @GetMapping("/expenses")
    public String expenses() {
        return "expenses";
    }
    
    @GetMapping("/balances")
    public String balances() {
        return "balances";
    }
}