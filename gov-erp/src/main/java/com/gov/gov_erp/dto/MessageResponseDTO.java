package com.gov.gov_erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String content;
    private Integer month;
    private Integer year;
    private LocalDateTime createdAt;
}
