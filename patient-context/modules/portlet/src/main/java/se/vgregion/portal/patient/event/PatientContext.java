/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.portal.patient.event;

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

    private PatientEvent currentPatient;

    private List<PatientEvent> patientHistory = new ArrayList<PatientEvent>();

    public PatientEvent getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(PatientEvent currentPatient) {
        this.currentPatient = currentPatient;
    }

    public List<PatientEvent> getPatientHistory() {
        return Collections.unmodifiableList(patientHistory);
    }

    public void addToHistory(PatientEvent patient) {
        if (!patientHistory.contains(patient)) {
            patientHistory.add(patient);
        }
    }

    public void clear() {
        currentPatient = null;
    }
}
