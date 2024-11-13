package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.RequestStatus;
import com.lyj.securitydomo.repository.RequestStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestStatusService {
    public final RequestStatusRepository requestStatusRepository;

    // 모든 상태 조회
    public List<RequestStatus> findAll() {
        return requestStatusRepository.findAll();
    }

    // 특정 상태 조회
    public Optional<RequestStatus> findById(Long id) {
        return requestStatusRepository.findById(id);
    }

    // 상태 생성
    public RequestStatus create(RequestStatus requestStatus) {
        return requestStatusRepository.save(requestStatus);
    }

    // 상태 업데이트
    public RequestStatus update(Long id, RequestStatus requestStatus) {
        RequestStatus existingStatus = requestStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RequestStatus not found with id: " + id));

        existingStatus.setStatus(requestStatus.getStatus());
        return requestStatusRepository.save(existingStatus);
    }

    // 상태 삭제
    public void delete(Long id) {
        requestStatusRepository.deleteById(id);
    }

}