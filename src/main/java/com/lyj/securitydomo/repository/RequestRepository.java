package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Request;
import com.lyj.securitydomo.dto.RequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {


    /**
     * 특정 게시물에 대한 요청 목록을 조회합니다.
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 대한 요청 목록
     */
    List<Request> findByPost_PostId(Long postId);

    /**
     * 특정 사용자가 신청한 요청 목록을 조회합니다.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자가 신청한 요청 목록
     */
    List<Request> findByUser_UserId(Long userId);
}