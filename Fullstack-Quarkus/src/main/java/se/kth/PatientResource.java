package se.kth;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

// JSON Hantering
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

// Java Utilities
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;
@Path("/patients")
public class PatientResource {

    @Inject
    @RestClient
    FhirClient fhirClient;

    @GET
    @Path("/search")
    public Uni<Response> searchPatientsByName(@QueryParam("name") String name) {
        return fhirClient.searchPatients(name)
                .onItem().transform(data -> Response.ok(data).build());
    }

    @GET
    @Path("/search-by-condition")
    public Uni<Response> searchSpecificPatientByCondition(
            @QueryParam("patientId") String patientId,
            @QueryParam("conditionCode") String conditionCode) {
        if (patientId == null || patientId.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Ange patient-ID för sökning."));
        }
        if (conditionCode == null || conditionCode.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Ange en condition-kod för sökning."));
        }

        return fhirClient.searchConditionByPatientAndCode(patientId, conditionCode)
                .onItem().transform(response -> {
                    if (response == null || response.isEmpty()) {
                        return Response.status(Response.Status.NOT_FOUND)
                                .entity("Ingen matchande condition hittades för patienten.")
                                .build();
                    }
                    return Response.ok(response).build();
                });
    }

    @GET
    @Path("/{practitionerId}/encounters")
    public Uni<Response> searchPractitionerEncountersByDate(
            @PathParam("practitionerId") String practitionerId,
            @QueryParam("date") String date
    ) {
        String formattedPractitionerId = "Practitioner/" + practitionerId;

        return fhirClient.searchPractitionerEncounters(date, formattedPractitionerId)
                .onItem().transform(data -> Response.ok(data).build());
    }

}
