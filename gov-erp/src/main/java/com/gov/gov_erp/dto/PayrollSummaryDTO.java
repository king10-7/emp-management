package com.gov.gov_erp.dto;

import lombok.*;

/**
 * Summary returned after a batch payroll execution run.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollSummaryDTO {
    private Integer month;
    private Integer year;
    private int processedEmployeesCount;
    private Double totalGrossPayout;
    private Double totalDeductions;
    private Double totalNetPayout;
    private String status;
}
