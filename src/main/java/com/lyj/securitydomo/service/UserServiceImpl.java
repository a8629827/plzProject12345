package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.UserDTO;
import com.lyj.securitydomo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElse(null);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
      @Autowired
    public void UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        // User를 저장하고 Address도 함께 저장될 수 있도록 한다
        userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public void updateUser(User user) {

    }
}
