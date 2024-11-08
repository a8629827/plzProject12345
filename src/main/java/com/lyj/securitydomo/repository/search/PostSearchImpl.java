
package com.lyj.securitydomo.repository.search;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {
    public PostSearchImpl() {super(Post.class);}
    @Override
    public Page<Post> searchAll(String[] types, String keyword, Pageable pageable) {
        QPost post = QPost.post;
        JPQLQuery<Post> query = from(post);

        if((types != null) && (types.length > 0) && keyword != null) {
            BooleanBuilder builder = new BooleanBuilder();
            for(String type : types) {
                switch (type) {
                    case "t":
                        builder.and(post.title.contains(keyword));
                        break;
                    case "c":
                        builder.and(post.contentText.contains(keyword));
                        break;
                }
            }
            query.where(builder);
        }
        query.where(post.postId.gt(0L));
        this.getQuerydsl().applyPagination(pageable, query);
        List<Post> list = query.fetch();
        long total = query.fetchCount();
        return new PageImpl<>(list, pageable, total);
    }
}

