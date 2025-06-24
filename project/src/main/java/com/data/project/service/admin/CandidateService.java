package com.data.project.service.admin;

import com.data.project.entity.Candidate;

import java.util.List;
import java.util.Map;

public interface CandidateService {
    List<Candidate> findAll(int page, int size);
    Candidate findById(Long id);
    Candidate save(Candidate candidate);
    void updateStatus(Long id, boolean status);
    List<Candidate> searchByName(String name, int page, int size);
    long countAll();
    long countSearchByName(String name);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    List<Candidate> findWithFilters(Map<String, Object> filters, int page, int size);
    long countWithFilters(Map<String, Object> filters);
}
