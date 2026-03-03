package se.kth.search.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Patient {

    private String id;
    private String name;
    private String patientCondition;
    private String practitionerId;  // Läkare som patienten är kopplad till

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    public String getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(String practitionerId) {
        this.practitionerId = practitionerId;
    }
}
