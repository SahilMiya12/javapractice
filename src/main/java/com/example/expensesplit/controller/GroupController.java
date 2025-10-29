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
        try {
            GroupDTO createdGroup = groupService.createGroup(groupDTO);
            return ResponseEntity.ok(createdGroup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        try {
            List<GroupDTO> groups = groupService.getAllGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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
        try {
            groupService.addMemberToGroup(groupId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/members")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable("id") Long groupId,
            @RequestParam Long userId) {
        try {
            groupService.removeMemberFromGroup(groupId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDTO>> getGroupMembers(@PathVariable("id") Long groupId) {
        try {
            List<UserDTO> members = groupService.getGroupMembers(groupId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long groupId) {
        try {
            groupService.deleteGroup(groupId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}