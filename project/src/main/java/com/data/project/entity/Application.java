package com.data.project.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long candidateId;
    private long recruitmentPositionId;
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String cvUrl;
    @Column(nullable = false, columnDefinition = "default 'PENDING'")
    private Progress progress;
    @Column(nullable = true)
    private LocalDateTime interviewRequestDate;
    @Column(nullable = true)
    private String interviewRequestResult;
    @Column(nullable = true)
    private String interviewLink;
    @Column(nullable = true)
    private LocalDateTime interviewTime;
    @Column(nullable = true)
    private String interviewResult;
    @Column(nullable = true)
    private String interviewResultNote;
    @Column(nullable = true)
    private LocalDateTime destroyAt;
    @Column(columnDefinition = "default current_timestamp")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "on update current_timestamp")
    private LocalDateTime updatedAt;
    @Column(nullable = true, columnDefinition = "text")
    private String destroyReason;
}
