package com.example.FullstackFhirService.Model.Services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FhirClientService {

    private final IGenericClient client;

    public FhirClientService() {
        var context = FhirContext.forR4();
        var baseURL = "https://hapi-fhir.app.cloud.cbh.kth.se/fhir";
        this.client = context.newRestfulGenericClient(baseURL);
    }

    public Patient getPatientById(String patientId) {
        Patient patient = client.read().resource(Patient.class).withId(patientId).execute();
        return patient;
    }

    public IGenericClient getClient() {
        return this.client;
    }

    public List<Patient> getAllPatients() {

        Bundle bundle = client
                .search()
                .forResource(Patient.class)
                .returnBundle(Bundle.class)
                .execute();


        return BundleUtil.toListOfEntries(FhirContext.forR4(), bundle).stream()
                .map(entry -> (Patient) entry.getResource())
                .collect(Collectors.toList());
    }

    public Patient getPatientByPersonnummer(String personnummer) {
        Bundle bundle = client.search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().systemAndCode("http://electronichealth.se/identifier/personnummer", personnummer))
                .returnBundle(Bundle.class)
                .execute();

        if (bundle != null && !bundle.getEntry().isEmpty()) {
            return (Patient) bundle.getEntry().get(0).getResource();
        } else {
            return null;
        }
    }

    public List<Observation> getObservationsByPatientId(String patientId) {
        Bundle bundle = client
                .search()
                .forResource(Observation.class)
                .where(Observation.PATIENT.hasId(patientId))
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(FhirContext.forR4(), bundle).stream()
                .map(entry -> (Observation) entry.getResource())
                .collect(Collectors.toList());
    }

    public List<Condition> getConditionsByPatientId(String patientId) {
        Bundle bundle = client
                .search()
                .forResource(Condition.class)
                .where(Condition.PATIENT.hasId(patientId))
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(FhirContext.forR4(), bundle).stream()
                .map(entry -> (Condition) entry.getResource())
                .collect(Collectors.toList());
    }

    public List<Communication> getCommunicationsByPatientId(String patientId) {
        Bundle bundle = client
                .search()
                .forResource(Communication.class)
                .where(Communication.PATIENT.hasId(patientId))
                .returnBundle(Bundle.class)
                .execute();

        return BundleUtil.toListOfEntries(FhirContext.forR4(), bundle).stream()
                .map(entry -> (Communication) entry.getResource())
                .collect(Collectors.toList());
    }


}