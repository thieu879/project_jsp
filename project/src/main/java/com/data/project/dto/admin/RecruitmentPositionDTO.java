package com.data.project.dto.admin;

import com.data.project.entity.Technology;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public class RecruitmentPositionDTO {
    private Long id;

    @NotBlank(message = "Tên vị trí không được để trống")
    @Length(max = 100, message = "Tên vị trí không được vượt quá 100 ký tự")
    private String name;

    @Length(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @NotNull(message = "Lương tối thiểu không được để trống")
    @DecimalMin(value = "0.0", message = "Lương tối thiểu phải >= 0")
    private Double minSalary;

    @NotNull(message = "Lương tối đa không được để trống")
    @DecimalMin(value = "0.0", message = "Lương tối đa phải >= 0")
    private Double maxSalary;

    @Min(value = 0, message = "Kinh nghiệm tối thiểu không được âm")
    @Max(value = 20, message = "Kinh nghiệm tối thiểu không được vượt quá 20 năm")
    private int minExperience;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải là ngày trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;

    private boolean status;
    private Set<Technology> technologies;

    // Thêm trường này để binding với checkbox form
    private Set<Long> technologyIds;

    public RecruitmentPositionDTO() {}

    public RecruitmentPositionDTO(String name, String description, Double minSalary, Double maxSalary,
                                  int minExperience, LocalDate expiredDate) {
        this.name = name;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.minExperience = minExperience;
        this.createdDate = LocalDate.now();
        this.expiredDate = expiredDate;
        this.status = true;
    }

    @AssertTrue(message = "Lương tối đa phải lớn hơn hoặc bằng lương tối thiểu")
    public boolean isSalaryValid() {
        if (minSalary == null || maxSalary == null) {
            return true;
        }
        return maxSalary >= minSalary;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getMinSalary() { return minSalary; }
    public void setMinSalary(Double minSalary) { this.minSalary = minSalary; }

    public Double getMaxSalary() { return maxSalary; }
    public void setMaxSalary(Double maxSalary) { this.maxSalary = maxSalary; }

    public int getMinExperience() { return minExperience; }
    public void setMinExperience(int minExperience) { this.minExperience = minExperience; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public LocalDate getExpiredDate() { return expiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.expiredDate = expiredDate; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    // Sửa setter để đồng bộ với technologyIds
    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
        // Đồng bộ technologyIds khi set technologies
        if (technologies != null) {
            this.technologyIds = technologies.stream()
                    .map(Technology::getId)
                    .collect(Collectors.toSet());
        }
    }

    // Getter và Setter cho technologyIds
    public Set<Long> getTechnologyIds() {
        return technologyIds;
    }

    public void setTechnologyIds(Set<Long> technologyIds) {
        this.technologyIds = technologyIds;
    }
}
