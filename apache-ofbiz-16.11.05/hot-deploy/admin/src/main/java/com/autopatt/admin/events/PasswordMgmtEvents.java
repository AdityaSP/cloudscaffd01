package com.autopatt.admin.events;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import com.autopatt.common.utils.JWTHelper;
import com.autopatt.common.utils.PasswordPolicyHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.party.party.PartyHelper;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;

public class PasswordMgmtEvents {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public final static String module = PasswordMgmtEvents.class.getName();

    public static String sendPasswordResetLink(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        List<String> errorList = new ArrayList<>();

        String emailId = UtilCodec.checkStringForHtmlStrictNone("Email Id",request.getParameter("USERNAME"),errorList);
        //String emailId = request.getParameter("USERNAME");

        Map<String, Object> result = null;
        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        try {
            result = dispatcher.runSync("generatePasswordResetToken", UtilMisc.<String, Object>toMap("userLoginId", emailId));
            if (!ServiceUtil.isSuccess(result)) {
                request.setAttribute("_ERROR_MESSAGE_", result.get("errorMessage"));
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to generate reset token");
            return ERROR;
        }
        System.out.println(result.get("token"));
        request.setAttribute("_EVENT_MESSAGE_", result.get("token"));
        request.setAttribute("TOKEN", result.get("token"));
        return SUCCESS;
    }

    public static String validateJWTToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter("token");
        request.setAttribute("token", token);
        try {
            JWTHelper.parseJWTToken(token);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    public static String resetPassword(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        List<String> errorList = new ArrayList<>();

     /*   String token = request.getParameter("token");
        String newPasswordVerify = request.getParameter("newPasswordVerify");
        String newPassword = request.getParameter("newPassword");*/
        String token = UtilCodec.checkStringForHtmlStrictNone("Token",request.getParameter("token"),errorList);
        String newPasswordVerify = UtilCodec.checkStringForHtmlStrictNone("New Password Verify",request.getParameter("newPasswordVerify"),errorList);
        String newPassword = UtilCodec.checkStringForHtmlStrictNone("New Password",request.getParameter("newPassword"),errorList);
        request.setAttribute("token", token);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }

        List<String> errorList = PasswordPolicyHelper.validatePasswordPolicy(newPassword);
        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            return ERROR;
        }
        Map<String, Object> result = null;
        try {
            result = JWTHelper.parseJWTToken(token);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return ERROR;
        }
        String userLoginId = (String) result.get("id");
        String userTenantId = (String) result.get("subject");
        try {
            Map<String, Object> resetPwdresult = dispatcher.runSync("resetPassword",
                    UtilMisc.<String, String>toMap("userLoginId", userLoginId, "userTenantId", userTenantId,
                            "newPassword", newPassword, "newPasswordVerify", newPasswordVerify));
            if (!ServiceUtil.isSuccess(resetPwdresult)) {
                if (resetPwdresult.containsKey("errorMessage")) {
                    request.setAttribute("_ERROR_MESSAGE_", resetPwdresult.get("errorMessage"));
                } else {
                    request.setAttribute("_ERROR_MESSAGE_LIST_", resetPwdresult.get("errorMessageList"));
                }
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to update the password");
            return ERROR;
        }
        return SUCCESS;
    }

    public static String sendEmployeePasswordResetLink(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String userLoginId = request.getParameter("userLoginId");
        String orgPartyId = request.getParameter("orgPartyId");
        String userTenantId = TenantCommonUtils.getTenantIdForOrgPartyId(delegator, orgPartyId);

        Map<String, Object> result = null;
        try {
            Delegator tenantDelegator = TenantCommonUtils.getTenantDelegator(userTenantId);
            GenericValue employeeUserLogin = tenantDelegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
            if(UtilValidate.isEmpty(employeeUserLogin)) {
                request.setAttribute("_ERROR_MESSAGE_", "Invalid Email Id");
                return ERROR;
            }
            result = dispatcher.runSync("generatePasswordResetToken",
                    UtilMisc.<String, Object>toMap("userLoginId", userLoginId, "userTenantId", userTenantId));
            if (!ServiceUtil.isSuccess(result)) {
                request.setAttribute("_ERROR_MESSAGE_", result.get("errorMessage"));
                return ERROR;
            }

            // Send Email notification
            String employeePartyId = employeeUserLogin.getString("partyId");
            Map<String,Object> emailNotificationCtx = UtilMisc.toMap(
                    "userLogin", UserLoginUtils.getSystemUserLogin(delegator),
                    "tenantId", userTenantId,
                    "employeePartyId", employeePartyId,
                    "employeeEmail", userLoginId,
                    "organizationName", PartyHelper.getPartyName(delegator, orgPartyId, false),
                    "employeePartyName", PartyHelper.getPartyName(tenantDelegator, employeePartyId, false),
                    "passwordResetToken", result.get("token")
            );
            Map<String, Object> sendEmailNotificationResp = dispatcher.runSync("sendEmployeePasswordResetEmail", emailNotificationCtx);
            if (!ServiceUtil.isSuccess(sendEmailNotificationResp)) {
                Debug.logError("Error sending password reset email notification to the user", module);
            }
        } catch (GenericServiceException | GenericEntityException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to generate reset token by admin");
            return ERROR;
        }

        String newPassword = "P@" + RandomStringUtils.random(15, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwyz1234567890@".toCharArray());
        try {
            Debug.logInfo("Sending Password reset email.", module);
            Map<String, Object> resetPwdresult = dispatcher.runSync("resetPassword",
                    UtilMisc.<String, String>toMap("userLoginId", userLoginId, "userTenantId", userTenantId,
                            "newPassword", newPassword, "newPasswordVerify", newPassword));
            if (!ServiceUtil.isSuccess(resetPwdresult)) {
                if (resetPwdresult.containsKey("errorMessage")) {
                    request.setAttribute("_ERROR_MESSAGE_", resetPwdresult.get("errorMessage"));
                } else {
                    request.setAttribute("_ERROR_MESSAGE_LIST_", resetPwdresult.get("errorMessageList"));
                }
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to update the password");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", result.get("token"));
        request.setAttribute("TOKEN", result.get("token"));
        return SUCCESS;
    }

    public static String validatePasswordPolicy(HttpServletRequest request, HttpServletResponse response) {
        String password = request.getParameter("password");
        List<String> errorList = PasswordPolicyHelper.validatePasswordPolicy(password);
        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            return ERROR;
        }
        return SUCCESS;
    }

}
