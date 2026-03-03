package se.kth.search.client;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient
public class PractitionerClientImpl implements PractitionerClient {

    @Override
    public Uni<List<String>> getPatientsByPractitioner(String practitionerId) {
        // REST-call handled by Quarkus and MicroProfile Rest Client.
        return null;
    }

    @Override
    public Uni<String> getPractitionerIdByName(String name) {
        // REST-call handled by Quarkus and MicroProfile Rest Client.
        return null;
    }
}
