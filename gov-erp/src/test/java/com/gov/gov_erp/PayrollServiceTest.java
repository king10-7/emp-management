package com.gov.gov_erp;

import com.gov.gov_erp.dto.PayrollRequestDTO;
import com.gov.gov_erp.dto.PayrollSummaryDTO;
import com.gov.gov_erp.entity.Deductions;
import com.gov.gov_erp.entity.Employee;
import com.gov.gov_erp.entity.Employment;
import com.gov.gov_erp.entity.EmploymentStatus;
import com.gov.gov_erp.entity.Payslip;
import com.gov.gov_erp.exception.ValidationException;
import com.gov.gov_erp.repository.DeductionsRepository;
import com.gov.gov_erp.repository.EmploymentRepository;
import com.gov.gov_erp.repository.PayslipRepository;
import com.gov.gov_erp.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests validating the mathematical accuracy of the monthly payroll calculations,
 * allowance ratios, statutory deduction thresholds, and output formatting.
 */
class PayrollServiceTest {

    private PayrollService payrollService;

    @Mock
    private EmploymentRepository employmentRepository;

    @Mock
    private PayslipRepository payslipRepository;

    @Mock
    private DeductionsRepository deductionsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payrollService = new PayrollService(employmentRepository, payslipRepository, deductionsRepository);
    }

    @Test
    void testPayrollCalculation_MathAccuracy() {
        // Arrange
        PayrollRequestDTO request = new PayrollRequestDTO();
        request.setMonth(6);
        request.setYear(2026);
        
        Employee mockEmployee = Employee.builder()
                .id(1L)
                .firstName("Jean Paul")
                .lastName("Nkurunziza")
                .email("jp.nkurunziza@gov.rw")
                .build();

        Employment mockEmployment = Employment.builder()
                .id(1L)
                .employeeIdString("GR-2026-0043")
                .institution("RCA")
                .baseSalary(70000.0) // 70,000 FRW
                .status(EmploymentStatus.ACTIVE)
                .employee(mockEmployee)
                .joiningDate(LocalDate.of(2026, 1, 10))
                .build();

        mockEmployee.setEmployment(mockEmployment);

        // Seed mock rates configuration (matches default exam criteria)
        Deductions mockRates = Deductions.builder()
                .id(1L)
                .employeeTaxRate(30.0)
                .pensionRate(6.0)
                .medicalInsuranceRate(5.0)
                .othersRate(5.0)
                .houseRate(14.0)
                .transportRate(14.0)
                .build();

        when(payslipRepository.existsByMonthAndYear(request.getMonth(), request.getYear())).thenReturn(false);
        when(employmentRepository.findByStatus(EmploymentStatus.ACTIVE)).thenReturn(List.of(mockEmployment));
        when(deductionsRepository.findAll()).thenReturn(List.of(mockRates));

        // Act
        PayrollSummaryDTO summary = payrollService.generatePayroll(request);

        // Assert
        assertNotNull(summary);
        assertEquals(1, summary.getProcessedEmployeesCount());
        assertEquals("SUCCESSFUL", summary.getStatus());

        // Math Verification:
        // Allowances:
        // House allowance: 70,000 * 14% = 9,800 FRW
        // Transport allowance: 70,000 * 14% = 9,800 FRW
        // Gross Salary = 70,000 + 9,800 + 9,800 = 89,600 FRW
        assertEquals(89600.0, summary.getTotalGrossPayout());

        // Deductions:
        // Tax: 70,000 * 30% = 21,000 FRW
        // Pension: 70,000 * 6% = 4,200 FRW
        // Medical: 70,000 * 5% = 3,500 FRW
        // Others: 70,000 * 5% = 3,500 FRW
        // Total Deductions = 32,200 FRW
        // Net Salary = Base Salary - Total Deductions = 70,000 - 32,200 = 57,800 FRW (matches user's example)
        assertEquals(32200.0, summary.getTotalDeductions());
        assertEquals(57800.0, summary.getTotalNetPayout());

        // Capture saved entity details to verify individual computations
        ArgumentCaptor<Payslip> payslipCaptor = ArgumentCaptor.forClass(Payslip.class);
        verify(payslipRepository, times(1)).save(payslipCaptor.capture());

        Payslip savedPayslip = payslipCaptor.getValue();
        assertEquals(89600.0, savedPayslip.getGrossSalary());
        assertEquals(57800.0, savedPayslip.getNetSalary());
        assertEquals(21000.0, savedPayslip.getEmployeeTaxAmount());
        assertEquals(4200.0, savedPayslip.getPensionAmount());
        assertEquals(3500.0, savedPayslip.getMedicalInsuranceAmount());
        assertEquals(3500.0, savedPayslip.getOthersAmount());
        assertEquals(9800.0, savedPayslip.getHouseAllowanceAmount());
        assertEquals(9800.0, savedPayslip.getTransportAllowanceAmount());
        assertEquals(6, savedPayslip.getMonth());
        assertEquals(2026, savedPayslip.getYear());
    }

    @Test
    void testPayrollCalculation_DeductionsExceedGrossLimit() {
        // Arrange
        PayrollRequestDTO request = new PayrollRequestDTO();
        request.setMonth(6);
        request.setYear(2026);
        
        Employee mockEmployee = Employee.builder()
                .id(1L)
                .firstName("Jean Paul")
                .lastName("Nkurunziza")
                .build();

        Employment mockEmployment = Employment.builder()
                .id(1L)
                .employeeIdString("GR-2026-0043")
                .institution("RCA")
                .baseSalary(70000.0)
                .status(EmploymentStatus.ACTIVE)
                .employee(mockEmployee)
                .build();

        mockEmployee.setEmployment(mockEmployment);

        // Seed mock rates that cause deductions to exceed base salary
        Deductions mockRates = Deductions.builder()
                .id(1L)
                .employeeTaxRate(90.0)
                .pensionRate(20.0)
                .medicalInsuranceRate(20.0)
                .othersRate(20.0)
                .houseRate(14.0)
                .transportRate(14.0)
                .build();

        when(payslipRepository.existsByMonthAndYear(request.getMonth(), request.getYear())).thenReturn(false);
        when(employmentRepository.findByStatus(EmploymentStatus.ACTIVE)).thenReturn(List.of(mockEmployment));
        when(deductionsRepository.findAll()).thenReturn(List.of(mockRates));

        // Act & Assert
        // Should throw ValidationException due to deductions audit check
        assertThrows(ValidationException.class, () -> {
            payrollService.generatePayroll(request);
        });
    }
}
