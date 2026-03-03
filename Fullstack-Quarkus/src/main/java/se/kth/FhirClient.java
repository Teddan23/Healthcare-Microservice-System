package se.kth;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface FhirClient {

    @GET
    @Path("/Patient")
    Uni<String> searchPatients(@QueryParam("name") String name);

    @GET
    @Path("/Practitioner")
    Uni<String> searchPractitioner(@QueryParam("name") String name);


    @GET
    @Path("/Condition")
    Uni<String> searchConditionByPatientAndCode(
            @QueryParam("patient") String patientId,
            @QueryParam("code") String conditionCode);


    @GET
    @Path("/Encounter")
    Uni<String> searchPractitionerEncounters(
            @QueryParam("date") String date,
            @QueryParam("practitioner") String practitioner
    );



    @GET
    @Path("/Condition/display")
    Uni<String> searchConditionByDisplay(@QueryParam("conditionDisplay") String conditionDisplay);

    @GET
    @Path("/Patient/{patientId}")
    Uni<String> getPatientById(@PathParam("patientId") String patientId);
}