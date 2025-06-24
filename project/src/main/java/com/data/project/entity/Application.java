package com.data.project.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "candidate_id", nullable = false)
    private long candidateId;

    @Column(name = "recruitment_position_id", nullable = false)
    private long recruitmentPositionId;

    @Column(name = "cv_url", nullable = false, length = 255)
    private String cvUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress", nullable = false)
    private Progress progress = Progress.PENDING;

    @Column(name = "interview_request_date")
    private LocalDateTime interviewRequestDate;

    @Column(name = "interview_request_result", length = 50)
    private String interviewRequestResult;

    @Column(name = "interview_link", length = 500)
    private String interviewLink;

    @Column(name = "interview_time")
    private LocalDateTime interviewTime;

    @Column(name = "interview_result", length = 50)
    private String interviewResult;

    @Column(name = "interview_result_note", columnDefinition = "TEXT")
    private String interviewResultNote;

    @Column(name = "destroy_at")
    private LocalDateTime destroyAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "destroy_reason", columnDefinition = "TEXT")
    private String destroyReason;

    @Transient
    private String candidateName;

    @Transient
    private String recruitmentName;
    @Transient
    private Set<Technology> technologies;

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }


    public Application() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.progress = Progress.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }

    public long getRecruitmentPositionId() { return recruitmentPositionId; }
    public void setRecruitmentPositionId(long recruitmentPositionId) { this.recruitmentPositionId = recruitmentPositionId; }

    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }

    public Progress getProgress() { return progress; }
    public void setProgress(Progress progress) { this.progress = progress; }

    public LocalDateTime getInterviewRequestDate() { return interviewRequestDate; }
    public void setInterviewRequestDate(LocalDateTime interviewRequestDate) { this.interviewRequestDate = interviewRequestDate; }

    public String getInterviewRequestResult() { return interviewRequestResult; }
    public void setInterviewRequestResult(String interviewRequestResult) { this.interviewRequestResult = interviewRequestResult; }

    public String getInterviewLink() { return interviewLink; }
    public void setInterviewLink(String interviewLink) { this.interviewLink = interviewLink; }

    public LocalDateTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalDateTime interviewTime) { this.interviewTime = interviewTime; }

    public String getInterviewResult() { return interviewResult; }
    public void setInterviewResult(String interviewResult) { this.interviewResult = interviewResult; }

    public String getInterviewResultNote() { return interviewResultNote; }
    public void setInterviewResultNote(String interviewResultNote) { this.interviewResultNote = interviewResultNote; }

    public LocalDateTime getDestroyAt() { return destroyAt; }
    public void setDestroyAt(LocalDateTime destroyAt) { this.destroyAt = destroyAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getDestroyReason() { return destroyReason; }
    public void setDestroyReason(String destroyReason) { this.destroyReason = destroyReason; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getRecruitmentName() { return recruitmentName; }
    public void setRecruitmentName(String recruitmentName) { this.recruitmentName = recruitmentName; }
}
