package com.data.project.dto.admin;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class TechnologyDTO {
    private Long id;

    @NotBlank(message = "Tên công nghệ không được để trống")
    @Length(max = 100, message = "Tên công nghệ không được vượt quá 100 ký tự")
    private String name;

    public TechnologyDTO() {}

    public TechnologyDTO(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

