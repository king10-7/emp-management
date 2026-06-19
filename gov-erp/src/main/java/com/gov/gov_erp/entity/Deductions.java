package com.gov.gov_erp.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Stores the percentage rates (%) for taxes, allowances, and deductions.
 * This entity acts as a central configuration table in the database.
 * The rates are fetched dynamically during payroll runs.
 */
@Entity
@Table(name = "deductions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deductions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_tax_rate", nullable = false)
    private Double employeeTaxRate;

    @Column(name = "pension_rate", nullable = false)
    private Double pensionRate;

    @Column(name = "medical_insurance_rate", nullable = false)
    private Double medicalInsuranceRate;

    @Column(name = "others_rate", nullable = false)
    private Double othersRate;

    @Column(name = "house_rate", nullable = false)
    private Double houseRate;

    @Column(name = "transport_rate", nullable = false)
    private Double transportRate;
}
