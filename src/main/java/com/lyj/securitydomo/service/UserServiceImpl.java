// UserServiceImpl.java
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.UserDTO;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        user.setEmail(userDTO.getEmail());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setBirthDate(userDTO.getBirthDate());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 관리자에 의한 회원 강퇴 기능
     * @param userId 강퇴할 사용자 ID
     */
    @Override
    public void adminDeleteUser(Long userId) {
        deleteUser(userId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }

//    @Override
//    public PageResponseDTO<PostDTO> writinglist(PageRequestDTO pageRequestDTO, User user) {
//
//        Pageable pageable = pageRequestDTO.getPageable("postId");
//
//        // isVisible이 true인 게시글만 조회
//        Page<Post> result = postRepository.findByUsername(user.getUsername(),pageable);  // isVisible이 true인 게시글만 조회
//
//        // 조회된 게시글 수 로그로 출력
////        log.info("조회된 게시글 수: {}", result.getTotalElements());
//
//        // 게시글 정보를 PostDTO로 변환하여 리스트에 담음
//        List<PostDTO> dtoList = result.getContent().stream()
//                .map(post -> PostDTO.builder()
//                        .postId(post.getPostId()) // 게시글 ID
//                        .title(post.getTitle()) // 제목
//                        .contentText(post.getContentText()) // 본문 내용
//                        .createdAt(post.getCreatedAt()) // 생성일
//                        .updatedAt(post.getUpDatedAt()) // 수정일
//                        .fileNames(post.getImageSet().stream() // 첨부된 파일들의 링크
//                                .map(image -> image.getUuid() + "_" + image.getFileName())
//                                .collect(Collectors.toList()))
//                        .requiredParticipants(post.getRequiredParticipants()) // 모집 인원
//                        .status(post.getStatus() != null ? post.getStatus().name() : null) // 모집 상태
//                        .author(post.getUser() != null ? post.getUser().getUsername() : null) // 작성자 정보
//                        .build()
//                )
//                .collect(Collectors.toList()); // PostDTO 리스트로 변환
//
//        // 게시글 DTO 리스트와 함께 페이징 처리된 결과 반환
////        log.info("게시글 DTO 리스트: {}", dtoList); // 반환되는 게시글 리스트 확인용 로그
//
//        return PageResponseDTO.<PostDTO>withAll()
//                .pageRequestDTO(pageRequestDTO) // 페이지 요청 정보
//                .dtoList(dtoList) // 게시글 DTO 리스트
//                .total((int) result.getTotalElements()) // 총 게시글 수
//                .build(); // PageResponseDTO 반환
//    }
}