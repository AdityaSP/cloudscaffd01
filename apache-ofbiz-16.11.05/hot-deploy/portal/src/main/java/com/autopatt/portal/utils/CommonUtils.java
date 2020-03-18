package com.autopatt.portal.utils;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.base.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.apache.ofbiz.security.Security;

public class CommonUtils{

    public final static String module = CommonUtils.class.getName();

    public static boolean getSecurityPermission(HttpServletRequest request, HttpServletResponse response,
                                                 String permissionName, GenericValue userLogin){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = dispatcher.getSecurity();
        if (security.hasPermission(permissionName, userLogin)) {
            return true;
        }
        return false;
    }

    public static HttpServletRequest getResponse(HttpServletRequest request, HttpServletResponse response,
                                                  String info, String message){
        Map<String,Object> data = UtilMisc.toMap();
        data.put("info", info);
        data.put("message", message);
        System.out.println("message =" +message);
        request.setAttribute("data", data);
        return request;
    }
}
