package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRequestDTO {

    @Size(max = 100 , message = "Name should not exceed 100 characters")
    @NotBlank(message = "Name should not be blank")
    private String name ;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address should not be blank")
    private String address;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    @NotBlank(groups = CreatePatientValidationGroup.class , message = "Registered date is required")
    private String registeredDate;

    public @Size(max = 100, message = "Name should not exceed 100 characters") @NotBlank(message = "Name should not be blank") String getName() {
        return name;
    }

    public void setName(@Size(max = 100, message = "Name should not exceed 100 characters") @NotBlank(message = "Name should not be blank") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Email should not be blank") @Email(message = "Email should be valid") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email should not be blank") @Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Address should not be blank") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Address should not be blank") String address) {
        this.address = address;
    }

    public @NotBlank(message = "Date of birth is required") String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotBlank(message = "Date of birth is required") String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotBlank(groups = CreatePatientValidationGroup.class, message = "Registered date is required") String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(@NotBlank(groups = CreatePatientValidationGroup.class, message = "Registered date is required") String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
