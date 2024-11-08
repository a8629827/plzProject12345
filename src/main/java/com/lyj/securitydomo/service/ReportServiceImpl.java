
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Date;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    @Qualifier("conversionServicePostProcessor")
    @Autowired
    private BeanFactoryPostProcessor conversionServicePostProcessor;


    @Override
    public void createReport(ReportDTO reportDTO) {
        logger.info("신고 생성 요청: postId={},  category={}, reason={}",
                reportDTO.getPostId(), reportDTO.getCategory(), reportDTO.getReason());

        Post post = postRepository.findById(Long.valueOf(reportDTO.getPostId()))
                .orElseThrow(() -> {
                    logger.error("해당 게시글을 찾을 수 없습니다: postId={}", reportDTO.getPostId());
                    return new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
                });


        // 열거형으로 변환하여 설정하고 유효하지 않은 경우 예외 처리
        Report.ReportCategory reportCategory;
        try {
            reportCategory = Report.ReportCategory.valueOf(reportDTO.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("유효하지 않은 신고 유형: category={}", reportDTO.getCategory());
            throw new IllegalArgumentException("유효하지 않은 신고 유형입니다.");
        }

        Report report = Report.builder()
                .post(post)
                .category(reportCategory)
                .reason(reportDTO.getReason())
                .status(Report.ReportStatus.PENDING)
                .createdAt(new Date())
                .build();

        reportRepository.save(report);
        logger.info("신고가 데이터베이스에 저장되었습니다: {}", report);
    }



    @Override
    public List<Report> findAllReports() {
        return reportRepository.findAll(); // 모든 신고를 가져옴
    }
}
