package com.gov.gov_erp.service;

import com.gov.gov_erp.dto.EmployeeRequestDTO;
import com.gov.gov_erp.dto.EmployeeResponseDTO;
import com.gov.gov_erp.entity.Employee;
import com.gov.gov_erp.entity.Employment;
import com.gov.gov_erp.entity.EmploymentStatus;
import com.gov.gov_erp.exception.ResourceNotFoundException;
import com.gov.gov_erp.exception.ValidationException;
import com.gov.gov_erp.repository.EmployeeRepository;
import com.gov.gov_erp.repository.EmploymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all business logic for registering and managing government employee profiles
 * and their corresponding employment contracts.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmploymentRepository employmentRepository) {
        this.employeeRepository = employeeRepository;
        this.employmentRepository = employmentRepository;
    }

    /**
     * Registers a new employee and their employment contract details.
     * Enforces unique constraint checks on email and business employee IDs.
     */
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        // Enforce duplicate checks
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("An employee with the email address '" + request.getEmail() + "' is already registered.");
        }
        if (employmentRepository.existsByEmployeeIdString(request.getEmployeeIdString())) {
            throw new ValidationException("An employment record with the employee ID '" + request.getEmployeeIdString() + "' already exists.");
        }

        // Map and construct biographical record
        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .district(request.getDistrict())
                .mobile(request.getMobile())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        // Map and construct contract details
        EmploymentStatus status = EmploymentStatus.valueOf(request.getStatus().toUpperCase());
        Employment employment = Employment.builder()
                .employeeIdString(request.getEmployeeIdString())
                .institution(request.getInstitution())
                .department(request.getDepartment())
                .position(request.getPosition())
                .baseSalary(request.getBaseSalary())
                .status(status)
                .joiningDate(request.getJoiningDate())
                .employee(employee)
                .build();

        employee.setEmployment(employment);

        // Save root entity (cascades save to employment due to CascadeType.ALL)
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponseDTO(savedEmployee);
    }

    /**
     * Retrieves all registered employees.
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves details of a single employee by their auto-generated database primary key.
     */
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No employee profile found matching the database ID: " + id));
        return mapToResponseDTO(employee);
    }

    /**
     * Updates the active/inactive status of an employee contract.
     */
    @Transactional
    public EmployeeResponseDTO updateEmployeeStatus(Long id, String statusStr) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No employee profile found matching the database ID: " + id));

        try {
            EmploymentStatus newStatus = EmploymentStatus.valueOf(statusStr.toUpperCase());
            employee.getEmployment().setStatus(newStatus);
            Employee updatedEmployee = employeeRepository.save(employee);
            return mapToResponseDTO(updatedEmployee);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status provided. Accepted statuses are: ACTIVE, INACTIVE.");
        }
    }

    /**
     * Updates all personal and employment details for an existing employee.
     * Email and Employee ID uniqueness checks are re-validated against other records.
     */
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No employee profile found matching the database ID: " + id));

        // Only check for duplicate email if the email is being changed to a different one
        if (!employee.getEmail().equals(request.getEmail()) && employeeRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Another employee with the email '" + request.getEmail() + "' already exists.");
        }

        Employment employment = employee.getEmployment();
        // Only check for duplicate employee ID if it is being changed
        if (!employment.getEmployeeIdString().equals(request.getEmployeeIdString())
                && employmentRepository.existsByEmployeeIdString(request.getEmployeeIdString())) {
            throw new ValidationException("Another employee with the ID '" + request.getEmployeeIdString() + "' already exists.");
        }

        // Update personal information
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDistrict(request.getDistrict());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());

        // Update employment / contract information
        employment.setEmployeeIdString(request.getEmployeeIdString());
        employment.setInstitution(request.getInstitution());
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setStatus(EmploymentStatus.valueOf(request.getStatus().toUpperCase()));
        employment.setJoiningDate(request.getJoiningDate());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponseDTO(updatedEmployee);
    }

    /**
     * Permanently removes an employee and their linked employment contract from the system.
     */
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No employee profile found matching the database ID: " + id));
        employeeRepository.delete(employee);
    }

    /**
     * Maps an Employee JPA entity structure to the public response DTO format.
     */
    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        Employment employment = employee.getEmployment();
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .district(employee.getDistrict())
                .mobile(employee.getMobile())
                .dateOfBirth(employee.getDateOfBirth())
                .employeeIdString(employment != null ? employment.getEmployeeIdString() : null)
                .institution(employment != null ? employment.getInstitution() : null)
                .department(employment != null ? employment.getDepartment() : null)
                .position(employment != null ? employment.getPosition() : null)
                .baseSalary(employment != null ? employment.getBaseSalary() : null)
                .status(employment != null ? employment.getStatus().name() : null)
                .joiningDate(employment != null ? employment.getJoiningDate() : null)
                .build();
    }
}
