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
 * The PatientContext is an aggregate bean that holds a reference to PatientEvent searches
 * performed in the current session.
 * Each health-care context enabled application has it's own PatientContext, the application
 * is responsible for keeping it's state up to date through listening to change events from
 * other health-care context enabled portlets in the portal.
 *
 * The most common scenario is to have multiple instances of Patient search portlet on the
 * portal and they will have this communication by default.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PatientContext implements Serializable {
    private static final long serialVersionUID = 5623507583753192525L;

    private PatientEvent currentPatient;

    private List<PatientEvent> patientHistory = new ArrayList<PatientEvent>();

    /**
     * Access the currently active PatientEvent.
     *
     * @return PatientEvent
     */
    public PatientEvent getCurrentPatient() {
        return currentPatient;
    }

    /**
     * Set the currently active PatientEvent.
     *
     * @param currentPatient PatientEvent
     */
    public void setCurrentPatient(PatientEvent currentPatient) {
        this.currentPatient = currentPatient;
    }

    /**
     * Access the context history.
     *
     * @return List
     */
    public List<PatientEvent> getPatientHistory() {
        return Collections.unmodifiableList(patientHistory);
    }

    /**
     * Add a PatientEvent object to the context history.
     * Duplications are not saved.
     *
     * @param patient PatientEvent.
     */
    public void addToHistory(PatientEvent patient) {
        if (!patientHistory.contains(patient)) {
            patientHistory.add(patient);
        }
    }

    /**
     * Utility method to access context history size.
     *
     * @return Integer
     */
    public int getPatientHistorySize() {
        return patientHistory.size();
    }

    public void clear() {
        currentPatient = null;
    }
}
