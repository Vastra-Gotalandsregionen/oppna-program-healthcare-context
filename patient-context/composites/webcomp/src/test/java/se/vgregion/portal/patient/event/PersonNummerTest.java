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

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PersonNummerTest {
    PersonNummer pNo;

    @Test
    public void testType() throws Exception {
        assertEquals("Wrong type", PersonNummer.Type.SHORT, PersonNummer.personummer("6707124696").getType());
        assertEquals("Wrong type", PersonNummer.Type.NORMAL, PersonNummer.personummer("670712-4696").getType());
        assertEquals("Wrong type", PersonNummer.Type.NORMAL, PersonNummer.personummer("670712+4696").getType());
        assertEquals("Wrong type", PersonNummer.Type.FULL_NO, PersonNummer.personummer("196707124696").getType());
        assertEquals("Wrong type", PersonNummer.Type.FULL, PersonNummer.personummer("19670712-4696").getType());
        assertEquals("Wrong type", PersonNummer.Type.FULL, PersonNummer.personummer("19670712+4696").getType());

        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("670712 4696").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("19670712 4696").getType());

        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("196707120").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("19670712+46962").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("670712469a").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("670712-469a").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("67071204696").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("670712/4696").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("19670712469a").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("19670712-469a").getType());
        assertEquals("Wrong type", PersonNummer.Type.INVALID, PersonNummer.personummer("19670712_4696").getType());
    }

    @Test
    public void testSeparator() throws Exception {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR)%100;

        pNo = PersonNummer.personummer(String.format("%02d1212121a", thisYear));
        assertEquals(null, pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("%02d1212-1212", thisYear));
        assertEquals("-", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("%02d1212+1212", thisYear));
        assertEquals("+", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("%02d12121212", thisYear));
        assertEquals("-", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("19%02d1212-1212", thisYear));
        assertEquals("+", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("19%02d1212-1212", thisYear-1));
        assertEquals("+", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("19%02d1212-1212", thisYear+1));
        assertEquals("-", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("19%02d1212+1212", thisYear+1));
        assertEquals("-", pNo.getSeparator());

        pNo = PersonNummer.personummer(String.format("19%02d1212+1212", thisYear-1));
        assertEquals("+", pNo.getSeparator());
    }

    @Test
    public void testCentury() throws Exception {
        pNo = PersonNummer.personummer("6707124696");
        assertEquals(19, pNo.getCentury());

        pNo = PersonNummer.personummer("670712-4696");
        assertEquals(19, pNo.getCentury());

        pNo = PersonNummer.personummer("670712+4696");
        assertEquals(18, pNo.getCentury());

        pNo = PersonNummer.personummer("990912-4696");
        assertEquals(19, pNo.getCentury());

        pNo = PersonNummer.personummer("990912+4696");
        assertEquals(18, pNo.getCentury());

        pNo = PersonNummer.personummer("000912-4696");
        assertEquals(20, pNo.getCentury());

        pNo = PersonNummer.personummer("000912+4696");
        assertEquals(19, pNo.getCentury());

        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR)%100;

        pNo = PersonNummer.personummer(String.format("%02d1212-1212", thisYear));
        assertEquals(20, pNo.getCentury());

        pNo = PersonNummer.personummer(String.format("%02d1212+1212", thisYear));
        assertEquals(19, pNo.getCentury());

        pNo = PersonNummer.personummer(String.format("%02d1212-1212", thisYear+1));
        assertEquals(19, pNo.getCentury());

        pNo = PersonNummer.personummer(String.format("%02d12121212", thisYear+1));
        assertEquals(19, pNo.getCentury());

        pNo = PersonNummer.personummer(String.format("%02d12121212", thisYear));
        assertEquals(20, pNo.getCentury());
    }

    @Test
    public void testYear() throws Exception {
        assertEquals("Wrong year", 67, PersonNummer.personummer("6707124696").getYear());
        assertEquals("Wrong year", 67, PersonNummer.personummer("670712-4696").getYear());
        assertEquals("Wrong year", 67, PersonNummer.personummer("670712+4696").getYear());
        assertEquals("Wrong year", 67, PersonNummer.personummer("196707124696").getYear());
        assertEquals("Wrong year", 67, PersonNummer.personummer("19670712-4696").getYear());
        assertEquals("Wrong year", 67, PersonNummer.personummer("19670712+4696").getYear());
        assertEquals("Wrong year", -1, PersonNummer.personummer("670712 4696").getYear());
    }

    @Test
    public void testMonth() throws Exception {
        pNo = PersonNummer.personummer("6707124696");
        assertEquals("Wrong month", 7, pNo.getMonth());
        assertTrue(pNo.isMonthValid());

        pNo = PersonNummer.personummer("670712-4696");
        assertEquals("Wrong month", 7, pNo.getMonth());
        assertTrue(pNo.isMonthValid());

        pNo = PersonNummer.personummer("670712+4696");
        assertEquals("Wrong month", 7, pNo.getMonth());
        assertTrue(pNo.isMonthValid());

        pNo = PersonNummer.personummer("196707124696");
        assertEquals("Wrong month", 7, pNo.getMonth());
        assertTrue(pNo.isMonthValid());

        pNo = PersonNummer.personummer("19670712-4696");
        assertEquals("Wrong month", 7, pNo.getMonth());
        assertTrue(pNo.isMonthValid());

        pNo = PersonNummer.personummer("19670712+4696");
        assertEquals("Wrong month", 7, pNo.getMonth());
        assertTrue(pNo.isMonthValid());

        pNo = PersonNummer.personummer("670712 4696");
        assertEquals("Wrong month", -1, pNo.getMonth());
        assertFalse(pNo.isMonthValid());

        pNo = PersonNummer.personummer("19652312+4696");
        assertEquals("Wrong month", 23, pNo.getMonth());
        assertFalse(pNo.isMonthValid());
                
        pNo = PersonNummer.personummer("19652312+4696");
        assertEquals("Wrong month", 23, pNo.getMonth());
        assertFalse(pNo.isMonthValid());

        pNo = PersonNummer.personummer("19652312+4696");
        assertEquals("Wrong month", 23, pNo.getMonth());
        assertFalse(pNo.isMonthValid());
    }

    @Test
    public void testDay() throws Exception {
        pNo = PersonNummer.personummer("191212121212");
        assertEquals(12, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("19121212121a");
        assertEquals(-1, pNo.getDay());
        assertFalse(pNo.isDayValid());

        pNo = PersonNummer.personummer("1212121212");
        assertEquals(12, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("121212-1212");
        assertEquals(12, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("1202301212");
        assertEquals(30, pNo.getDay());
        assertFalse(pNo.isDayValid());

        pNo = PersonNummer.personummer("1212311212");
        assertEquals(31, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("1212321212");
        assertEquals(32, pNo.getDay());
        assertFalse(pNo.isDayValid());

        pNo = PersonNummer.personummer("20000228-1212");
        assertEquals(28, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("20000229-1212");
        assertEquals(29, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("20000230-1212");
        assertEquals(30, pNo.getDay());
        assertFalse(pNo.isDayValid());

        pNo = PersonNummer.personummer("20090228-1212");
        assertEquals(28, pNo.getDay());
        assertTrue(pNo.isDayValid());

        pNo = PersonNummer.personummer("20090229-1212");
        assertEquals(29, pNo.getDay());
        assertFalse(pNo.isDayValid());

        pNo = PersonNummer.personummer("20090230-1212");
        assertEquals(30, pNo.getDay());
        assertFalse(pNo.isDayValid());
    }

    @Test
    public void testBirthNumber() {
        assertEquals(121, PersonNummer.personummer("1212121212").getBirthNumber());
        assertEquals(121, PersonNummer.personummer("121212-1212").getBirthNumber());
        assertEquals(121, PersonNummer.personummer("19121212-1212").getBirthNumber());
        assertEquals(121, PersonNummer.personummer("191212121212").getBirthNumber());
        assertEquals(121, PersonNummer.personummer("191212121212").getBirthNumber());
        assertEquals(-1, PersonNummer.personummer("a91212121212").getBirthNumber());
    }


    @Test
    public void testCheckNumber() {
        assertEquals(2, PersonNummer.personummer("1212121212").getCheckNumber());
        assertEquals(2, PersonNummer.personummer("121212-1212").getCheckNumber());
        assertEquals(2, PersonNummer.personummer("19121212-1212").getCheckNumber());
        assertEquals(2, PersonNummer.personummer("191212121212").getCheckNumber());
        assertEquals(2, PersonNummer.personummer("191212121212").getCheckNumber());
        assertEquals(-1, PersonNummer.personummer("a91212121212").getCheckNumber());
    }

    @Test
    public void testCheckNumberValid() {
        pNo = PersonNummer.personummer("1212121212");
        assertTrue(pNo.isCheckNumberValid());

        pNo = PersonNummer.personummer("121212-1212");
        assertTrue(pNo.isCheckNumberValid());

        pNo = PersonNummer.personummer("191212121212");
        assertTrue(pNo.isCheckNumberValid());

        pNo = PersonNummer.personummer("19121212-1212");
        assertTrue(pNo.isCheckNumberValid());

        pNo = PersonNummer.personummer("19121212-1211");
        assertFalse(pNo.isCheckNumberValid());

        pNo = PersonNummer.personummer("19221212-1212");
        assertFalse(pNo.isCheckNumberValid());

        pNo = PersonNummer.personummer("18121212-1212");
        assertTrue(pNo.isCheckNumberValid());
    }

    @Test
    public void testGender() {
        assertEquals(PersonNummer.Gender.MALE, PersonNummer.personummer("1212121212").getGender());
        assertEquals(PersonNummer.Gender.FEMALE, PersonNummer.personummer("121212-1222").getGender());
        assertNull(PersonNummer.personummer("a21212-1222").getGender());
    }

    @Test
    public void testFormat() {
        pNo = PersonNummer.personummer("1212121212");
        assertEquals("1212121212", pNo.getShort());
        assertEquals("121212-1212", pNo.getNormal());
        assertEquals("19121212-1212", pNo.getFull());

        pNo = PersonNummer.personummer("121212-1212");
        assertEquals("1212121212", pNo.getShort());
        assertEquals("121212-1212", pNo.getNormal());
        assertEquals("19121212-1212", pNo.getFull());

        pNo = PersonNummer.personummer("121212+1212");
        assertEquals("181212121212", pNo.getShort());
        assertEquals("121212+1212", pNo.getNormal());
        assertEquals("18121212-1212", pNo.getFull());

        pNo = PersonNummer.personummer("19121212-1212");
        assertEquals("1212121212", pNo.getShort());
        assertEquals("121212-1212", pNo.getNormal());
        assertEquals("19121212-1212", pNo.getFull());

        pNo = PersonNummer.personummer("18121212-1212");
        assertEquals("181212121212", pNo.getShort());
        assertEquals("121212+1212", pNo.getNormal());
        assertEquals("18121212-1212", pNo.getFull());

        pNo = PersonNummer.personummer("18121212+1212");
        assertEquals("181212121212", pNo.getShort());
        assertEquals("121212+1212", pNo.getNormal());
        assertEquals("18121212-1212", pNo.getFull());
    }

    @Test
    public void testFormatInvalid() {
        pNo = PersonNummer.personummer("aaa");
        assertEquals("INVALID [aaa]", pNo.getShort());
        assertEquals("INVALID [aaa]", pNo.getNormal());
        assertEquals("INVALID [aaa]", pNo.getFull());
    }

    @Test
    public void testCalculatedCheckNumber() {
        pNo = PersonNummer.personummer("1212121212");
        assertEquals(2, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("121212-1212");
        assertEquals(2, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("121212+1212");
        assertEquals(2, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("19121212-1212");
        assertEquals(2, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("18121212-1212");
        assertEquals(2, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("18121212+1212");
        assertEquals(2, pNo.getCalculatedCheckNumber());

        pNo = PersonNummer.personummer("1212121412");
        assertEquals(0, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("121212-1412");
        assertEquals(0, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("121212+1412");
        assertEquals(0, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("19121212-1412");
        assertEquals(0, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("18121212-1412");
        assertEquals(0, pNo.getCalculatedCheckNumber());
        pNo = PersonNummer.personummer("18121212+1412");
        assertEquals(0, pNo.getCalculatedCheckNumber());

        pNo = PersonNummer.personummer("aaa");
        assertEquals(-1, pNo.getCalculatedCheckNumber());
    }
}
