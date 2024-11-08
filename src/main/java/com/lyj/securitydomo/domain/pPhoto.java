package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Random;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class pPhoto implements Comparable<pPhoto> {

    @Id
    private String uuid;

    private String fileName;

    private int pno;

    // Post와의 연관관계 설정 - 다대일 관계로 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 썸네일 이미지 링크를 반환하는 메서드입니다.
     * UUID와 파일 이름이 존재할 경우 썸네일 이미지(s_ 접두사)를 반환하며, 그렇지 않으면 랜덤 이미지를 반환합니다.
     *
     * @return 썸네일 이미지 링크
     */
    public String getThumbnailLink() {
        return (uuid != null && fileName != null)
                ? "/view/s_" + uuid + "_" + fileName
                : getRandomImage();
    }

    /**
     * 원본 이미지 링크를 반환하는 메서드입니다.
     *
     * @return 원본 이미지 링크
     */
    public String getOriginalLink() {
        return (uuid != null && fileName != null)
                ? "/view/" + uuid + "_" + fileName
                : getRandomImage();
    }

    /**
     * 기본 랜덤 이미지를 반환하는 메서드입니다.
     * 업로드된 이미지가 없는 경우 호출됩니다.
     *
     * @return 기본 랜덤 이미지 링크
     */
    public static String getRandomImage() {
        String[] defaultImages = {
                "https://dummyimage.com/450x300/dee2e6/6c757d.jpg",
                "https://dummyimage.com/450x300/ced4da/495057.jpg",
                "https://dummyimage.com/450x300/e9ecef/adb5bd.jpg"
        };
        Random random = new Random();
        return defaultImages[random.nextInt(defaultImages.length)];
    }

    @Override
    public int compareTo(pPhoto other) {
        return Integer.compare(this.pno, other.pno);
    }

    public void changePost(Post post) {
        this.post = post;
    }
}