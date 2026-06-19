package com.gov.gov_erp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Stores basic personal and biological information for an employee.
 * Keeping this distinct from the Employment contract details satisfies 2NF/3NF
 * and allows for biographical data to remain stable even if employment contracts change.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String mobile;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    // One-to-One relationship back-reference to Employment.
    // Cascade type ALL ensures that when we save or update an Employee, their contract is handled as well.
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Employment employment;
}
