package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/12/2015.
 * Used to validate user and club information
 */
public class Validation {

    public static boolean isValidPassword(String pass){
        // 6 < length < 17
        // must contain one letter, one number, one special char
        int maxLength = 17, minLength = 6;

        int length = pass.length();
        if(length < minLength || length > maxLength){
            return false;
        }

        if (!pass.matches("[0-9]+") || !pass.matches("[a-z][A-Z]+") || !pass.matches("\\p{Punct}+")){
            return false;
        }
        return true;
    }

    public static boolean isValidUsername(String user){

        //must be alphanumeric with underscores, spaces, periods, dashes
        int maxLength = 18, minLength = 3;

        int length = user.length();
        if(length < minLength || length > maxLength){
            return false;
        }

        if(user.matches("^([A-Za-z0-9 -_.])")){
            return false;
        }
        return true;
    }

}
