package com.example.FullstackUserService.Controller.Controllers;

import com.example.FullstackUserService.Controller.DTOs.UserDTO;
import com.example.FullstackUserService.Model.Services.KeycloakAuthService;
import com.example.FullstackUserService.Model.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {


    private static final Pattern PERSONNUMMER_PATTERN = Pattern.compile("^\\d{8}-\\d{4}$");

    @Autowired
    private UserService userService;

    @Autowired
    private KeycloakAuthService keycloakAuthService;

    @GetMapping("/fetchUser")
    public UserDTO getUser(String personnummer){
        return userService.getUserByPersonnummer(personnummer);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials){
        String personnummer = credentials.get("personnummer");
        String password = credentials.get("password");


        UserDTO user = userService.handleLogin(personnummer, password);

        System.out.println("User: " + user);
        if(user != null){
            System.out.println("User is not null!!!");
        }
        else{
            System.out.println("User not found :( cope + ratio");
        }

        if(user != null){
            String token = keycloakAuthService.getToken(personnummer, password);
            if(token != null){
                System.out.println("TOKEN ÄR: " + token);
                Map<String, Object> response = Map.of(
                        "personnummer", user.getPersonnummer(),
                        "role", user.getRole(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "token", token
                );
                return ResponseEntity.ok(response);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, Object> params){

        System.out.println("AM IN REGISTER!");
        String personnummer = (String) params.get("personnummer");

        if (!PERSONNUMMER_PATTERN.matcher(personnummer).matches()) {
            throw new RuntimeException("Wrong format for personnummer!");
        }

        String firstName = (String) params.get("firstName");
        String lastName = (String) params.get("lastName");
        String gender = (String) params.get("gender");

        String birthDateStr = (String) params.get("birthDate");
        LocalDate birthDate = LocalDate.parse(birthDateStr);
        String role = (String) params.get("role");
        String password = (String) params.get("password");

        UserDTO userDTO = new UserDTO(personnummer, firstName, lastName, gender, birthDate, role);



        if(userService.addUser(userDTO, password)){
            if(keycloakAuthService.createUser(personnummer, firstName, lastName, password, role.toUpperCase())){
                return ResponseEntity.status(HttpStatus.CREATED).body("User successfully added.");
            }
            else{
                return ResponseEntity.status(HttpStatus.CREATED).body("Could not add user to keycloak.");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not add user for DB or HAPI");
        }
    }

}
