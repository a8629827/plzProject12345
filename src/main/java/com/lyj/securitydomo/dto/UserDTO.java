package com.lyj.securitydomo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {
    private Long userId;            // 아이디
    private String username;     // 이름
    private String password;     // 비밀번호 (보안상 저장/출력 시 주의)
    private String email;        // 이메일
    private String role;         // 권한
    private LocalDate birthDate; // 생년월일 (기본값 2000.11.12)
    private String gender;       // 성별
    private String city;         // 도시
    private String state;        // 주
    private LocalDate signupDate; // 가입 날짜
}