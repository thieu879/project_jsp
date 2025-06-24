package com.data.project.service.admin;

import com.data.project.entity.Application;
import com.data.project.entity.Progress;
import com.data.project.repository.admin.ApplicationRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {
    private ApplicationRepository applicationRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<Application> findAll(int page, int size) {
        return applicationRepository.findAll(page, size);
    }

    @Override
    public List<Application> findAllApplications(int page, int size) {
        return applicationRepository.findAllApplications(page, size);
    }

    @Override
    public Application findById(Long id) {
        return applicationRepository.findById(id);
    }

    @Override
    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public void updateStatus(Long id, Progress status) {
        applicationRepository.updateStatus(id, status);
    }

    @Override
    public List<Application> searchByName(String name, int page, int size) {
        return applicationRepository.searchByName(name, page, size);
    }

    @Override
    public long countAll() {
        return applicationRepository.countAll();
    }

    @Override
    public long countByName(String name) {
        return applicationRepository.countByName(name);
    }

    // Thêm các phương thức mới
    @Override
    public List<Application> findByStatus(Progress status, int page, int size) {
        return applicationRepository.findByStatus(status, page, size);
    }

    @Override
    public long countByStatus(Progress status) {
        return applicationRepository.countByStatus(status);
    }

    @Override
    public List<Application> searchByNameAndStatus(String name, Progress status, int page, int size) {
        return applicationRepository.searchByNameAndStatus(name, status, page, size);
    }

    @Override
    public long countByNameAndStatus(String name, Progress status) {
        return applicationRepository.countByNameAndStatus(name, status);
    }

    @Override
    public void updateApplication(Application application) {
        applicationRepository.updateApplication(application);
    }
    @Override
    public List<Application> findByCandidateId(Long candidateId, int page, int size) {
        return applicationRepository.findByCandidateId(candidateId, page, size);
    }

    @Override
    public long countByCandidateId(Long candidateId) {
        return applicationRepository.countByCandidateId(candidateId);
    }

}
