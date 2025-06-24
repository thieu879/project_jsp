package com.data.project.service.admin;

import com.data.project.entity.RecruitmentPosition;

import java.util.List;

public interface RecruitmentPositionService {
    List<RecruitmentPosition> findAll(int page, int size);
    RecruitmentPosition findById(Long id);
    RecruitmentPosition save(RecruitmentPosition recruitmentPosition);
    void updateStatus(Long id, boolean status);
    void updateRecruitmentPosition(RecruitmentPosition recruitmentPosition);
    List<RecruitmentPosition> searchByName(String name, int page, int size);
    long countAll();
    long countByName(String name);
}
