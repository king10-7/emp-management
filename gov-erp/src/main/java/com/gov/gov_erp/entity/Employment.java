package com.gov.gov_erp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Stores contractual and salary details for an employee's position.
 * The baseSalary defined here is used for monthly payroll generation.
 */
@Entity
@Table(name = "employments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique organizational ID assigned to the employee (e.g. GR-2026-0001)
    @Column(name = "employee_id_string", nullable = false, unique = true)
    private String employeeIdString;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String position;

    @Column(name = "base_salary", nullable = false)
    private Double baseSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    // Link back to the parent Employee biographical record.
    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
