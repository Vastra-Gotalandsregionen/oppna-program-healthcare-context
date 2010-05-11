package se.vgregion.portal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PatientContext implements Serializable {

    private Patient currentPatient;

    private List<Patient> patientHistory = new ArrayList<Patient>();

    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

    public List<Patient> getPatientHistory() {
        return Collections.unmodifiableList(patientHistory);
    }

    public void addToHistory(Patient patient) {
        if (!patientHistory.contains(patient)) {
            patientHistory.add(patient);
        }
    }

    public void clear() {
        currentPatient = null;
    }
}
