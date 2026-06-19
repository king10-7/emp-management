package com.gov.gov_erp.repository;

import com.gov.gov_erp.entity.Employment;
import com.gov.gov_erp.entity.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    
    // Check if a business employee ID string is already registered
    boolean existsByEmployeeIdString(String employeeIdString);

    // Retrieve contract information by the business employee ID
    Optional<Employment> findByEmployeeIdString(String employeeIdString);

    // Fetch all employment profiles based on status (e.g. ACTIVE)
    List<Employment> findByStatus(EmploymentStatus status);
}
