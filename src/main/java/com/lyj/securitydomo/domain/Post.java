package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "imageSet")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId; // 게시글 ID 자동 생성

    private String title; // 제목

    private String contentText; // 게시글 본문 내용 저장

    // 모집 인원 필드
    private Integer requiredParticipants; // 모집 인원

    // 모집 상태 필드
    @Enumerated(EnumType.STRING)
    private Status status; // 모집 상태 (모집중 또는 모집완료)

    public enum Status {
        모집중, 모집완료
    }
    private double lat;//위도
    private double lng;//경도

    @Builder.Default
    // 비공개 처리 상태 (기본값: true)
    private boolean isVisible = true; // 기본값은 true (게시글이 공개 상태로 설정)


    // 게시글 제목과 내용을 변경하는 메서드
    public void change(String title, String contentText) {
        this.title = title;
        this.contentText = contentText;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "post_applicants",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> applicants = new HashSet<>(); //신청자 정보를 담는 필드

    @OneToMany(mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<pPhoto> imageSet = Collections.synchronizedSet(new HashSet<>());

    public String getThumbnail() {
        String thumbnailLink = (imageSet != null && !imageSet.isEmpty())
                ? imageSet.iterator().next().getThumbnailLink() // 썸네일 링크를 가져옴
                : pPhoto.getRandomImage();
        return thumbnailLink;


    }

    public List<String> getOriginalImageLinks() {
        return imageSet.stream()
                .sorted()
                .map(pPhoto::getOriginalLink) // 각 pPhoto의 원본 링크를 가져옴
                .collect(Collectors.toList());
    }

    // 이미지 추가 메서드
    public void addImage(String uuid, String fileName) {
        pPhoto image = pPhoto.builder()
                .uuid(uuid)
                .fileName(fileName)
                .post(this)
                .pno(imageSet.size())
                .build();
        imageSet.add(image);
    }

    // 모든 이미지를 제거하는 메서드
    public void clearAllImages() {
        imageSet.forEach(pPhoto -> pPhoto.changePost(null));
        this.imageSet.clear();
    }

    // 비공개 처리 메서드
    //글을 비공개로 설정하고 싶을 때 사용
    public void setInvisible() {
        this.isVisible = false;
    }


    //공개/비공개 상태를 설정하고 싶을 때 사용.
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    //현재 공개 상태를 확인할 때 사용.
    public boolean isVisible() {
        return isVisible;
    }


}