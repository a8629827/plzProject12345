// UserService.java
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    // 회원가입
    UserDTO createUser(UserDTO userDto);

    // 특정 회원 조회
    UserDTO getUserById(Long userId);

    // 모든 회원 조회 (관리자용)
    List<UserDTO> getAllUsers();

    // 현재 로그인된 사용자 조회 (마이페이지)
    User getCurrentUser();

    // 회원 정보 수정 (마이페이지)
    void updateUser(Long userId, UserDTO userDto);

    // 회원 탈퇴 (사용자 삭제)
    void deleteUser(Long userId);

    // 관리자 회원 강퇴 기능
    void adminDeleteUser(Long userId);

    void save(User user);

    User findByUsername(String username);

//    public PageResponseDTO<PostDTO> writinglist(PageRequestDTO pageRequestDTO, User user);
}