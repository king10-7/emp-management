package com.gov.gov_erp.controller;

import com.gov.gov_erp.dto.MessageResponseDTO;
import com.gov.gov_erp.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<MessageResponseDTO>> getMessagesByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(messageService.getMessagesByEmployeeId(employeeId));
    }

    @GetMapping("/period/{month}/{year}")
    public ResponseEntity<List<MessageResponseDTO>> getMessagesByPeriod(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(messageService.getMessagesByPeriod(month, year));
    }
}
