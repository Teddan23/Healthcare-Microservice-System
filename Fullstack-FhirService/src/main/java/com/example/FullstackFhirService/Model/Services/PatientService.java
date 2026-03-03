package com.example.FullstackFhirService.Model.Services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleUtil;
import com.example.FullstackFhirService.Controller.DTOs.*;
import com.example.FullstackFhirService.Model.Models.FHIRPatientConverter;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.hl7.fhir.r4.model.Patient;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.FullstackFhirService.Model.Models.FHIRPatientConverter.convertUserDTOToPatient;
import static com.example.FullstackFhirService.Model.Models.FHIRPatientConverter.toFHIRPatient;


@Service
public class PatientService {

    @Autowired
    private FhirClientService fhirClientService;

    @Autowired
    private FHIRPatientConverter fhirPatientConverter;

    @Value("${pagination.page.size:20}")
    private int pageSize;

    public PatientDTO getPatientById(String id) {
        Patient patient = fhirClientService.getPatientById(id);
        return fhirPatientConverter.toDTO(patient);
    }

    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = fhirClientService.getAllPatients();
        return patients.stream().map(fhirPatientConverter::toDTO).collect(Collectors.toList());
    }

    private String getPersonnummer(Patient patient)
    {
        if(patient.hasIdentifier())
        {
            for(Identifier identifier : patient.getIdentifier())
            {
                if(identifier.getSystem() != null && identifier.getSystem().equals("http://electronichealth.se/identifier/personnummer"))
                {
                    return identifier.getValue();
                }
            }
        }

        return null;
    }

    public PatientDTO getPatientDetails(String id) {
        Patient patient = fhirClientService.getPatientById(id);
        if (patient == null) {
            throw new RuntimeException("Patient not found with ID: " + id);
        }

        PatientDTO patientDTO = fhirPatientConverter.toDTO(patient);

        patientDTO.setPersonnummer(getPersonnummer(patient));

        List<ObservationDTO> observations = getObservationsByPatientId(id);
        List<ConditionDTO> conditions = getConditionsByPatientId(id);
        List<CommunicationDTO> communications = getCommunicationsByPatientId(id);
        if (observations == null)
        {
            for(int i = 0; i < 10; i++)
            {
                System.out.println("OBSERVATION IS NULL! EMPTY ARRAY CREATES");
            }
            observations = new ArrayList<>();
        }
        if (conditions == null) {
            for(int i = 0; i < 10; i++)
            {
                System.out.println("CONDITION IS NULL! EMPTY ARRAY CREATES");
            }
            conditions = new ArrayList<>();
        }
        for(int i = 0; i < 10; i++)
        {
            System.out.println("Personnummer is: " + patientDTO.getPersonnummer());
        }

        patientDTO.setObservations(observations);
        patientDTO.setConditions(conditions);
        patientDTO.setCommunications(communications);

        return patientDTO;
    }

    public List<ObservationDTO> getObservationsByPatientId(String patientId) {
        List<Observation> observations = fhirClientService.getObservationsByPatientId(patientId);

        return observations.stream()
                .map(observation -> fhirPatientConverter.toObservationDTO(observation))
                .collect(Collectors.toList());
    }

    public List<ConditionDTO> getConditionsByPatientId(String patientId) {
        List<Condition> conditions = fhirClientService.getConditionsByPatientId(patientId);

        return conditions.stream()
                .map(condition -> fhirPatientConverter.toConditionDTO(condition))
                .collect(Collectors.toList());
    }

    public List<CommunicationDTO> getCommunicationsByPatientId(String patientId) {
        List<Communication> communications = fhirClientService.getCommunicationsByPatientId(patientId);

        return communications.stream()
                .map(communication -> fhirPatientConverter.toCommunicationDTO(communication))
                .collect(Collectors.toList());
    }

    public List<PatientDTO> getPatientsPage(int pageNumber, int pageSize) {
        Bundle bundle = fhirClientService.getClient()
                .search()
                .forResource(Patient.class)
                .count(pageSize)
                .offset((pageNumber - 1) * pageSize)
                .returnBundle(Bundle.class)
                .execute();

        List<Patient> patients = BundleUtil.toListOfResourcesOfType(FhirContext.forR4(), bundle, Patient.class);

        return patients.stream()
                .map(fhirPatientConverter::toDTO)
                .collect(Collectors.toList());
    }




    public int getTotalPatientCount() {
        Bundle bundle = fhirClientService.getClient()
                .search()
                .forResource(Patient.class)
                .count(1000)
                .returnBundle(Bundle.class)
                .execute();

        List<Patient> patients = new ArrayList<>();
        while (bundle != null && !bundle.getEntry().isEmpty()) {
            patients.addAll(BundleUtil.toListOfResourcesOfType(FhirContext.forR4(), bundle, Patient.class));

            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = fhirClientService.getClient()
                        .loadPage()
                        .next(bundle)
                        .execute();
            } else {
                break;
            }
        }

        return patients.size();
    }


    public void addDiagnosisToPatient(String patientId, ConditionDTO conditionDto) {
        Patient patient = fhirClientService.getPatientById(patientId);
        if (patient == null) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }

        Condition condition = new Condition();
        condition.setId(conditionDto.getId());
        condition.setSubject(new Reference("Patient/" + patientId));
        condition.setClinicalStatus(new CodeableConcept().setText("active"));

        condition.setCode(new CodeableConcept().setText(conditionDto.getCode()));

        if (conditionDto.getDescription() != null && !conditionDto.getDescription().isEmpty()) {
            condition.setNote(
                    List.of(new Annotation().setText(conditionDto.getDescription()))
            );
        }

        if (conditionDto.getOnsetDate() != null) {
            condition.setOnset(new DateTimeType(conditionDto.getOnsetDate()));
        }

        try {
            fhirClientService.getClient().update().resource(condition).execute();
            System.out.println("Diagnosis added successfully");
        } catch (Exception e) {
            System.err.println("Error adding diagnosis: " + e.getMessage());
            throw new RuntimeException("Error adding diagnosis: " + e.getMessage());
        }
    }

    public void addNoteToPatient(String patientId, ObservationDTO observationDto) {
        Patient patient = fhirClientService.getPatientById(patientId);
        if (patient == null) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }

        Observation observation = new Observation();
        observation.setId(observationDto.getId());
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation.setCode(new CodeableConcept().setText(observationDto.getCode()));
        observation.setValue(new StringType(observationDto.getValue()));
        observation.setEffective(new DateTimeType(observationDto.getDate()));

        observation.setSubject(new Reference("Patient/" + patientId));
        
        try {
            fhirClientService.getClient().update().resource(observation).execute();
        } catch (Exception e) {
            System.err.println("Error updating observation: " + e.getMessage());
            throw new RuntimeException("Error updating observation: " + e.getMessage());
        }
    }

    public PatientDTO getPatientByPersonnummer(String personnummer) {
        Patient patient = fhirClientService.getPatientByPersonnummer(personnummer);

        if (patient == null) {
            System.out.println("Patient is null");
            return null;
        }
        for(int i = 0; i < 10; i++)
        {
            System.out.println("Patient Id är: " +patient.getIdPart());
        }
        return getPatientDetails(patient.getIdPart());
    }

    public void addPatientToHapiWithUser(UserDTO userDto){
        Patient patient = convertUserDTOToPatient(userDto);
        try {
            fhirClientService.getClient().update()
                    .resource(patient)
                    .conditionalByUrl("Patient?identifier=" + patient.getIdentifierFirstRep().getSystem() + "|" + patient.getIdentifierFirstRep().getValue())
                    .execute();
        } catch (Exception e) {

            throw new RuntimeException("Error while updating/creating patient in HAPI FHIR: " + e.getMessage());
        }
        System.out.println("Patient just added to HAPI. The ID of that patient is: " + patient.getIdentifier());
    }

}
