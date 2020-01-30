package com.autopatt.common.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.ofbiz.base.location.FlexibleLocation;
import org.apache.ofbiz.base.util.Debug;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PasswordBlackList {

    public final static String module = JWTHelper.class.getName();
    public final static List<String> passwordList = new ArrayList<>();

    public static boolean checkBackListHasPassword(String password) {
        if (passwordList.isEmpty()) {
            loadPasswordBackListToFile();
        }
        if (null == password) {
            return false;
        }
        password = password.toLowerCase();
        for (String value : passwordList) {
            if (password.indexOf(value) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static void loadPasswordBackListToFile() {
        LineIterator it = null;
        try {
            URL url = FlexibleLocation.resolveLocation("password_blacklist.txt", null);
            it = FileUtils.lineIterator(new File(url.getFile()), "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                passwordList.add(line.toLowerCase());
            }
        } catch (IOException e) {
            Debug.logError(e, module);
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

}
