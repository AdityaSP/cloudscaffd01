package com.autopatt.admin.events;
import com.autopatt.common.utils.PasswordPolicyHelper;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.webapp.control.LoginWorker;

public class MyProfileEvents {
    public final static String module = MyProfileEvents.class.getName();
    public static String SUCCESS = "success";
    public static String ERROR = "error";

    public static String updateMyProfile(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String firstname=request.getParameter("firstname");
        String lastname=request.getParameter("lastname");
        Map<String, Object> inputs = UtilMisc.toMap("partyId", userLogin.get("partyId"));
        try {
            GenericValue person = delegator.findOne("Person", inputs , false);
            person.set("firstName",firstname);
            person.set("lastName",lastname);
            delegator.store(person);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update the profile details.");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "Profile details updated successfully.");
        return SUCCESS;
    }

    public static String updatePassword(HttpServletRequest request, HttpServletResponse response) {
        List<String> errorList = PasswordPolicyHelper.validatePasswordPolicy(request.getParameter("PASSWORD"));
        if(!errorList.isEmpty()){
            request.setAttribute("info", errorList);
            request.setAttribute("message",ERROR);
            return ERROR;
        }
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String username = userLogin.getString("userLoginId");
        String password = request.getParameter("PASSWORD");
        Map<String, Object> inMap = UtilMisc.<String, Object>toMap("login.username", username, "login.password", password, "locale", UtilHttp.getLocale(request));
        inMap.put("userLoginId", username);
        inMap.put("currentPassword", password);
        inMap.put("newPassword", request.getParameter("newPassword"));
        inMap.put("newPasswordVerify", request.getParameter("newPasswordVerify"));
        Map<String, Object> resultPasswordChange = null;
        resultPasswordChange = LoginWorker.updatePassword(request, response,inMap);
        if (ServiceUtil.isError(resultPasswordChange)) {
            String errorMessage = (String) resultPasswordChange.get(ModelService.ERROR_MESSAGE);
            if (UtilValidate.isNotEmpty(errorMessage)) {
                request.setAttribute("info", errorMessage);
                request.setAttribute("message",ERROR);
                return ERROR;
            }
            request.setAttribute("info", resultPasswordChange.get(ModelService.ERROR_MESSAGE_LIST));
            request.setAttribute("message",ERROR);
            return ERROR;
        }
        request.setAttribute("message",SUCCESS);
        request.setAttribute("info","Password updated successfully");
        return SUCCESS;
    }

}