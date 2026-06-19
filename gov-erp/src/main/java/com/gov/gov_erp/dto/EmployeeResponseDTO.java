package com.gov.gov_erp.dto;

import lombok.*;
import java.time.LocalDate;

/**
 * Clean data transfer representation of an Employee profile sent to the client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String district;
    private String mobile;
    private LocalDate dateOfBirth;
    
    // Contractual Details
    private String employeeIdString;
    private String institution;
    private String department;
    private String position;
    private Double baseSalary;
    private String status;
    private LocalDate joiningDate;
}
