package com.gov.gov_erp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Payload to manage and update percentage rates for taxes, allowances, and deductions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductionsRequestDTO {

    @NotNull(message = "Employee tax rate is required")
    @Min(value = 0, message = "Rate must be positive or zero")
    @Max(value = 100, message = "Rate must not exceed 100%")
    private Double employeeTaxRate;

    @NotNull(message = "Pension rate is required")
    @Min(value = 0, message = "Rate must be positive or zero")
    @Max(value = 100, message = "Rate must not exceed 100%")
    private Double pensionRate;

    @NotNull(message = "Medical insurance rate is required")
    @Min(value = 0, message = "Rate must be positive or zero")
    @Max(value = 100, message = "Rate must not exceed 100%")
    private Double medicalInsuranceRate;

    @NotNull(message = "Others rate is required")
    @Min(value = 0, message = "Rate must be positive or zero")
    @Max(value = 100, message = "Rate must not exceed 100%")
    private Double othersRate;

    @NotNull(message = "House allowance rate is required")
    @Min(value = 0, message = "Rate must be positive or zero")
    @Max(value = 100, message = "Rate must not exceed 100%")
    private Double houseRate;

    @NotNull(message = "Transport allowance rate is required")
    @Min(value = 0, message = "Rate must be positive or zero")
    @Max(value = 100, message = "Rate must not exceed 100%")
    private Double transportRate;
}
