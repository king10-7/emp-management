package com.gov.gov_erp.repository;

import com.gov.gov_erp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByEmployeeId(Long employeeId);
    List<Message> findByMonthAndYear(Integer month, Integer year);
}
