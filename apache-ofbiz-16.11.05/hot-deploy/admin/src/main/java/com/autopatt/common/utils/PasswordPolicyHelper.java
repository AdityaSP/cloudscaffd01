package com.autopatt.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordPolicyHelper {

    public static List<String> validatePasswordPolicy(String password){
        List<String> errorList = validatePasswordPattern(password);
        boolean result = PasswordBlackList.checkBackListHasPassword(password);
        if(result){
            errorList.add("Password is present in blacklist");
        }
        return errorList;
    }

    public static List<String> validatePasswordPattern(String password) {
        List<String> errorList = new ArrayList<>();
        if(null == password){
            errorList.add("Password is empty");
            return errorList;
        }
        if (password.length() < 8) {
            errorList.add("Password length is less than 8");
        }
        if (!matchPattern("^(?=.*[!@#\\$%\\^&\\*]).{1,}", password)) {
            errorList.add("Special character is mandatory");
        }
        if (!matchPattern("^(?=.*[A-Z]).{1,}", password)) {
            errorList.add("At-least one capital letter is mandatory");
        }
        return errorList;
    }

    public static boolean matchPattern(String passwordPattern, String password) {
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
