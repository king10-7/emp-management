package com.gov.gov_erp.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Complete details of a generated payslip returned to the client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipResponseDTO {
    private Long payslipId;
    private String employeeName;
    private String employeeIdString;
    private Integer month;
    private Integer year;
    private Double baseSalary;
    private AllowancesDTO allowances;
    private DeductionsDTO deductions;
    private Double grossSalary;
    private Double netSalary;
    private String status;
    private LocalDateTime generatedAt;
}
