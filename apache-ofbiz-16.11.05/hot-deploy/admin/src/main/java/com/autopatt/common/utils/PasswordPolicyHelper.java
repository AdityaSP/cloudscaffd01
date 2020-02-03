package com.autopatt.common.utils;

import org.apache.ofbiz.base.util.UtilProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordPolicyHelper {

    private static Properties PORTAL_PROPERTIES = UtilProperties.getProperties("admin.properties");
    private static String PWD_MIN_LENGTH = PORTAL_PROPERTIES.getProperty("password.min.length", "8");

    public static List<String> validatePasswordPolicy(String password){
        List<String> errorList = validatePasswordPattern(password);
        boolean result = PasswordBlackList.checkBlackListHasPassword(password);
        if(result){
            errorList.add("Commonly used password is not allowed");
        }
        return errorList;
    }

    public static List<String> validatePasswordPattern(String password) {
        List<String> errorList = new ArrayList<>();
        if(null == password){
            errorList.add("Password cannot be empty");
            return errorList;
        }
        if (password.length() < Integer.parseInt(PWD_MIN_LENGTH)) {
            errorList.add("Password should contain at-least 8 characters");
        }
        if (!matchPattern("^(?=.*[!@#\\$%\\^&\\*]).{1,}", password)) {
            errorList.add("At-least One Special character is required");
        }
        if (!matchPattern("^(?=.*[A-Z]).{1,}", password)) {
            errorList.add("Password should contain at-least one Upper Case character");
        }
        return errorList;
    }

    public static boolean matchPattern(String passwordPattern, String password) {
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
