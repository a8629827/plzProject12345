package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    void createReport(ReportDTO reportDTO);
    List<Report> findAllReports(); // Method to fetch all reports
}