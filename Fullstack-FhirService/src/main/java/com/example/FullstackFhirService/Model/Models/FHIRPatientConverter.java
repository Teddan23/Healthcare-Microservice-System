package com.example.FullstackFhirService.Model.Models;

import com.example.FullstackFhirService.Controller.DTOs.*;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;


import java.time.ZoneId;
import java.util.Date;

@Component
public class FHIRPatientConverter {

    public PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getIdElement().getIdPart());
        if (patient.hasName() && patient.getNameFirstRep() != null) {
            dto.setFirstName(patient.getNameFirstRep().getGivenAsSingleString());
            dto.setLastName(patient.getNameFirstRep().getFamily());
        } else {
            dto.setFirstName("Unknown");
            dto.setLastName("Unknown");
        }
        dto.setGender(patient.getGender() != null ? patient.getGender().toCode() : "unknown");
        dto.setBirthDate(patient.getBirthDate());
        return dto;
    }

    public static Patient toFHIRPatient(PatientDTO patientDTO) {
        Patient patient = new Patient();

        if (patientDTO == null) {
            throw new IllegalArgumentException("PatientDTO kan inte vara null");
        }

        if (patientDTO.getFirstName() != null || patientDTO.getLastName() != null) {
            HumanName name = new HumanName();
            if (patientDTO.getLastName() != null) {
                name.setFamily(patientDTO.getLastName());
            }
            if (patientDTO.getFirstName() != null) {
                name.addGiven(patientDTO.getFirstName());
            }
            patient.addName(name);
        }

        AdministrativeGender gender = convertToAdministrativeGender(
                patientDTO.getGender() != null ? patientDTO.getGender() : ""
        );
        patient.setGender(gender);

        if (patientDTO.getBirthDate() != null) {
            try {
                patient.setBirthDateElement(new DateType(patientDTO.getBirthDate()));
            } catch (Exception e) {
                System.out.println("Felaktigt datumformat: " + e.getMessage());
            }
        }
        return patient;
    }

    private static AdministrativeGender convertToAdministrativeGender(String genderStr) {
        if (genderStr == null || genderStr.isEmpty()) {
            return AdministrativeGender.UNKNOWN;
        }

        switch (genderStr.toLowerCase()) {
            case "male":
                return AdministrativeGender.MALE;
            case "female":
                return AdministrativeGender.FEMALE;
            case "other":
                return AdministrativeGender.OTHER;
            default:
                return AdministrativeGender.UNKNOWN;
        }
    }

    public static ObservationDTO toObservationDTO(Observation observation) {
        ObservationDTO dto = new ObservationDTO();

        dto.setId(observation.getIdElement().getIdPart());
        if (observation.hasCode() && observation.getCode().hasText()) {
            dto.setCode(observation.getCode().getText());
        } else {
            dto.setCode("Unknown code");
        }

        if (observation.hasValue()) {
            if (observation.getValue() instanceof Quantity) {
                Quantity quantity = (Quantity) observation.getValue();
                String value = quantity.getValue() != null ? quantity.getValue().toString() : "No value";
                String unit = quantity.getUnit() != null ? quantity.getUnit() : "";
                dto.setValue(value + " " + unit);
            } else if (observation.getValue() instanceof CodeableConcept) {
                CodeableConcept codeableConcept = (CodeableConcept) observation.getValue();
                String value = codeableConcept.getText() != null ? codeableConcept.getText() : "No value";
                dto.setValue(value);
            } else {
                dto.setValue(observation.getValue().toString());
            }
        } else {
            dto.setValue("No value");
        }

        if (observation.hasIssued()) {
            Date issuedDate = observation.getIssued();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dto.setDate(sdf.format(issuedDate));
        } else {
            dto.setDate(null);
        }

        return dto;
    }


    public static ConditionDTO toConditionDTO(Condition condition) {
        ConditionDTO dto = new ConditionDTO();

        dto.setId(condition.getIdElement().getIdPart());
        if (condition.hasCode() && condition.getCode().hasText()) {
            dto.setCode(condition.getCode().getText());
            dto.setDescription(condition.getCode().getText());
        } else {
            dto.setCode("Unknown code");
            dto.setDescription("No description");
        }
        if (condition.hasOnsetDateTimeType() && condition.getOnsetDateTimeType().hasValue()) {
            dto.setOnsetDate(condition.getOnsetDateTimeType().getValue());
        } else {
            dto.setOnsetDate(null);
        }
        return dto;
    }

    public static CommunicationDTO toCommunicationDTO(Communication communication) {
        CommunicationDTO dto = new CommunicationDTO();

        dto.setId(communication.getIdElement().getIdPart());
        if (communication.hasSubject() && communication.getSubject().hasDisplay()) {
            dto.setSubject(communication.getSubject().getDisplay());
        } else {
            dto.setSubject("Unknown subject");
        }

        if (communication.hasStatus()) {
            dto.setStatus(communication.getStatus().toCode());
        } else {
            dto.setStatus("unknown");
        }

        dto.setDateSent(communication.hasSent() ? communication.getSent() : null);

        return dto;
    }

    public static Patient convertUserDTOToPatient(UserDTO userDTO) {
        Patient patient = new Patient();

        boolean hasPNIdentifier = false;
        for (Identifier identifier : patient.getIdentifier()) {
            if (identifier.getType().getCodingFirstRep().getCode().equals("PN")) {
                identifier.setValue(userDTO.getPersonnummer());
                hasPNIdentifier = true;
                break;
            }
        }

        if (!hasPNIdentifier) {
            patient.addIdentifier()
                    .setSystem("http://electronichealth.se/identifier/personnummer")
                    .setValue(userDTO.getPersonnummer())
                    .setType(new CodeableConcept().addCoding(new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "PN", "Personnummer")));
        }

        patient.addName()
                .setFamily(userDTO.getLastName())
                .addGiven(userDTO.getFirstName());

        if ("male".equalsIgnoreCase(userDTO.getGender())) {
            patient.setGender(AdministrativeGender.MALE);
        } else if ("female".equalsIgnoreCase(userDTO.getGender())) {
            patient.setGender(AdministrativeGender.FEMALE);
        } else {
            patient.setGender(AdministrativeGender.UNKNOWN);
        }

        if (userDTO.getBirthDate() != null) {
            patient.setBirthDate(java.util.Date.from(userDTO.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        return patient;
    }

}
