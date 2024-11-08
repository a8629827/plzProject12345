package com.lyj.securitydomo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private String name;
    private String username;
    private String password;
    private LocalDate birthDate;
    private String gender;
    private String role;
    private String email;
    private String city;
    private String state;
}
