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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static se.vgregion.portal.patient.event.Extension.assertEndsWith;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PatientEventTest {
    PatientEvent patient;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreate() {
        patient = new PatientEvent("aaa");
        assertEquals("aaa", patient.getInputText());
        assertNull(patient.getPersonNummer());

        patient = new PatientEvent("19521212-1212");
        assertEquals("19521212-1212", patient.getInputText());
        assertEquals("19521212-1212", patient.getPersonNummer().getFull());
    }

    @Test
    public void testEquals() throws Exception {
        PatientEvent p1 = new PatientEvent("531212-1212");
        assertEquals(p1, new PatientEvent("531212-1212"));
        assertEquals(p1, new PatientEvent("19531212-1212"));
        assertEquals(p1, new PatientEvent("19531212+1212"));
        assertEquals(p1, new PatientEvent("195312121212"));
        assertEquals(p1, new PatientEvent("5312121212"));

        PatientEvent p2 = new PatientEvent("aaa");
        assertEquals(p2, new PatientEvent("aaa"));

        PatientEvent p3 = new PatientEvent("531212+1212");
        assertEquals(p3, new PatientEvent("18531212-1212"));
        assertEquals(p3, new PatientEvent("185312121212"));
    }

    @Test
    public void testNotEquals() throws Exception {
        PatientEvent p1 = new PatientEvent("531212-1212");
        PatientEvent p2 = new PatientEvent("aaa");

        assertFalse(p1.equals(new PatientEvent("531212-1213")));
        assertFalse(p2.equals(new PatientEvent("aab")));
        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p2.equals(null));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(1211601890, new PatientEvent("531212-1212").hashCode());
        assertEquals(1211601890, new PatientEvent("5312121212").hashCode());
        assertEquals(1211601890, new PatientEvent("19531212-1212").hashCode());
        assertEquals(1211601890, new PatientEvent("19531212+1212").hashCode());
        assertEquals(1211601890, new PatientEvent("195312121212").hashCode());

        assertEquals(324098207, new PatientEvent("531212+1212").hashCode());
        assertEquals(324098207, new PatientEvent("185312121212").hashCode());
        assertEquals(324098207, new PatientEvent("18531212-1212").hashCode());
        assertEquals(324098207, new PatientEvent("18531212+1212").hashCode());

        assertEquals(96321, new PatientEvent("aaa").hashCode());
        assertEquals(96322, new PatientEvent("aab").hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEndsWith("[personNummer=531212-1212]]", new PatientEvent("531212-1212").toString());
        assertEndsWith("[personNummer=531212-1212]]", new PatientEvent("5312121212").toString());
        assertEndsWith("[personNummer=531212-1212]]", new PatientEvent("19531212-1212").toString());
        assertEndsWith("[personNummer=531212-1212]]", new PatientEvent("19531212+1212").toString());
        assertEndsWith("[personNummer=531212-1212]]", new PatientEvent("195312121212").toString());

        assertEndsWith("[personNummer=531212+1212]]", new PatientEvent("531212+1212").toString());
        assertEndsWith("[personNummer=531212+1212]]", new PatientEvent("185312121212").toString());
        assertEndsWith("[personNummer=531212+1212]]", new PatientEvent("18531212-1212").toString());
        assertEndsWith("[personNummer=531212+1212]]", new PatientEvent("18531212+1212").toString());

        assertEndsWith("[inputText=aaa,personNummer=<null>]", new PatientEvent("aaa").toString());
    }
}
