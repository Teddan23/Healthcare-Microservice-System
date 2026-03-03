package se.kth.search.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

@Path("/practitioner")
public interface PractitionerClient {

    @GET
    @Path("/patients")
    Uni<List<String>> getPatientsByPractitioner(@QueryParam("practitionerId") String practitionerId);

    @GET
    @Path("/practitioners")
    Uni<String> getPractitionerIdByName(@QueryParam("name") String name);

}