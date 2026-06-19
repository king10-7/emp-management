package com.gov.gov_erp.controller;

import com.gov.gov_erp.dto.PayrollRequestDTO;
import com.gov.gov_erp.dto.PayrollSummaryDTO;
import com.gov.gov_erp.dto.PayslipResponseDTO;
import com.gov.gov_erp.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@Tag(name = "Payroll Management", description = "Endpoints for generating monthly payroll lists and retrieving detailed payslips.")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate monthly payroll", description = "Triggers salary, allowance, and deduction calculations for all ACTIVE employees for a given period.")
    @ApiResponse(responseCode = "200", description = "Payroll generated successfully",
            content = @Content(schema = @Schema(implementation = PayrollSummaryDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request payload or processing failed")
    @ApiResponse(responseCode = "409", description = "Payroll already generated for the requested period")
    public ResponseEntity<PayrollSummaryDTO> generatePayroll(@Valid @RequestBody PayrollRequestDTO request) {
        PayrollSummaryDTO summary = payrollService.generatePayroll(request.getMonth(), request.getYear());
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/approve")
    @Operation(summary = "Approve payroll", description = "Approves the payroll for a given month/year, updating all payslips to 'Paid' status.")
    @ApiResponse(responseCode = "200", description = "Payroll approved successfully",
            content = @Content(schema = @Schema(implementation = PayrollSummaryDTO.class)))
    @ApiResponse(responseCode = "404", description = "Payroll not found for the requested period")
    public ResponseEntity<PayrollSummaryDTO> approvePayroll(@Valid @RequestBody PayrollRequestDTO request) {
        PayrollSummaryDTO summary = payrollService.approvePayroll(request.getMonth(), request.getYear());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/payslips/employee/{employeeId}")
    @Operation(summary = "Get employee payslips", description = "Retrieves all historical monthly payslips issued for a specific employee.")
    @ApiResponse(responseCode = "200", description = "Payslip history retrieved successfully")
    public ResponseEntity<List<PayslipResponseDTO>> getPayslipsByEmployee(
            @Parameter(description = "Database ID of the employee whose payslips are retrieved", required = true)
            @PathVariable Long employeeId) {
        List<PayslipResponseDTO> payslips = payrollService.getPayslipsByEmployee(employeeId);
        return ResponseEntity.ok(payslips);
    }

    @GetMapping("/payslips/period/{month}/{year}")
    @Operation(summary = "Get payslips by period", description = "Retrieves all payslips for a specific month/year.")
    @ApiResponse(responseCode = "200", description = "Payslips retrieved successfully")
    public ResponseEntity<List<PayslipResponseDTO>> getPayslipsByPeriod(
            @Parameter(description = "Month (1-12)", required = true)
            @PathVariable Integer month,
            @Parameter(description = "Year", required = true)
            @PathVariable Integer year) {
        List<PayslipResponseDTO> payslips = payrollService.getPayslipsByPeriod(month, year);
        return ResponseEntity.ok(payslips);
    }

    @GetMapping("/payslips/{id}")
    @Operation(summary = "Get single payslip", description = "Retrieves specific monthly payment details and breakdown by payslip ID.")
    @ApiResponse(responseCode = "200", description = "Payslip details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payslip not found")
    public ResponseEntity<PayslipResponseDTO> getPayslipById(
            @Parameter(description = "Database ID of the payslip record", required = true)
            @PathVariable Long id) {
        PayslipResponseDTO payslip = payrollService.getPayslipById(id);
        return ResponseEntity.ok(payslip);
    }
}
