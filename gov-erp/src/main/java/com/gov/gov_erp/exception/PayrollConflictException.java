package com.gov.gov_erp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to run payroll twice for the same period.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PayrollConflictException extends RuntimeException {
    public PayrollConflictException(String message) {
        super(message);
    }
}
