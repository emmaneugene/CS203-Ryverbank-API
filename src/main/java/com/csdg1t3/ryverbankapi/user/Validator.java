package com.csdg1t3.ryverbankapi.user;

/**
* Validator class that provides NRIC and phone number validation
*/
public class Validator {
    public static boolean validateNRIC(String nric) {
        // NRIC must be 9 characters long
        if (nric.length() != 9) {
            return false;
        }
        // NRIC must begin with S,T, F or G
        nric = nric.toUpperCase();
        if (!"STFG".contains(nric.substring(0, 1))) {
            return false;
        }
       // Calculate checksum
        int checksum = 0;
        int nricDigits;
        try {
            nricDigits = Integer.parseInt(nric.substring(1,8));
        } catch (NumberFormatException e) {
            return false;
        }
        int[] multiples = {2, 3, 4, 5, 6, 7, 2};
        for (int multiple : multiples) {
            checksum += multiple * (nricDigits % 10);
            nricDigits /= 10;
        }
        if (nric.charAt(0) == 'T' || nric.charAt(0) == 'G') {
            checksum += 4;
        }
        // Validate checksum
        int remainder = checksum % 11;
        char[] outputs;
        if (nric.charAt(0) == 'S' || nric.charAt(0) == 'T') {
            outputs = new char[]{'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        } else {
            outputs = new char[]{'X', 'W', 'U', 'T', 'R', 'Q', 'P', 'N', 'M', 'L', 'K'};
        }

        return outputs[remainder] == nric.charAt(8);
    }

    public static boolean validatePhoneno(String phoneNo) {
        // Phone number can only be 8 characters long
        if (phoneNo.length() != 8) {
            return false;
        }
        // All characters must be digits
        for (char c : phoneNo.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        // First digit must be 9 or 8
        if (phoneNo.charAt(0) != '9' && phoneNo.charAt(0) != '8') {
            return false;
        }
        return true;
    }
}