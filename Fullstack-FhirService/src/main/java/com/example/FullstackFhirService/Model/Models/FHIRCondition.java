package com.example.FullstackFhirService.Model.Models;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Patient;

public class FHIRCondition {
    public static Condition createCondition(Patient patient, String diagnosis) {
        Condition condition = new Condition();

        condition.setSubject(new Reference(patient));

        condition.setCode(new CodeableConcept().setText(diagnosis));

        return condition;
    }
}
