package com.lyj.securitydomo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;


@Data
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="user_id",nullable = false) // 외래키 매핑
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="post_id",nullable = false) // 외래키 매핑
    private Post post;

    private String title;

    @Column(nullable = false,length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "request_status_id",nullable = false) // 외래키 매핑
    private RequestStatus requestStatus;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern ="yyyy-MM-dd H:mm:ss")
    private Date regDate;





}