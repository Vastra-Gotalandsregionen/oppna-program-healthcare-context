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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PersonNummer implements Serializable {
    public enum Gender {MALE, FEMALE}
    public enum Type {SHORT, NORMAL, FULL_NO, FULL, INVALID}

    private int century;
    private int year;
    private int month;
    private int day;
    private int birthNumber;
    private int checkNumber;
    private String separator; // or "+" if age over 100

    private boolean checkNumberValid = false;
    private boolean monthValid = false;
    private boolean dayValid = false;
    private Gender gender;
    private Type type;
    private String numberText;

    private PersonNummer(String personnummer) {
        setNumberText(personnummer);
    }

    public static PersonNummer personummer(String personnummer) {
        PersonNummer pNo = new PersonNummer(personnummer);
        pNo.initYear();
        pNo.initSeparator();
        pNo.initCentury();
        pNo.initMonth();
        pNo.initDay();
        pNo.initBirthNumber();
        pNo.initCheckNumber();
        pNo.initGender();

        return pNo;
    }

    public String toShort() {
        return String.format("%02d%02d%02d%03d%d", year, month, day, birthNumber, checkNumber);
    }

    public String toNormal() {
        return String.format("%02d%02d%02d%s%03d%d", year, month, day, separator, birthNumber, checkNumber);
    }

    public String toFull() {
        return String.format("%02d%02d%02d%02d-%03d%d", century, year, month, day, birthNumber, checkNumber);
    }


    private void setNumberText(String numberText) {
        this.numberText = numberText;
        this.initType();
    }

    private void initGender() {
        if (type == Type.INVALID) gender = null;
        else gender = (birthNumber%2 == 0) ? Gender.FEMALE : Gender.MALE;
    }

    private void initCheckNumber() {
        if (type == Type.INVALID) {
            checkNumber = -1;
            checkNumberValid = false;
        } else {
            checkNumber = Integer.parseInt(numberText.substring(numberText.length()-1));
            int checkDigit = checkDigitCalculator(toShort());
            checkNumberValid = checkDigit == checkNumber;
        }
    }

    private void initBirthNumber() {
        if (type == Type.INVALID) birthNumber = -1;

        if (type == Type.SHORT) birthNumber = Integer.parseInt(numberText.substring(6, 9));

        if (type == Type.NORMAL) birthNumber = Integer.parseInt(numberText.substring(7, 10));

        if (type == Type.FULL_NO) birthNumber = Integer.parseInt(numberText.substring(8, 11));

        if (type == Type.FULL) birthNumber = Integer.parseInt(numberText.substring(9, 12));
    }

    private void initDay() {
        if (type == Type.INVALID) day = -1;
        
        if (type == Type.SHORT || type == Type.NORMAL) day = Integer.parseInt(numberText.substring(4, 6));

        if (type == Type.FULL_NO || type == Type.FULL) day = Integer.parseInt(numberText.substring(6, 8));

        // is day valid
        String datePart = String.format("%02d%02d%02d%02d", century, year, month, day);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date d = df.parse(datePart);
            String back = df.format(d);

            dayValid = datePart.equals(back);
        } catch (ParseException e) {
            // never happends
            e.printStackTrace();
        }
    }

    private void initMonth() {
        if (type == Type.INVALID) month = -1;

        if (type == Type.SHORT || type == Type.NORMAL) {
            month = Integer.parseInt(numberText.substring(2, 4));
        }

        if (type == Type.FULL_NO || type == Type.FULL) {
            month = Integer.parseInt(numberText.substring(4, 6));
        }

        monthValid = (month > 0 && month < 13) ? true : false;
    }

    private void initSeparator() {
        if (type == Type.INVALID) separator = null;

        if (type == Type.SHORT) separator = "-";

        if (type == Type.NORMAL) separator = numberText.substring(6, 7);

        if (type == Type.FULL_NO || type == Type.FULL) {
            Calendar cal = Calendar.getInstance();
            int thisFullYear = cal.get(Calendar.YEAR);
            int fullYear = Integer.parseInt(numberText.substring(0, 4));

            separator = (thisFullYear - fullYear >= 100) ? "+" : "-";
        }
    }

    /**
     * Use separator and year to determine century - Never use indata directly.
     */
    private void initCentury() {
        if (type == Type.INVALID) century = -1;

        Calendar cal = Calendar.getInstance();
        int thisCentury = cal.get(Calendar.YEAR)/100;
        int thisYear = cal.get(Calendar.YEAR)%100;

        if ("-".equals(separator)) {
            if (year > thisYear) {
                century = thisCentury -1;
            } else {
                century = thisCentury;
            }
        }

        if ("+".equals(separator)) {
            if (year > thisYear) {
                century = thisCentury -2;
            } else {
                century = thisCentury -1;
            }
        }
    }

    private void initYear() {
        if (type == Type.INVALID) {
            year = -1;
        }

        if (type == Type.SHORT || type == Type.NORMAL) {
            year = Integer.parseInt(numberText.substring(0, 2));
        }

        if (type == Type.FULL_NO || type == Type.FULL) {
            year = Integer.parseInt(numberText.substring(2, 4));
        }
    }

    private void initType() {
        Type type;

        switch (numberText.length()) {
            case 10:
                type = numberText.matches("\\d{10}") ? Type.SHORT : Type.INVALID;
                break;
            case 11:
                type = numberText.matches("\\d{6}[-|+]\\d{4}") ? Type.NORMAL : Type.INVALID;
                break;
            case 12:
                type = numberText.matches("\\d{12}") ? Type.FULL_NO : Type.INVALID;
                break;
            case 13:
                type = numberText.matches("\\d{8}[-|+]\\d{4}") ? Type.FULL : Type.INVALID;
                break;
            default:
                type = Type.INVALID;
        }

        this.type = type;
    }

    // valid input:
    // 6509124696
    // 650912-4696
    // 650912+4696
    // 196509124696
    // 19650912-4696
    // 18650912-4696
    // 18650912+4696

    // output:
    // 6509124696 - SHORT
    // 650912-4696 - NORMAL if age < 100
    // 650912+4696 - NORMAL if age > 100
    // 19650912-4696 - LONG
    // 18650912-4696 - LONG

    public static int checkDigitCalculator(String shortFormat) {
        long pnr = Long.parseLong(shortFormat);

        // number sum [0...18]
        int[] numberSum = {0,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9};
        int checkNumber = (int)(pnr % 10);

        int sum = 0;
        for (int i=1; i<=9; i++) {
            pnr = pnr / 10; // strip last digit

            int digit = (int)(pnr % 10);
            int multiplyer = ((i%2)+1); // multiplyer is [2 1 2 1 2 1 2 1 2]

            digit = digit * multiplyer;
            sum = sum + numberSum[digit];
        }

        int checkDigit = 10 - (sum % 10);

        return checkDigit;
    }

    public int getCentury() {
        return century;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getBirthNumber() {
        return birthNumber;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public String getSeparator() {
        return separator;
    }

    public boolean isCheckNumberValid() {
        return checkNumberValid;
    }

    public boolean isMonthValid() {
        return monthValid;
    }

    public boolean isDayValid() {
        return dayValid;
    }

    public Gender getGender() {
        return gender;
    }

    public Type getType() {
        return type;
    }
}
