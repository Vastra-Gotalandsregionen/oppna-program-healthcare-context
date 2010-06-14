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

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PatientEvent implements Serializable {
    private static final long serialVersionUID = 1196374164899537338L;

    private final String inputText;

    private final PersonNummer personNummer;

    /**
     * PatientEvent are immutable and cannot be changed after creation.
     *
     * @param inputText String representing a patient identifier.
     */
    public PatientEvent(String inputText) {
        this.inputText = inputText;

        PersonNummer pNo = PersonNummer.personummer(inputText);
        if (pNo.getType() != PersonNummer.Type.INVALID) {
            personNummer = pNo;
        } else {
            personNummer = null;
        }
    }

    public String getInputText() {
        return inputText;
    }

    public PersonNummer getPersonNummer() {
        return personNummer;
    }

//    public void setPersonNummer(PersonNummer pNo) {
//        personNummer = pNo;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatientEvent)) {
            return false;
        }

        PatientEvent that = (PatientEvent) o;

        if (personNummer != null && that.personNummer != null) {
            return personNummer.equals(that.personNummer);
        } else if ((personNummer != null && that.personNummer == null) ||
                (personNummer == null && that.personNummer != null)) {
            return false;
        } else {
            if (inputText != null ? !inputText.equals(that.inputText) : that.inputText != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return ((personNummer != null) ?
                personNummer.hashCode() : ((inputText != null) ? inputText.hashCode() : 0));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("inputText", inputText).
                append("personNummer", personNummer).
                toString();
    }

    //    @Override
//    public String toString() {
//        if (personNummer != null && personNummer.getType() != PersonNummer.Type.INVALID) {
//            return new ToStringBuilder(this).append("personNummer", personNummer.getNormal()).toString();
//        } else {
//            return new ToStringBuilder(this).append("inputText", inputText).toString();
//        }
//    }
}
