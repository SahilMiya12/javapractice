package com.example.expensesplit.service;

import com.example.expensesplit.dto.GroupDTO;
import com.example.expensesplit.dto.UserDTO;
import com.example.expensesplit.entity.Group;
import com.example.expensesplit.entity.GroupMember;
import com.example.expensesplit.entity.User;
import com.example.expensesplit.repository.GroupMemberRepository;
import com.example.expensesplit.repository.GroupRepository;
import com.example.expensesplit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    public GroupDTO createGroup(GroupDTO groupDTO) {
        Group group = new Group();
        group.setName(groupDTO.getName());
        
        Group savedGroup = groupRepository.save(group);
        return convertToDTO(savedGroup);
    }
    
    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public GroupDTO getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        return convertToDTO(group);
    }
    
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        groupRepository.delete(group);
    }
    
    public void addMemberToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (groupMemberRepository.existsByGroupGroupIdAndUserUserId(groupId, userId)) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        
        groupMemberRepository.save(groupMember);
    }
    
    public List<UserDTO> getGroupMembers(Long groupId) {
        List<GroupMember> members = groupMemberRepository.findByGroupGroupId(groupId);
        return members.stream()
                .map(member -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(member.getUser().getUserId());
                    dto.setName(member.getUser().getName());
                    dto.setEmail(member.getUser().getEmail());
                    dto.setContactNo(member.getUser().getContactNo());
                    dto.setJoinDate(member.getUser().getJoinDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    private GroupDTO convertToDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setCreatedAt(group.getCreatedAt());
        return dto;
    }
}