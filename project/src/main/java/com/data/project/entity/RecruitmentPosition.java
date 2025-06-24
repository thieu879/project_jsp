package com.data.project.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "recruitmentPosition")
@DynamicInsert
public class RecruitmentPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 100)
    private String description;

    @Column(nullable = false)
    private double minSalary;

    @Column(nullable = false)
    private double maxSalary;

    @Column(columnDefinition = "INT DEFAULT 0")
    @ColumnDefault("0")
    private int minExperience = 0;

    @Column(name = "createdDate")
    private LocalDate createdDate;

    @Column(nullable = false)
    private LocalDate expiredDate;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    @ColumnDefault("1")
    private boolean status = true;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "recruitment_position_technology",
            joinColumns = @JoinColumn(name = "recruitmentPositionId"),
            inverseJoinColumns = @JoinColumn(name = "technologyId")
    )
    private Set<Technology> technologies;


    public RecruitmentPosition() {
        this.createdDate = LocalDate.now();
        this.status = true;
        this.minExperience = 0;
    }

    public RecruitmentPosition(String name, String description, double minSalary, double maxSalary,
                               int minExperience, LocalDate expiredDate) {
        this();
        this.name = name;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.minExperience = minExperience;
        this.expiredDate = expiredDate;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = LocalDate.now();
        }
        // Validation logic thay vì check constraint
        if (this.minSalary < 0) {
            throw new IllegalArgumentException("minSalary không thể âm");
        }
        if (this.maxSalary < this.minSalary) {
            throw new IllegalArgumentException("maxSalary phải >= minSalary");
        }
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }
}
