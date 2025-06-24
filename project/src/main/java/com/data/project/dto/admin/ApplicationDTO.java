package com.data.project.dto.admin;

import com.data.project.entity.Progress;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class ApplicationDTO {

    private Integer id;

    @NotNull(message = "Candidate ID không được để trống")
    @Positive(message = "Candidate ID phải là số dương")
    private Long candidateId;

    @NotNull(message = "Recruitment Position ID không được để trống")
    @Positive(message = "Recruitment Position ID phải là số dương")
    private Long recruitmentPositionId;

    @NotBlank(message = "CV URL không được để trống")
    @Size(max = 255, message = "CV URL không được vượt quá 255 ký tự")
    @Pattern(regexp = "^(https?://).*", message = "CV URL phải là URL hợp lệ")
    private String cvUrl;

    @NotNull(message = "Progress không được để trống")
    private Progress progress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewRequestDate;

    @Size(max = 500, message = "Kết quả yêu cầu phỏng vấn không được vượt quá 500 ký tự")
    private String interviewRequestResult;

    @Size(max = 255, message = "Link phỏng vấn không được vượt quá 255 ký tự")
    @Pattern(regexp = "^(https?://).*", message = "Link phỏng vấn phải là URL hợp lệ",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String interviewLink;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Thời gian phỏng vấn phải trong tương lai")
    private LocalDateTime interviewTime;

    @Size(max = 500, message = "Kết quả phỏng vấn không được vượt quá 500 ký tự")
    private String interviewResult;

    @Size(max = 1000, message = "Ghi chú kết quả phỏng vấn không được vượt quá 1000 ký tự")
    private String interviewResultNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime destroyAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Size(max = 2000, message = "Lý do hủy không được vượt quá 2000 ký tự")
    private String destroyReason;

    public ApplicationDTO() {}

    public ApplicationDTO(Long candidateId, Long recruitmentPositionId, String cvUrl, Progress progress) {
        this.candidateId = candidateId;
        this.recruitmentPositionId = recruitmentPositionId;
        this.cvUrl = cvUrl;
        this.progress = progress;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public Long getRecruitmentPositionId() { return recruitmentPositionId; }
    public void setRecruitmentPositionId(Long recruitmentPositionId) {
        this.recruitmentPositionId = recruitmentPositionId;
    }

    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }

    public Progress getProgress() { return progress; }
    public void setProgress(Progress progress) { this.progress = progress; }

    public LocalDateTime getInterviewRequestDate() { return interviewRequestDate; }
    public void setInterviewRequestDate(LocalDateTime interviewRequestDate) {
        this.interviewRequestDate = interviewRequestDate;
    }

    public String getInterviewRequestResult() { return interviewRequestResult; }
    public void setInterviewRequestResult(String interviewRequestResult) {
        this.interviewRequestResult = interviewRequestResult;
    }

    public String getInterviewLink() { return interviewLink; }
    public void setInterviewLink(String interviewLink) { this.interviewLink = interviewLink; }

    public LocalDateTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalDateTime interviewTime) { this.interviewTime = interviewTime; }

    public String getInterviewResult() { return interviewResult; }
    public void setInterviewResult(String interviewResult) { this.interviewResult = interviewResult; }

    public String getInterviewResultNote() { return interviewResultNote; }
    public void setInterviewResultNote(String interviewResultNote) {
        this.interviewResultNote = interviewResultNote;
    }

    public LocalDateTime getDestroyAt() { return destroyAt; }
    public void setDestroyAt(LocalDateTime destroyAt) { this.destroyAt = destroyAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getDestroyReason() { return destroyReason; }
    public void setDestroyReason(String destroyReason) { this.destroyReason = destroyReason; }
}

