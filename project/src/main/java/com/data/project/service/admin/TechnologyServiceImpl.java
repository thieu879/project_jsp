package com.data.project.service.admin;

import com.data.project.entity.Technology;
import com.data.project.repository.admin.TechnologyRepository;
import com.data.project.repository.admin.TechnologyRepositoryImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TechnologyServiceImpl implements TechnologyService {
    private TechnologyRepository technologyRepository;

    public TechnologyServiceImpl(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    @Override
    public List<Technology> findAll(int page, int size) {
        return technologyRepository.findAll(page, size);
    }

    @Override
    public Technology findById(Long id) {
        return technologyRepository.findById(id);
    }

    @Override
    public Technology save(Technology technology) {
        return technologyRepository.save(technology);
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        technologyRepository.updateStatus(id, status);
    }

    @Override
    public void updateTechnology(Technology technology) {
        technologyRepository.updateTechnology(technology);
    }

    @Override
    public List<Technology> searchByName(String name, int page, int size) {
        return technologyRepository.searchByName(name, page, size);
    }

    @Override
    public long countAll() {
        return ((TechnologyRepositoryImpl) technologyRepository).countAll();
    }

    @Override
    public long countByName(String name) {
        return ((TechnologyRepositoryImpl) technologyRepository).countByName(name);
    }
}

