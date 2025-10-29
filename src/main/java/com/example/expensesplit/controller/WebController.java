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
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Get user count
            int userCount = userService.getAllUsers().size();
            model.addAttribute("totalUsers", userCount);
            
            // Get group count
            int groupCount = groupService.getAllGroups().size();
            model.addAttribute("totalGroups", groupCount);
            
            // Calculate total expenses count
            long totalExpenses = 0;
            try {
                totalExpenses = expenseService.getAllExpenses().size();
            } catch (Exception e) {
                // If no expenses exist, set to 0
                totalExpenses = 0;
            }
            model.addAttribute("totalExpenses", totalExpenses);
            
        } catch (Exception e) {
            // If database is not ready, set defaults
            System.err.println("Error loading dashboard data: " + e.getMessage());
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