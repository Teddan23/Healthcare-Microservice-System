package com.example.FullstackFhirService.Controller.Controllers;

import com.example.FullstackFhirService.Controller.DTOs.*;
import com.example.FullstackFhirService.Model.Services.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @GetMapping("/{id}")
    public PatientDTO getPatientById(@PathVariable String id) {
        return patientService.getPatientById(id);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF')")
    @GetMapping("/all")
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF')")
    @GetMapping("/all/paginated")
    public ResponseEntity<Map<String, Object>> getAllPatientsPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<PatientDTO> patients = patientService.getPatientsPage(page, size);
        int totalCount = patientService.getTotalPatientCount();

        Map<String, Object> response = new HashMap<>();
        response.put("patients", patients);
        response.put("totalCount", totalCount);
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @GetMapping("/{personnummer}/personDetails")
    public ResponseEntity<PatientDTO> getPatientByPersonummerDetails(@PathVariable String personnummer) {
        PatientDTO patient = patientService.getPatientByPersonnummer(personnummer);
        if (patient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("Patient details fetched for: " + personnummer);



        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @GetMapping("/{id}/details")
    public ResponseEntity<PatientDTO> getPatientDetails(@PathVariable String id) {
        try {
            PatientDTO patientDetails = patientService.getPatientDetails(id);
            return ResponseEntity.ok(patientDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF')")
    @PostMapping("/{patientId}/addNote")
    public ResponseEntity<String> addNoteToPatient(@PathVariable String patientId, @RequestBody ObservationDTO observationDto) {
        try {
            patientService.addNoteToPatient(patientId, observationDto);
            return ResponseEntity.ok("Note added successfully");
        } catch (Exception e) {
            System.out.println("Error adding note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding note: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF')")
    @PostMapping("/{patientId}/addDiagnosis")
    public ResponseEntity<String> addDiagnosisToPatient(@PathVariable String patientId, @RequestBody ConditionDTO conditionDto) {
        try {
            patientService.addDiagnosisToPatient(patientId, conditionDto);
            return ResponseEntity.ok("Diagnosis added successfully");
        } catch (Exception e) {
            System.out.println("Error adding diagnosis: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding diagnosis: " + e.getMessage());
        }
    }



    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @GetMapping("/{id}/observations")
    public List<ObservationDTO> getObservationsByPatientId(@PathVariable String id) {
        return patientService.getObservationsByPatientId(id);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @GetMapping("/{id}/conditions")
    public List<ConditionDTO> getConditionsByPatientId(@PathVariable String id) {
        return patientService.getConditionsByPatientId(id);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @GetMapping("/{id}/communications")
    public List<CommunicationDTO> getCommunicationsByPatientId(@PathVariable String id) {
        return patientService.getCommunicationsByPatientId(id);
    }

    @PostMapping("/addPatient")
    public void addPatientToHapiWithUser(@RequestBody UserDTO userDto){
        System.out.println("I MADE IT IN! am adding a user now :3");
        patientService.addPatientToHapiWithUser(userDto);
    }

}
