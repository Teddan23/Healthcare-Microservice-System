package se.kth;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/practitioners")
public class PractitionerResource {

    @Inject
    @RestClient
    FhirClient fhirClient;

    @GET
    @Path("/search")
    public Uni<Response> searchPractitionerByName(@QueryParam("name") String name) {
        return fhirClient.searchPractitioner(name)
                .onItem().transform(data -> Response.ok(data).build());
    }

    @GET
    @Path("/encounters")
    public Uni<Response> searchPractitionerEncountersByDate(
            @QueryParam("date") String date,
            @QueryParam("practitioner") String practitioner)
             {

                 String practitionerReference= "Practitioner/" + practitioner;


                 return fhirClient.searchPractitionerEncounters(date, practitionerReference)
                .onItem().transform(data -> Response.ok(data).build());
    }
}