package com.gov.gov_erp.repository;

import com.gov.gov_erp.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    
    // Check if payroll has already been run for this employee for the given month/year
    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    // Check if any payslips have been generated for a month/year in the system
    boolean existsByMonthAndYear(Integer month, Integer year);

    // Retrieve all historical payslips for a given employee
    List<Payslip> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);

    // Find specific payslips by month/year
    List<Payslip> findByMonthAndYear(Integer month, Integer year);

    // Find specific payslip by employee and period
    Optional<Payslip> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
}
