package com.gov.gov_erp.controller;

import com.gov.gov_erp.dto.EmployeeRequestDTO;
import com.gov.gov_erp.dto.EmployeeResponseDTO;
import com.gov.gov_erp.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Exposes REST API endpoints for HR managers to manage employee profiles
 * and update contract details.
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "Endpoints for registering, listing, and managing employee profiles.")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Register a new employee", description = "Creates a new employee profile and maps their initial employment details.")
    @ApiResponse(responseCode = "201", description = "Employee registered successfully",
            content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO request) {
        EmployeeResponseDTO created = employeeService.createEmployee(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all employees", description = "Fetches a complete listing of all registered employees and contracts.")
    @ApiResponse(responseCode = "200", description = "Employee list retrieved successfully")
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<EmployeeResponseDTO> list = employeeService.getAllEmployees();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieves a specific employee's personal and employment details by database ID.")
    @ApiResponse(responseCode = "200", description = "Employee profile retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Employee profile not found")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @Parameter(description = "Database ID of the employee to retrieve", required = true)
            @PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update employee status", description = "Updates an employee's status to ACTIVE or INACTIVE (affecting payroll qualification).")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status parameter provided")
    @ApiResponse(responseCode = "404", description = "Employee profile not found")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeStatus(
            @Parameter(description = "Database ID of the employee to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status value (ACTIVE or INACTIVE)", required = true)
            @RequestParam String status) {
        EmployeeResponseDTO updated = employeeService.updateEmployeeStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee details", description = "Updates all personal and employment details for an existing employee profile.")
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed")
    @ApiResponse(responseCode = "404", description = "Employee profile not found")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @Parameter(description = "Database ID of the employee to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO request) {
        EmployeeResponseDTO updated = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Permanently removes an employee and their linked employment contract from the system.")
    @ApiResponse(responseCode = "204", description = "Employee deleted successfully")
    @ApiResponse(responseCode = "404", description = "Employee profile not found")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "Database ID of the employee to delete", required = true)
            @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
