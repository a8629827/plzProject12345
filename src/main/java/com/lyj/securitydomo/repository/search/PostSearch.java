package com.lyj.securitydomo.repository.search;

import com.lyj.securitydomo.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearch {
    Page<Post> searchAll (String[] types, String keyword, Pageable pageable);
}