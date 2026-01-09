package com.example.loanManage.dto;

import com.example.loanManage.entity.Gender;
import com.example.loanManage.entity.IdType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Lombok annotation: Automatically creates Getters, Setters, toString, and equals methods.
@NoArgsConstructor //Lombok: Creates a constructor with no arguments.
@AllArgsConstructor //Lombok: Creates a constructor with all fields as arguments.
public class BorrowerDto {
    private Long id;
    @NotBlank(message = "Name is required") // Ensures the name is not empty or just spaces.
    @Size(min = 3, message = "Name must be at least 3 characters") // Rejects names that are too short.
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

    private Boolean sameAsPresentAddress; // A checkbox value to indicate if both addresses are the same.
}
