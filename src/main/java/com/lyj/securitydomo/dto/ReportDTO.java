package com.lyj.securitydomo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long reportId;
    private Long postId; // Post와의 관계
    private String reason; // 신고 사유
    private String status; // 신고 진행 상태
    private String category; // 신고 분류
    private Date createdAt; // 생성 날짜
}