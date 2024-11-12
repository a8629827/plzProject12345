package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // username으로 사용자 조회
    Optional<User> findByUsername(String username);

    // 모든 사용자 조회(관리자)
    List<User> findAll();

    // 특정 사용자 삭제
    void deleteById(Long userId);
}