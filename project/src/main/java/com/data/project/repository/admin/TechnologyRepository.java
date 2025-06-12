package com.data.project.repository.admin;

import com.data.project.entity.Technology;

import java.util.List;

public interface TechnologyRepository {
    List<Technology> findAll(int page, int size);
    Technology findById(Long id);
    Technology save(Technology technology);
    void updateStatus(Long id, boolean status);
    void updateTechnology(Technology technology);
    List<Technology> searchByName(String name, int page, int size);
}
