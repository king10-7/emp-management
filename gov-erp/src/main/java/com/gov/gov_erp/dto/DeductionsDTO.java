package com.gov.gov_erp.dto;

import lombok.*;

/**
 * Breakdown of deductions applied to the payroll run.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductionsDTO {
    private Double employeeTax;
    private Double pension;
    private Double medicalInsurance;
    private Double others;
    private Double totalDeductions;
}
