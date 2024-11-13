package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class RequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_status_id")
    private Long requestStatusId;

    @Column(nullable = false,length = 50)
    private String status;
}