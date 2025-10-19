package com.example.expensesplit.repository;

import com.example.expensesplit.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // This method is automatically provided by JpaRepository
    // Optional<Group> findById(Long id);
}