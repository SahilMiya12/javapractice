package com.example.expensesplit.repository;

import com.example.expensesplit.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupGroupId(Long groupId);
    Optional<GroupMember> findByGroupGroupIdAndUserUserId(Long groupId, Long userId);
    boolean existsByGroupGroupIdAndUserUserId(Long groupId, Long userId);
}
