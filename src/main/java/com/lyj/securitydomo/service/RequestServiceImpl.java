package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Request;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.RequestDTO;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.RequestRepository;
import com.lyj.securitydomo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RequestServiceImpl는 RequestService 인터페이스를 구현하며,
 * 요청 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 모든 요청을 조회하여 RequestDTO 리스트로 반환합니다.
     * @return 모든 요청 목록 (RequestDTO 리스트)
     */
    @Override
    public List<RequestDTO> getRequests() {
        return requestRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Request 객체를 저장합니다.
     * @param request 저장할 Request 객체
     */
    @Override
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    /**
     * 게시물 ID, 제목, 내용으로 새로운 요청을 생성하여 저장합니다.
     * 현재 로그인한 사용자의 정보를 요청에 포함합니다.
     * @param postId 게시물 ID
     * @param title 요청 제목
     * @param content 요청 내용
     */
    @Override
    public void saveRequest(long postId, String title, String content) {
        User user = userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        Request request = new Request();
        request.setUser(user);
        request.setPost(post);
        request.setTitle(title);
        request.setContent(content);

        requestRepository.save(request);
    }

    /**
     * 특정 게시물에 대한 요청 목록을 조회하여 RequestDTO 리스트로 반환합니다.
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 대한 요청 목록 (RequestDTO 리스트)
     */
    @Override
    public List<RequestDTO> getRequestsByPostId(Long postId) {
        return requestRepository.findByPost_PostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 신청한 요청 목록을 조회하여 RequestDTO 리스트로 반환합니다.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자가 신청한 요청 목록 (RequestDTO 리스트)
     */
    @Override
    public List<RequestDTO> getRequestsByUserId(Long userId) {
        return requestRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 요청을 삭제합니다.
     * 요청이 존재하지 않으면 IllegalArgumentException을 발생시킵니다.
     * @param requestId 삭제할 요청의 ID
     */
    @Override
    public void deleteRequest(Long requestId) {
        if (requestRepository.existsById(requestId)) {
            requestRepository.deleteById(requestId);
        } else {
            throw new IllegalArgumentException("해당 요청이 존재하지 않습니다.");
        }
    }

    /**
     * Request 객체를 RequestDTO로 변환합니다.
     * Request에 포함된 User와 Post의 정보를 RequestDTO에 매핑합니다.
     * @param request 변환할 Request 객체
     * @return 변환된 RequestDTO 객체
     */
    private RequestDTO convertToDTO(Request request) {
        return new RequestDTO(
                request.getRequestId(),
                request.getPost().getPostId(),
                request.getUser().getUserId(),
                request.getTitle(),
                request.getContent(),
                request.getUser().getUsername(),
                request.getContent(),
                request.getRegDate(),
                request.getRequestStatus().getRequestStatusId(),
                request.getRequestStatus().getStatus(),
                request.getPost().getUser().getUsername(),
                request.getPost().getStatus().toString()
        );
    }

    /**
     * 현재 로그인한 사용자의 이름을 반환합니다.
     * @return 현재 사용자 이름 (Authentication에서 가져옴)
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }
}