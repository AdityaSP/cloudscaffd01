package com.autopatt.admin.events;

import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public class AdminMgmtEvents {
    public final static String module = AdminMgmtEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String createAdminUser(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Map<String, Object> createPersonResp = dispatcher.runSync("createPerson",
                    UtilMisc.<String, Object>toMap("firstName", firstName, "lastName", lastName, "userLogin", userLogin));
            if (!ServiceUtil.isSuccess(createPersonResp)) {
                Debug.logError("Error creating new admin user for " + email, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new admin user. ");
                return ERROR;
            }
            String partyId = (String) createPersonResp.get("partyId");

            // userlogin
            Map<String, Object> userLoginCtx = UtilMisc.toMap("userLogin", userLogin);
            userLoginCtx.put("userLoginId", email);
            userLoginCtx.put("currentPassword", password);
            userLoginCtx.put("currentPasswordVerify", password);
            userLoginCtx.put("requirePasswordChange", "Y"); // TODO: change back to Y after implementing password change screen
            userLoginCtx.put("partyId", partyId);

            try {
                GenericValue person = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", email), false);
                if (person != null) {
                    request.setAttribute("_ERROR_MESSAGE_", "Email already exists");
                    return ERROR;
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add user, email already exists");
                return ERROR;
            }

            Map<String, Object> createUserLoginResp = dispatcher.runSync("createUserLogin", userLoginCtx);
            if (!ServiceUtil.isSuccess(createUserLoginResp)) {
                Debug.logError("Error creating userLogin for " + email, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new admin user. ");
                return ERROR;
            }
            //role
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", partyId,
                    "roleTypeId", "AUTOPATT_ADMIN",
                    "userLogin", userLogin
            );
            Map<String, Object> createPartyRoleResp = dispatcher.runSync("createPartyRole", partyRole);
            if (!ServiceUtil.isSuccess(createPartyRoleResp)) {
                Debug.logError("Error creating party role for " + email, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new admin user role. ");
                return ERROR;
            }

            //securitygroup
            GenericValue userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup",
                    UtilMisc.toMap("userLoginId", email,
                            "groupId", "APADMIN_FULLADMIN",
                            "fromDate", UtilDateTime.nowTimestamp()));
            try {
                userLoginSecurityGroup.create();
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Unable to assign security group to the admin user. ");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error trying to create admin user");
            return ERROR;
        }
        request.setAttribute("createSuccess", "Y");
        return SUCCESS;
    }

    public static String updateAdminUser(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String partyId = request.getParameter("partyId");
        Map<String, Object> inputs = UtilMisc.toMap("partyId",partyId );
        try {
            GenericValue person = delegator.findOne("Person", inputs, false);
            person.set("firstName", firstname);
            person.set("lastName", lastname);

            delegator.store(person);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update the admin user details.");
            return ERROR;
        }
        request.setAttribute("updateSuccess", "Y");
        return SUCCESS;
    }
    public static String deleteAdminUser(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String adminPartyId = request.getParameter("adminPartyId");
        System.out.println("Deleting... " + adminPartyId);

        try {
            // 1. Remove PartyRole for this user
            List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", adminPartyId), null, false);
            if(UtilValidate.isNotEmpty(partyRoles)) {
                for(GenericValue partyRole: partyRoles) {
                    partyRole.remove();
                }
            }
            // 2. Get userLoginId for this partyId
            String userLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, adminPartyId);

            // 3. Remove all UserLoginSecurityGroup for this userLoginId
            List<GenericValue> userLoginSecGroups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId), null, false);
            if(UtilValidate.isNotEmpty(userLoginSecGroups)) {
                for(GenericValue userLoginSecGroup : userLoginSecGroups) {
                    userLoginSecGroup.remove();
                }
            }
            // 4. set enabled=N in UserLogin, and disabledDateTime to null
            GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
            if(UtilValidate.isNotEmpty(partyUserLogin)) {
                partyUserLogin.setString("enabled", "N");
                partyUserLogin.set("disabledDateTime", null);
                partyUserLogin.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        request.setAttribute("_EVENT_MESSAGE_", " Admin User deleted successfully.");
        return SUCCESS;
    }

    public static String checkIfEmailAlreadyExists(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator mainDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        String email = request.getParameter("email");
        try {
            // TODO: handle deleted user's email check

            GenericValue person = mainDelegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", email), false);
            if (person == null) {
                request.setAttribute("EMAIL_EXISTS", "NO");
            } else {
                request.setAttribute("EMAIL_EXISTS", "YES");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Email already exists");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "Available to use");
        return SUCCESS;
    }

}
