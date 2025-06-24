package com.data.project.service.admin;

import com.data.project.entity.Application;
import com.data.project.entity.Progress;

import java.util.List;

public interface ApplicationService {
    List<Application> findAll(int page, int size);
    List<Application> findAllApplications(int page, int size);
    Application findById(Long id);
    Application save(Application application);
    void updateStatus(Long id, Progress status);
    List<Application> searchByName(String name, int page, int size);
    long countAll();
    long countByName(String name);
    List<Application> findByStatus(Progress status, int page, int size);
    long countByStatus(Progress status);
    List<Application> searchByNameAndStatus(String name, Progress status, int page, int size);
    long countByNameAndStatus(String name, Progress status);
    void updateApplication(Application application);
    List<Application> findByCandidateId(Long candidateId, int page, int size);
    long countByCandidateId(Long candidateId);

}
