package com.data.project.dto.admin;

import com.data.project.entity.Technology;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public class CandidateDTO {
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Length(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Length(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;

    @Min(value = 0, message = "Kinh nghiệm không được âm")
    @Max(value = 50, message = "Kinh nghiệm không được vượt quá 50 năm")
    private int experience;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gender;

    private Boolean status = true;

    @Length(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private Set<Technology> technologies;

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }

    public CandidateDTO() {}

    public CandidateDTO(String name, String email, String phone, int experience,
                        Boolean gender, String description, LocalDate dob) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.experience = experience;
        this.gender = gender;
        this.description = description;
        this.dob = dob;
        this.status = true;
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public Boolean getGender() { return gender; }
    public void setGender(Boolean gender) { this.gender = gender; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
}
