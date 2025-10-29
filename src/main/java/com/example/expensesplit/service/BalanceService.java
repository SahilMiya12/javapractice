package com.example.expensesplit.service;

import com.example.expensesplit.dto.BalanceDTO;
import com.example.expensesplit.entity.Expense;
import com.example.expensesplit.entity.ExpenseParticipant;
import com.example.expensesplit.entity.GroupMember;
import com.example.expensesplit.entity.User;
import com.example.expensesplit.repository.ExpenseParticipantRepository;
import com.example.expensesplit.repository.ExpenseRepository;
import com.example.expensesplit.repository.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BalanceService {
    
    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    public List<BalanceDTO> getGroupBalances(Long groupId) {
        List<GroupMember> groupMembers = groupMemberRepository.findByGroupGroupId(groupId);
        Map<Long, User> userMap = new HashMap<>();
        Map<Long, BigDecimal> paidAmounts = new HashMap<>();
        Map<Long, BigDecimal> owedAmounts = new HashMap<>();
        
        // Initialize user map and amounts
        for (GroupMember member : groupMembers) {
            User user = member.getUser();
            userMap.put(user.getUserId(), user);
            paidAmounts.put(user.getUserId(), BigDecimal.ZERO);
            owedAmounts.put(user.getUserId(), BigDecimal.ZERO);
        }
        
        // Calculate paid amounts
        List<Expense> expenses = expenseRepository.findByGroupGroupId(groupId);
        for (Expense expense : expenses) {
            Long paidByUserId = expense.getPaidBy().getUserId();
            BigDecimal currentPaid = paidAmounts.getOrDefault(paidByUserId, BigDecimal.ZERO);
            paidAmounts.put(paidByUserId, currentPaid.add(BigDecimal.valueOf(expense.getAmount())));
        }
        
        // Calculate owed amounts
        for (Long userId : userMap.keySet()) {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByUserUserIdAndExpenseGroupGroupId(userId, groupId);
            BigDecimal totalOwed = participants.stream()
                    .map(participant -> BigDecimal.valueOf(participant.getShareAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            owedAmounts.put(userId, totalOwed);
        }
        
        // Calculate balances
        List<BalanceDTO> balances = new ArrayList<>();
        for (Long userId : userMap.keySet()) {
            BigDecimal paid = paidAmounts.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal owed = owedAmounts.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal balance = paid.subtract(owed);
            
            BalanceDTO balanceDTO = new BalanceDTO();
            balanceDTO.setUserId(userId);
            balanceDTO.setUserName(userMap.get(userId).getName());
            balanceDTO.setBalance(balance);
            
            balances.add(balanceDTO);
        }
        
        return balances;
    }
}