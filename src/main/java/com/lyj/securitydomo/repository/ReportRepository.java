package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}