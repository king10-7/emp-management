package com.gov.gov_erp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a monthly salary disbursement record for an employee.
 * Stores calculated amounts as snapshots so historical records remain locked
 * even if global rates are modified later.
 * Status starts as "Pending" and moves to "Paid" when the admin approves the payroll.
 */
@Entity
@Table(name = "payslips", uniqueConstraints = {
    // Prevents duplicate payroll for the same employee in the same month/year
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Separate month and year columns matching the exam payslip table structure
    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "base_salary", nullable = false)
    private Double baseSalary;

    @Column(name = "house_allowance_amount", nullable = false)
    private Double houseAllowanceAmount;

    @Column(name = "transport_allowance_amount", nullable = false)
    private Double transportAllowanceAmount;

    @Column(name = "employee_tax_amount", nullable = false)
    private Double employeeTaxAmount;

    @Column(name = "pension_amount", nullable = false)
    private Double pensionAmount;

    @Column(name = "medical_insurance_amount", nullable = false)
    private Double medicalInsuranceAmount;

    @Column(name = "others_amount", nullable = false)
    private Double othersAmount;

    @Column(name = "gross_salary", nullable = false)
    private Double grossSalary;

    @Column(name = "net_salary", nullable = false)
    private Double netSalary;

    // Pending = payroll generated but not approved, Paid = admin approved the payroll
    @Column(nullable = false)
    @Builder.Default
    private String status = "Pending";

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
