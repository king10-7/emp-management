package com.gov.gov_erp.repository;

import com.gov.gov_erp.entity.Deductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeductionsRepository extends JpaRepository<Deductions, Long> {
}
