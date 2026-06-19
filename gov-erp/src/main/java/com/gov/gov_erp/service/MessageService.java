package com.gov.gov_erp.service;

import com.gov.gov_erp.dto.MessageResponseDTO;
import com.gov.gov_erp.entity.Message;
import com.gov.gov_erp.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final com.gov.gov_erp.repository.EmployeeRepository employeeRepository;

    public MessageService(MessageRepository messageRepository,
                          com.gov.gov_erp.repository.EmployeeRepository employeeRepository) {
        this.messageRepository = messageRepository;
        this.employeeRepository = employeeRepository;
    }

    // Temporary method for testing
    @Transactional
    public MessageResponseDTO createTestMessage(Long employeeId, Integer month, Integer year) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new com.gov.gov_erp.exception.ResourceNotFoundException("Employee not found"));
        var message = Message.builder()
                .employee(employee)
                .content("Dear " + employee.getFirstName() + " Test message")
                .month(month)
                .year(year)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        message = messageRepository.save(message);
        return convertToDTO(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessagesByEmployeeId(Long employeeId) {
        return messageRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessagesByPeriod(Integer month, Integer year) {
        return messageRepository.findByMonthAndYear(month, year).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MessageResponseDTO convertToDTO(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .employeeId(message.getEmployee().getId())
                .employeeName(message.getEmployee().getFirstName() + " " + message.getEmployee().getLastName())
                .content(message.getContent())
                .month(message.getMonth())
                .year(message.getYear())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
