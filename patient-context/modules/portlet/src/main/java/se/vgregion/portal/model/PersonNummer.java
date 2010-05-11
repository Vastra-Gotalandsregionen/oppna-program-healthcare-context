package se.vgregion.portal.model;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class PersonNummer {
    public enum Gender {MALE, FEMALE}

    private String century = "19";
    private String year = "";
    private String month = "";
    private String day = "";
    private String birthNumber = "";
    private String checkNumber = "";
    private String separator = "-"; // or "+" if age over 100

    private boolean checkNumberValid = false;
    private boolean monthValid = false;
    private boolean dayValid = false;
    private Gender gender;

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

}
