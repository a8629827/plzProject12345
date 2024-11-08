package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;

import java.util.List;

public interface PostService {

    // 게시글 등록 메서드
    Long register(PostDTO postDTO);

    // 게시글 조회 메서드
    PostDTO readOne(Long postId);

    // 게시글 수정 메서드
    void modify(PostDTO postDTO);

    // 게시글 삭제 메서드
    void remove(Long postId);

    // 게시글 목록 조회 메서드
    PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO);

}