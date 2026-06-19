package com.gov.gov_erp.dto;

import lombok.*;

/**
 * Breakdown of allowances calculated for the employee.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllowancesDTO {
    private Double houseAllowance;
    private Double transportAllowance;
    private Double totalAllowances;
}
