package com.data.project.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "technology")
public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, columnDefinition = "varchar(100)")
    private String name;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean status = true;
    @ManyToMany(mappedBy = "technologies")
    private Set<Candidate> candidates;

    @ManyToMany(mappedBy = "technologies")
    private Set<Recruitment_position> recruitmentPositions;

    public Technology() {}

    public Technology(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(Set<Candidate> candidates) {
        this.candidates = candidates;
    }

    public Set<Recruitment_position> getRecruitmentPositions() {
        return recruitmentPositions;
    }

    public void setRecruitmentPositions(Set<Recruitment_position> recruitmentPositions) {
        this.recruitmentPositions = recruitmentPositions;
    }
}
