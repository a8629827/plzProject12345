package com.lyj.securitydomo.config;

import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.dto.ReportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Report -> ReportDTO 매핑
        modelMapper.createTypeMap(Report.class, ReportDTO.class)
                .addMapping(report -> report.getPost().getPostId(), ReportDTO::setPostId); // Report의 Post 객체에서 postId를 가져와 ReportDTO에 설정

        return modelMapper;
    }
}