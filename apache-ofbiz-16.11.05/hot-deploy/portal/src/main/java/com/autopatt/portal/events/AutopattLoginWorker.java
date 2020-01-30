package com.autopatt.portal.events;

import com.autopatt.common.utils.PasswordPolicyHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.*;
import org.apache.ofbiz.webapp.WebAppUtil;
import org.apache.ofbiz.webapp.control.LoginWorker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public class AutopattLoginWorker extends LoginWorker{

    public final static String module = AutopattLoginWorker.class.getName();

    public static String SUCCESS = "success";
    public static String ERROR = "error";

    public static String extensionCheckLogin(HttpServletRequest request, HttpServletResponse response) {
        String res = LoginWorker.extensionCheckLogin(request, response);
        if (!SUCCESS.equals(res)) {
            return res;
        }
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin != null && StringUtils.isNotEmpty(userLogin.getString("userLoginId")) && StringUtils.isNotEmpty(sessionId)) {
            try {
                String userLoginId = userLogin.getString("userLoginId");
                GenericValue userLoginSessionInfo = delegator.findOne("UserLoginSessionInfo", false, "userLoginId", userLoginId);
                if (null != userLoginSessionInfo) {
                    String oldSessionId = userLoginSessionInfo.getString("sessionId");
                    if (sessionId.equals(oldSessionId)) {
                        return SUCCESS;
                    }
                    session.invalidate();
                }
                else{
                    //rare case scenario
                    return SUCCESS;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        String errMsg = "You have been logged out since you have logged in from another device or browser.";
        request.setAttribute("_ERROR_MESSAGE_", errMsg);
        return ERROR;
    }

    public static String login(HttpServletRequest request, HttpServletResponse response) {
        String userTenantId = request.getParameter("userTenantId");
        if(UtilValidate.isNotEmpty(userTenantId)) {
            userTenantId = userTenantId.trim();
            String delegatorName = "default#" + userTenantId;
            Delegator tenantDelegator = DelegatorFactory.getDelegator(delegatorName);
            if(UtilValidate.isNotEmpty(tenantDelegator)) {
                LocalDispatcher tenantDispatcher = new GenericDispatcherFactory().createLocalDispatcher("default#" + userTenantId, tenantDelegator);
                setWebContextObjects(request, response, tenantDelegator,tenantDispatcher);
            } else {
                request.setAttribute("_ERROR_MESSAGE_", "Invalid Organization Id");
                return "error";
            }
        } else {
            request.setAttribute("_ERROR_MESSAGE_", "Invalid Organization Id");
            return "error";
        }

        String res = LoginWorker.login(request, response);
        request.setAttribute("USERNAME", request.getParameter("USERNAME"));
        if (!SUCCESS.equals(res)) {
            return res;
        }
        return overridePreviousLogInSession(request) ? SUCCESS : ERROR;
    }

    private static boolean overridePreviousLogInSession(HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin != null && StringUtils.isNotEmpty(userLogin.getString("userLoginId")) && StringUtils.isNotEmpty(sessionId)) {
            try {
                String userLoginId = userLogin.getString("userLoginId");
                GenericValue userLoginSessionInfo = delegator.findOne("UserLoginSessionInfo", false, "userLoginId", userLoginId);
                if (null == userLoginSessionInfo) {
                    GenericValue userAccessToken = delegator.makeValue("UserLoginSessionInfo", UtilMisc.<String, Object>toMap(
                            "userLoginId", userLoginId,
                            "sessionId", sessionId));
                    delegator.create(userAccessToken);
                    return true;
                }
                String currentSessionId = userLoginSessionInfo.getString("sessionId");
                if (currentSessionId.equals(sessionId)) {
                    return true;
                }
                userLoginSessionInfo.setString("sessionId", sessionId);
                delegator.store(userLoginSessionInfo);
                return true;
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                Debug.logError(e, "Exception during storing session id in UserLoginSessionInfo : " + e.getMessage(), module);
            }
        }
        String errMsg = "Exception while managing One Device login feature";
        request.setAttribute("_ERROR_MESSAGE_", errMsg);
        return false;
    }

    public static String changePassword(HttpServletRequest request, HttpServletResponse response) {
        List<String> errorList = PasswordPolicyHelper.validatePasswordPolicy(request.getParameter("PASSWORD"));
        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            return ERROR;
        }
        String res = LoginWorker.login(request, response);
        if (!SUCCESS.equals(res)) {
            boolean requirePasswordChange = "Y".equals(request.getParameter("requirePasswordChange"));
            if(requirePasswordChange) {
                String username = request.getParameter("USERNAME");
                request.setAttribute("USERNAME", username);
            }
            return res;
        }
        return overridePreviousLogInSession(request) ? SUCCESS : ERROR;
    }

    public static String updatePassword(HttpServletRequest request, HttpServletResponse response) {
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
        try {
            resultPasswordChange = dispatcher.runSync("updatePassword", inMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to authenticate with current password");
            return ERROR;
        }
        if (ServiceUtil.isError(resultPasswordChange)) {
            String errorMessage = (String) resultPasswordChange.get(ModelService.ERROR_MESSAGE);
            if (UtilValidate.isNotEmpty(errorMessage)) {
                request.setAttribute("_ERROR_MESSAGE_", errorMessage);
            }
            request.setAttribute("_ERROR_MESSAGE_LIST_", resultPasswordChange.get(ModelService.ERROR_MESSAGE_LIST));
            return ERROR;
        }
        return SUCCESS;
    }


}
