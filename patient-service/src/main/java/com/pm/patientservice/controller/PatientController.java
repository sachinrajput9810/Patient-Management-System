package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    public PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        return ResponseEntity.ok(patientService.getPatients());
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class , CreatePatientValidationGroup.class})
                                                                @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok(patientResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id ,@Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok(patientResponseDTO);
    }

}
