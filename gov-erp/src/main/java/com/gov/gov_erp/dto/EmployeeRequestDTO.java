package com.gov.gov_erp.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Carries input request parameters and constraints for registering or updating employees.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid email format")
    private String email;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Mobile number must be a valid phone number (10 to 15 digits)")
    private String mobile;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Employee ID String is required")
    @Size(max = 20, message = "Employee ID must not exceed 20 characters")
    private String employeeIdString;

    @NotBlank(message = "Institution is required")
    private String institution;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Base salary is required")
    @PositiveOrZero(message = "Base salary must be zero or positive")
    private Double baseSalary;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
    private String status;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
}
