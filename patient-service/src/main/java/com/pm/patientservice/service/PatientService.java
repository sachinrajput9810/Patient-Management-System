package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository ;
    private final BillingServiceGrpcClient billingServiceGrpcClient;


    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients =  patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistException("Patient with email " + patientRequestDTO.getEmail() + " already exists.");
        }
        Patient patient = PatientMapper.toModel(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);
        // creating billing account via GRPC after patient creation
        billingServiceGrpcClient.createBillingAccount(String.valueOf(savedPatient.getId()), savedPatient.getName() , savedPatient.getEmail()) ;
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(UUID id , PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow( () ->
                new PatientNotFoundException("Patient not found with id :: " + id) ) ;

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistException("Patient with email " + patientRequestDTO.getEmail() + " already exists.");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patientRepository.save(patient);
        return PatientMapper.toDTO(patient);
    }

    public void deletePatient(UUID id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isEmpty()) {
            throw new PatientNotFoundException("Patient not found with id :: " + id);
        }
        patientRepository.delete(patient.get());
    }


}
