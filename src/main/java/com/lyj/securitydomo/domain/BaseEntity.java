package com.lyj.securitydomo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@MappedSuperclass
@EntityListeners(value = {AbstractMethodError.class})
@Getter

abstract class BaseEntity {
    @CreatedDate
    @Column(name = "createdAt",updatable = false)
    private Date createdAt; //등록날짜

    @LastModifiedDate
    @Column(name = "upDatedAt")
    private Date upDatedAt; //수정날짜
}