package com.gov.gov_erp.service;

import com.gov.gov_erp.dto.AllowancesDTO;
import com.gov.gov_erp.dto.DeductionsDTO;
import com.gov.gov_erp.dto.DeductionsRequestDTO;
import com.gov.gov_erp.dto.PayrollSummaryDTO;
import com.gov.gov_erp.dto.PayslipResponseDTO;
import com.gov.gov_erp.entity.Deductions;
import com.gov.gov_erp.entity.Employment;
import com.gov.gov_erp.entity.EmploymentStatus;
import com.gov.gov_erp.entity.Payslip;
import com.gov.gov_erp.exception.PayrollConflictException;
import com.gov.gov_erp.exception.ResourceNotFoundException;
import com.gov.gov_erp.exception.ValidationException;
import com.gov.gov_erp.repository.DeductionsRepository;
import com.gov.gov_erp.repository.EmploymentRepository;
import com.gov.gov_erp.repository.PayslipRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    private final EmploymentRepository employmentRepository;
    private final PayslipRepository payslipRepository;
    private final DeductionsRepository deductionsRepository;

    public PayrollService(EmploymentRepository employmentRepository,
                          PayslipRepository payslipRepository,
                          DeductionsRepository deductionsRepository) {
        this.employmentRepository = employmentRepository;
        this.payslipRepository = payslipRepository;
        this.deductionsRepository = deductionsRepository;
    }

    @PostConstruct
    @Transactional
    public void seedDefaultRates() {
        if (deductionsRepository.count() == 0) {
            Deductions defaultRates = Deductions.builder()
                    .employeeTaxRate(30.0)
                    .pensionRate(6.0)
                    .medicalInsuranceRate(5.0)
                    .othersRate(5.0)
                    .houseRate(14.0)
                    .transportRate(14.0)
                    .build();
            deductionsRepository.save(defaultRates);
        }
    }

    @Transactional(readOnly = true)
    public Deductions getRates() {
        return deductionsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Statutory deduction rates configuration not seeded."));
    }

    @Transactional
    public Deductions updateRates(DeductionsRequestDTO request) {
        Deductions rates = deductionsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> new Deductions());

        rates.setEmployeeTaxRate(request.getEmployeeTaxRate());
        rates.setPensionRate(request.getPensionRate());
        rates.setMedicalInsuranceRate(request.getMedicalInsuranceRate());
        rates.setOthersRate(request.getOthersRate());
        rates.setHouseRate(request.getHouseRate());
        rates.setTransportRate(request.getTransportRate());

        return deductionsRepository.save(rates);
    }

    @Transactional
    public PayrollSummaryDTO generatePayroll(Integer month, Integer year) {
        if (payslipRepository.existsByMonthAndYear(month, year)) {
            throw new PayrollConflictException("Payroll has already been processed for the period: " + month + "/" + year);
        }

        List<Employment> activeContracts = employmentRepository.findByStatus(EmploymentStatus.ACTIVE);
        if (activeContracts.isEmpty()) {
            throw new ValidationException("No ACTIVE employees found in the system to process payroll for period: " + month + "/" + year);
        }

        Deductions rates = getRates();

        int processedCount = 0;
        double batchGrossTotal = 0.0;
        double batchDeductionsTotal = 0.0;
        double batchNetTotal = 0.0;

        for (Employment contract : activeContracts) {
            double baseSalary = contract.getBaseSalary();

            double houseAllowance = baseSalary * (rates.getHouseRate() / 100.0);
            double transportAllowance = baseSalary * (rates.getTransportRate() / 100.0);
            double grossSalary = baseSalary + houseAllowance + transportAllowance;

            double employeeTax = baseSalary * (rates.getEmployeeTaxRate() / 100.0);
            double pension = baseSalary * (rates.getPensionRate() / 100.0);
            double medicalInsurance = baseSalary * (rates.getMedicalInsuranceRate() / 100.0);
            double others = baseSalary * (rates.getOthersRate() / 100.0);
            double totalDeductions = employeeTax + pension + medicalInsurance + others;

            if (totalDeductions > grossSalary) {
                throw new ValidationException(String.format(
                        "Deductions audit failed: Total deductions (%f FRW) exceed the Gross Salary limit (%f FRW) for employee %s.",
                        totalDeductions, grossSalary, contract.getEmployeeIdString()
                ));
            }

            double netSalary = baseSalary - totalDeductions;

            if (netSalary < 0) {
                throw new ValidationException(String.format(
                        "Salary audit failed: Calculated Net Salary cannot be negative (%f FRW) for employee %s.",
                        netSalary, contract.getEmployeeIdString()
                ));
            }

            Payslip payslip = Payslip.builder()
                    .month(month)
                    .year(year)
                    .baseSalary(baseSalary)
                    .houseAllowanceAmount(houseAllowance)
                    .transportAllowanceAmount(transportAllowance)
                    .employeeTaxAmount(employeeTax)
                    .pensionAmount(pension)
                    .medicalInsuranceAmount(medicalInsurance)
                    .othersAmount(others)
                    .grossSalary(grossSalary)
                    .netSalary(netSalary)
                    .status("Pending")
                    .generatedAt(LocalDateTime.now())
                    .employee(contract.getEmployee())
                    .build();

            payslipRepository.save(payslip);

            processedCount++;
            batchGrossTotal += grossSalary;
            batchDeductionsTotal += totalDeductions;
            batchNetTotal += netSalary;
        }

        return PayrollSummaryDTO.builder()
                .month(month)
                .year(year)
                .processedEmployeesCount(processedCount)
                .totalGrossPayout(batchGrossTotal)
                .totalDeductions(batchDeductionsTotal)
                .totalNetPayout(batchNetTotal)
                .status("SUCCESSFUL")
                .build();
    }

    @Transactional
    public PayrollSummaryDTO approvePayroll(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(month, year);
        if (payslips.isEmpty()) {
            throw new ResourceNotFoundException("No payroll found for period: " + month + "/" + year);
        }

        for (Payslip payslip : payslips) {
            payslip.setStatus("Paid");
            payslipRepository.save(payslip);
        }

        double totalGross = payslips.stream().mapToDouble(Payslip::getGrossSalary).sum();
        double totalNet = payslips.stream().mapToDouble(Payslip::getNetSalary).sum();
        double totalDeductions = payslips.stream()
                .mapToDouble(p -> p.getEmployeeTaxAmount() + p.getPensionAmount()
                        + p.getMedicalInsuranceAmount() + p.getOthersAmount())
                .sum();

        return PayrollSummaryDTO.builder()
                .month(month)
                .year(year)
                .processedEmployeesCount(payslips.size())
                .totalGrossPayout(totalGross)
                .totalDeductions(totalDeductions)
                .totalNetPayout(totalNet)
                .status("APPROVED")
                .build();
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDTO> getPayslipsByEmployee(Long employeeId) {
        List<Payslip> payslips = payslipRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId);
        return payslips.stream()
                .map(this::mapToPayslipResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDTO> getPayslipsByPeriod(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(month, year);
        return payslips.stream()
                .map(this::mapToPayslipResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayslipResponseDTO getPayslipById(Long id) {
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No payslip found matching ID: " + id));
        return mapToPayslipResponseDTO(payslip);
    }

    public PayslipResponseDTO mapToPayslipResponseDTO(Payslip payslip) {
        Employment contract = payslip.getEmployee().getEmployment();
        String employeeIdStr = contract != null ? contract.getEmployeeIdString() : "UNKNOWN";

        AllowancesDTO allowancesDTO = AllowancesDTO.builder()
                .houseAllowance(payslip.getHouseAllowanceAmount())
                .transportAllowance(payslip.getTransportAllowanceAmount())
                .totalAllowances(payslip.getHouseAllowanceAmount() + payslip.getTransportAllowanceAmount())
                .build();

        DeductionsDTO deductionsDTO = DeductionsDTO.builder()
                .employeeTax(payslip.getEmployeeTaxAmount())
                .pension(payslip.getPensionAmount())
                .medicalInsurance(payslip.getMedicalInsuranceAmount())
                .others(payslip.getOthersAmount())
                .totalDeductions(payslip.getEmployeeTaxAmount() + payslip.getPensionAmount() +
                        payslip.getMedicalInsuranceAmount() + payslip.getOthersAmount())
                .build();

        return PayslipResponseDTO.builder()
                .payslipId(payslip.getId())
                .employeeName(payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName())
                .employeeIdString(employeeIdStr)
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .baseSalary(payslip.getBaseSalary())
                .allowances(allowancesDTO)
                .deductions(deductionsDTO)
                .grossSalary(payslip.getGrossSalary())
                .netSalary(payslip.getNetSalary())
                .status(payslip.getStatus())
                .generatedAt(payslip.getGeneratedAt())
                .build();
    }
}
