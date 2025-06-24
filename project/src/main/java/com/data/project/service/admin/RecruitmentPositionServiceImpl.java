package com.data.project.service.admin;

import com.data.project.entity.RecruitmentPosition;
import com.data.project.repository.admin.RecruitmentPositionRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RecruitmentPositionServiceImpl implements RecruitmentPositionService {
    private RecruitmentPositionRepository recruitmentPositionRepository;
    public RecruitmentPositionServiceImpl(RecruitmentPositionRepository recruitmentPositionRepository) {
        this.recruitmentPositionRepository = recruitmentPositionRepository;
    }
    @Override
    public List<RecruitmentPosition> findAll(int page, int size) {
        return recruitmentPositionRepository.findAll(page, size);
    }

    @Override
    public RecruitmentPosition findById(Long id) {
        return recruitmentPositionRepository.findById(id);
    }

    @Override
    public RecruitmentPosition save(RecruitmentPosition recruitmentPosition) {
        return recruitmentPositionRepository.save(recruitmentPosition);
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        recruitmentPositionRepository.updateStatus(id, status);
    }

    @Override
    public void updateRecruitmentPosition(RecruitmentPosition recruitmentPosition) {
        recruitmentPositionRepository.updateRecruitmentPosition(recruitmentPosition);
    }

    @Override
    public List<RecruitmentPosition> searchByName(String name, int page, int size) {
        return recruitmentPositionRepository.searchByName(name, page, size);
    }

    @Override
    public long countAll() {
        return recruitmentPositionRepository.countAll();
    }

    @Override
    public long countByName(String name) {
        return recruitmentPositionRepository.countByName(name);
    }
}
