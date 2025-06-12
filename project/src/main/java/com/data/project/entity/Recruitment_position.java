package com.data.project.entity;

import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "recruitment_position")
@Check(constraints = "minSalary >= 0 AND maxSalary >= minSalary")
public class Recruitment_position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String name;
    @Column(nullable = true, columnDefinition = "varchar(100)")
    private String description;
    @Column(nullable = false)
    private double minSalary;
    @Column(nullable = false)
    private double maxSalary;
    @Column(columnDefinition = "default 0")
    private int minExperience;
    @Column(columnDefinition = "default current_date")
    private LocalDate createdDate;
    @Column(nullable = false)
    private LocalDate expiredDate;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "recruitment_position_technology",
            joinColumns = @JoinColumn(name = "recruitmentPositionId"),
            inverseJoinColumns = @JoinColumn(name = "technologyId")
    )
    private Set<Technology> technologies;

    public Recruitment_position() {
    }
    public Recruitment_position(String name, String description, double minSalary, double maxSalary, int minExperience, LocalDate createdDate, LocalDate expiredDate) {
        this.name = name;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.minExperience = minExperience;
        this.createdDate = createdDate;
        this.expiredDate = expiredDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(double minSalary) {
        this.minSalary = minSalary;
    }

    public double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public int getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(int minExperience) {
        this.minExperience = minExperience;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }
}
