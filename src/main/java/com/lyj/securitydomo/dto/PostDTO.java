package com.lyj.securitydomo.dto;

import com.lyj.securitydomo.dto.upload.UploadResultDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private Long postId; // 게시글 ID 자동 생성

    @NotEmpty
    @Size(min = 3, max = 200)
    private String title; // 제목

    @NotEmpty
    private String contentText; // 게시글 본문 내용 저장

    private Date createdAt; // 등록 날짜

    private Date updatedAt; // 수정 날짜

    private List<String> fileNames; // 파일 이름 리스트 (썸네일 및 원본 파일 이름을 저장)

    private Integer requiredParticipants; // 모집 인원

    private String status; // 모집 상태 (모집중 또는 모집완료)

    private String author; // 작성자 정보

    private List<String> originalImageLinks;

    private double lat;

    private double lng;

    @Builder.Default
    private boolean isVisible = true; // 기본값은 true (사용자에게 보임)
    /**
     * 썸네일 이미지 링크를 가져오는 메서드입니다.
     * 업로드된 이미지가 없으면 랜덤 이미지를 반환합니다.
     *
     * @return 이미지 링크
     */
    public String getThumbnail() {
        // fileNames가 null이거나 비어있지 않다면 첫 번째 이미지(썸네일) 링크를 반환
        if (fileNames != null && !fileNames.isEmpty()) {
            return "/view/s_" + fileNames.get(0);
        } else {
            return UploadResultDTO.getRandomImage();
        }
    }
    public List<String> getOriginalImageLinks() {
        // 필요하다면 파일 링크 로직을 작성해 추가합니다.
        return this.originalImageLinks;
    }

}