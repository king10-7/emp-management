package com.gov.gov_erp.repository;

import com.gov.gov_erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Check if an email is already registered in the system
    boolean existsByEmail(String email);

    // Retrieve an employee profile by email
    Optional<Employee> findByEmail(String email);
}
