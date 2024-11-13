
package com.lyj.securitydomo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Long postId;
    private String title;
    private String content;
    private String username;
    private String contentText;

    public RequestDTO(Long requestId, Long postId, Long userId, String title, String content, String username, String contentText, Date regDate, Long requestStatusId, String status, String author, String postStatus) {
        // 필드 초기화
    }

}