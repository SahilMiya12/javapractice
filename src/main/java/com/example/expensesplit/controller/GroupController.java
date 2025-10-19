package com.example.expensesplit.controller;

import com.example.expensesplit.dto.GroupDTO;
import com.example.expensesplit.dto.UserDTO;
import com.example.expensesplit.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    
    @Autowired
    private GroupService groupService;
    
    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@Valid @RequestBody GroupDTO groupDTO) {
        GroupDTO createdGroup = groupService.createGroup(groupDTO);
        return ResponseEntity.ok(createdGroup);
    }
    
    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable("id") Long groupId) {
        try {
            GroupDTO group = groupService.getGroupById(groupId);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMemberToGroup(
            @PathVariable("id") Long groupId,
            @RequestParam Long userId) {
        groupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDTO>> getGroupMembers(@PathVariable("id") Long groupId) {
        List<UserDTO> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }
}