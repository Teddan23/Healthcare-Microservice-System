package com.example.FullstackFhirService.Model.Models;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Patient;

public class FHIRObservation {
    public static Observation createObservation(Patient patient, String note) {
        Observation observation = new Observation();

        observation.setStatus(Observation.ObservationStatus.FINAL);

        observation.setCode(new CodeableConcept().setText("Patient Note"));

        observation.setSubject(new Reference(patient));

        observation.setValue(new StringType(note));

        return observation;
    }
}
