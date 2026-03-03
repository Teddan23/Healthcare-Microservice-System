package com.example.FullstackUserService.Model.Services;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakAuthService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.credentials.secret}")
    private String keycloakClientSecret;

    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    // Denna metod returnerar en token från Keycloak
    public String getToken(String username, String password) {
        try{
            System.out.println("In getToken method");

            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakAuthServerUrl)
                    .realm(keycloakRealm)
                    .clientId(keycloakClientId)
                    .clientSecret(keycloakClientSecret)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(username)
                    .password(password)
                    .build();

            System.out.println("keycloak build is:\n" + keycloak);

            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();

            System.out.println("Accesstoken response is:\n" + tokenResponse);
            return tokenResponse.getToken();
        }
        catch (Exception e){
            System.err.println("Error fetching token from Keycloak: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }



    public boolean createUser(String personnummer, String firstName, String lastName, String password, String roleName) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakAuthServerUrl)
                    .realm("master")
                    .clientId(adminClientId)
                    .username(adminUsername)
                    .password(adminPassword)
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(personnummer);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            user.setCredentials(Collections.singletonList(credential));

            UsersResource usersResource = keycloak.realm(keycloakRealm).users();
            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                System.out.println("User created successfully in Keycloak.");

                String userId = usersResource.search(personnummer).get(0).getId();

                RoleRepresentation role = keycloak.realm(keycloakRealm)
                        .roles()
                        .get(roleName.toUpperCase())
                        .toRepresentation();

                usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));

                return true;
            } else {
                System.err.println("Failed to create user. Status: " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
