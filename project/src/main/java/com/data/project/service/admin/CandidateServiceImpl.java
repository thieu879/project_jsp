package com.data.project.service.admin;

import com.data.project.entity.Candidate;
import com.data.project.repository.admin.CandidateRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CandidateServiceImpl implements CandidateService {
    private CandidateRepository candidateRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public List<Candidate> findAll(int page, int size) {
        return candidateRepository.findAll(page, size);
    }

    @Override
    public Candidate findById(Long id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        candidateRepository.updateStatus(id, status);
    }

    @Override
    public List<Candidate> searchByName(String name, int page, int size) {
        return candidateRepository.searchByName(name, page, size);
    }

    @Override
    public long countAll() {
        return candidateRepository.countAll();
    }

    @Override
    public long countSearchByName(String name) {
        return candidateRepository.countSearchByName(name);
    }

    @Override
    public boolean existsByEmail(String email) {
        return candidateRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return candidateRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public List<Candidate> findWithFilters(Map<String, Object> filters, int page, int size) {
        return candidateRepository.findWithFilters(filters, page, size);
    }

    @Override
    public long countWithFilters(Map<String, Object> filters) {
        return candidateRepository.countWithFilters(filters);
    }
}
