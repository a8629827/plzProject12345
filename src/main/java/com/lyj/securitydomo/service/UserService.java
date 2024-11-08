package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {
    UserDTO createUser(UserDTO userDto);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();

    void save(User user);

    User getCurrentUser();

    void updateUser(User user);
}
