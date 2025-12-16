package com.example.loanManage.dto;

import com.example.loanManage.entity.Gender;
import com.example.loanManage.entity.IdType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    private String name;

    @NotNull(message = "ID Type required")
    private IdType idType;

    @NotBlank(message = "ID Number required")
    private String idNumber;

    @NotBlank(message = "Date of Birth required")
    private String dateOfBirth;

    @NotNull(message = "Gender required")
    private Gender gender;

    @NotBlank(message = "Mobile required")
    @Pattern(regexp = "^[0-9]{11}$", message = "Mobile must be 11 digits")
    private String mobile;

    @NotBlank(message = "Email required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Present division required")
    private String presentDivision;

    @NotBlank(message = "Present district required")
    private String presentDistrict;

    @NotBlank(message = "Present upazila required")
    private String presentUpazila;

    @NotBlank(message = "Permanent division required")
    private String permanentDivision;

    @NotBlank(message = "Permanent district required")
    private String permanentDistrict;

    @NotBlank(message = "Permanent upazila required")
    private String permanentUpazila;

    private Boolean sameAsPresentAddress;
}
