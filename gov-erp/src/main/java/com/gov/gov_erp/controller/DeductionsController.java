package com.gov.gov_erp.controller;

import com.gov.gov_erp.dto.DeductionsRequestDTO;
import com.gov.gov_erp.entity.Deductions;
import com.gov.gov_erp.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managers to view and adjust tax, pension, insurance,
 * and allowance percentages dynamically in the system.
 */
@RestController
@RequestMapping("/api/deductions")
@Tag(name = "Deductions Configuration", description = "Endpoints for managing global statutory tax, deduction, and allowance percentages.")
public class DeductionsController {

    private final PayrollService payrollService;

    public DeductionsController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    @Operation(summary = "Get current statutory rates", description = "Retrieves the current configured percentages for taxes, deductions, and allowances.")
    @ApiResponse(responseCode = "200", description = "Current rates retrieved successfully")
    public ResponseEntity<Deductions> getRates() {
        Deductions rates = payrollService.getRates();
        return ResponseEntity.ok(rates);
    }

    @PutMapping
    @Operation(summary = "Update statutory rates", description = "Modifies global percentages used for monthly salary calculations.")
    @ApiResponse(responseCode = "200", description = "Rates updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid validation parameters")
    public ResponseEntity<Deductions> updateRates(@Valid @RequestBody DeductionsRequestDTO request) {
        Deductions updated = payrollService.updateRates(request);
        return ResponseEntity.ok(updated);
    }
}
