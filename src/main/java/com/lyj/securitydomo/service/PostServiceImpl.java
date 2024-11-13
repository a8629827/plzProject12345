
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.domain.pPhoto;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Override
    public Long register(PostDTO postDTO, User user) {
        // 1. Post 엔티티 생성
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .contentText(postDTO.getContentText())
                .requiredParticipants(postDTO.getRequiredParticipants())
                .status(postDTO.getStatus() != null ? Post.Status.valueOf(postDTO.getStatus()) : null)
                .lat(postDTO.getLat()) // 위도 추가
                .lng(postDTO.getLng()) // 경도 추가
                .build();

        // 2. 파일 정보 추가 (파일이 존재하는 경우에만 추가)
        if (postDTO.getFileNames() != null && !postDTO.getFileNames().isEmpty()) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);
                }
            });
        } else {
            // 파일이 없을 경우 랜덤 이미지 추가
            String randomImage = pPhoto.getRandomImage(); // pPhoto에서 랜덤 이미지 가져오기
            post.addImage(randomImage, randomImage); // 랜덤 이미지를 파일 이름으로 사용
            log.info("파일이 없어서 랜덤 이미지를 사용합니다: {}", randomImage); // 로그 추가

        }
        // 3. Post 엔티티를 저장 (연관된 pPhoto 엔티티도 함께 저장됨)
        post.setUser(user);
        Long postId = postRepository.save(post).getPostId();
        log.info("게시글이 성공적으로 등록되었습니다. ID: {}", postId);

        return postId;
    }


    private final PostRepository postRepository;
    @Override
    public PostDTO readOne(Long postId) {
        // 현재 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN")); // "ADMIN" 권한 확인

        // 게시글을 조회 (관리자는 비공개 상태도 무시하고 조회)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // 게시글이 비공개 상태이고 관리자가 아니라면 예외 발생
        if (!post.isVisible() && !isAdmin) {
            throw new EntityNotFoundException("Post not found or post is invisible");
        }

        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contentText(post.getContentText())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpDatedAt())
                .fileNames(post.getImageSet().stream()
                        .map(image -> image.getUuid() + "_" + image.getFileName())
                        .collect(Collectors.toList()))
                .requiredParticipants(post.getRequiredParticipants())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .author(post.getUser() != null ? post.getUser().getUsername() : null)
                .build();
    }

    @Override
    public void modify(PostDTO postDTO) {
        Post post = postRepository.findById(postDTO.getPostId()).orElseThrow();

        post.change(postDTO.getTitle(), postDTO.getContentText());
        post.clearAllImages();

        if (postDTO.getFileNames() != null) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);
                }
            });
        }
        postRepository.save(post);
    }



    //삭제
    @Override
    public void remove(Long postId) {
        postRepository.deleteById(postId);
    }


    //페이징
    @Override
    public PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("postId");

        // isVisible이 true인 게시글만 조회
        Page<Post> result = postRepository.findByIsVisibleTrue(pageable);  // isVisible이 true인 게시글만 조회

        // 조회된 게시글 수 로그로 출력
        log.info("조회된 게시글 수: {}", result.getTotalElements());

        // 게시글 정보를 PostDTO로 변환하여 리스트에 담음
        List<PostDTO> dtoList = result.getContent().stream()
                .map(post -> PostDTO.builder()
                        .postId(post.getPostId()) // 게시글 ID
                        .title(post.getTitle()) // 제목
                        .contentText(post.getContentText()) // 본문 내용
                        .createdAt(post.getCreatedAt()) // 생성일
                        .updatedAt(post.getUpDatedAt()) // 수정일
                        .fileNames(post.getImageSet().stream() // 첨부된 파일들의 링크
                                .map(image -> image.getUuid() + "_" + image.getFileName())
                                .collect(Collectors.toList()))
                        .requiredParticipants(post.getRequiredParticipants()) // 모집 인원
                        .status(post.getStatus() != null ? post.getStatus().name() : null) // 모집 상태
                        .author(post.getUser() != null ? post.getUser().getUsername() : null) // 작성자 정보
                        .build()
                )
                .collect(Collectors.toList()); // PostDTO 리스트로 변환

        // 게시글 DTO 리스트와 함께 페이징 처리된 결과 반환
        log.info("게시글 DTO 리스트: {}", dtoList); // 반환되는 게시글 리스트 확인용 로그

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO) // 페이지 요청 정보
                .dtoList(dtoList) // 게시글 DTO 리스트
                .total((int) result.getTotalElements()) // 총 게시글 수
                .build(); // PageResponseDTO 반환
    }

    @Override
    public void markPostAsInvisible(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // 게시글을 비공개 처리 (isVisible을 false로 설정)
        post.makeInvisible();
        postRepository.save(post);  // 변경 사항 저장
    }


    @Override
    public PageResponseDTO<PostDTO> writinglist(PageRequestDTO pageRequestDTO, User user) {

        Pageable pageable = pageRequestDTO.getPageable("postId");

        // isVisible이 true인 게시글만 조회
        Page<Post> result = postRepository.findByUsername(user.getUsername(),pageable);  // isVisible이 true인 게시글만 조회

        // 조회된 게시글 수 로그로 출력
//        log.info("조회된 게시글 수: {}", result.getTotalElements());

        // 게시글 정보를 PostDTO로 변환하여 리스트에 담음
        List<PostDTO> dtoList = result.getContent().stream()
                .map(post -> PostDTO.builder()
                        .postId(post.getPostId()) // 게시글 ID
                        .title(post.getTitle()) // 제목
                        .contentText(post.getContentText()) // 본문 내용
                        .createdAt(post.getCreatedAt()) // 생성일
                        .updatedAt(post.getUpDatedAt()) // 수정일
                        .fileNames(post.getImageSet().stream() // 첨부된 파일들의 링크
                                .map(image -> image.getUuid() + "_" + image.getFileName())
                                .collect(Collectors.toList()))
                        .requiredParticipants(post.getRequiredParticipants()) // 모집 인원
                        .status(post.getStatus() != null ? post.getStatus().name() : null) // 모집 상태
                        .author(post.getUser() != null ? post.getUser().getUsername() : null) // 작성자 정보
                        .build()
                )
                .collect(Collectors.toList()); // PostDTO 리스트로 변환

        // 게시글 DTO 리스트와 함께 페이징 처리된 결과 반환
//        log.info("게시글 DTO 리스트: {}", dtoList); // 반환되는 게시글 리스트 확인용 로그

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO) // 페이지 요청 정보
                .dtoList(dtoList) // 게시글 DTO 리스트
                .total((int) result.getTotalElements()) // 총 게시글 수
                .build(); // PageResponseDTO 반환
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return List.of();
    }

    @Override
    public PostDTO getPostById(Long postId) {
        return null;
    }

    @Override
    public void deletePost(Long postId) {

    }

//    @Override
//    public List<PostDTO> getAllPosts() {
//        return postRepository.findAll().stream()
//                .map(post -> new PostDTO(post.getpostId(), post.getTitle(), post.getThumbnail(), post.getCreatedAt(), post.getStatus(), post.getRequiredParticipants()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public PostDTO getPostById(Long postId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + postId));
//        return new PostDTO(post.getPostId(), post.getTitle(), post.getThumbnail(), post.getCreatedAt(), post.getStatus(), post.getRequiredParticipants(), post.getAuthor(), post.getContentText());
//    }
//
//    @Override
//    public void deletePost(Long postId) {
//        if (!postRepository.existsById(postId)) {
//            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + postId);
//        }
//        postRepository.deleteById(postId);
//    }


}