package com.autopatt.portal.events;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import com.autopatt.common.utils.JWTHelper;
import com.autopatt.common.utils.SecurityGroupUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.party.party.PartyHelper;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class UserMgmtEvents {
    public final static String module = UserMgmtEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String createUser(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String securityGroupId = request.getParameter("securityGroupId");

        // Check permission
        Security security = dispatcher.getSecurity();
        if (!security.hasPermission("PORTAL_ADD_USER", userLogin)) {
            request.setAttribute("_ERROR_MESSAGE_", "You do not have permission to add a new user. ");
            return ERROR;
        }

        // check tenant has valid subscription to add new user
        try {
            Map<String, Object> resp = dispatcher.runSync("hasValidSubscriptionToAddUser",
                    UtilMisc.<String, Object>toMap("userLogin", userLogin));
            if (!ServiceUtil.isSuccess(resp)) {
                String errorMessage = (String) resp.get("errorMessage");
                Debug.logError(errorMessage, module);
                request.setAttribute("_ERROR_MESSAGE_", errorMessage);
                return ERROR;
            }
            Boolean hasPermissionToAddUser = (Boolean) resp.get("hasPermission");
            if (!hasPermissionToAddUser) {
                request.setAttribute("_ERROR_MESSAGE_", "Max user count exceeded for the subscription");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to fetch subscription");
            return ERROR;
        }

        // TODO: Validations - check for duplicate email

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

        try {
            // Create Party & Person
            Map<String, Object> createPersonResp = dispatcher.runSync("createPerson", UtilMisc.<String, Object>toMap("firstName", firstName,
                    "lastName", lastName,
                    "userLogin", userLogin));
            if (!ServiceUtil.isSuccess(createPersonResp)) {
                Debug.logError("Error creating new user for " + email, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new user. ");
                return ERROR;
            }
            String partyId = (String) createPersonResp.get("partyId");

            // Create UserLogin
            Map<String, Object> userLoginCtx = UtilMisc.toMap("userLogin", userLogin);
            userLoginCtx.put("userLoginId", email);
            userLoginCtx.put("currentPassword", password);
            userLoginCtx.put("currentPasswordVerify", password);
            userLoginCtx.put("requirePasswordChange", "Y"); // enforce password change for new user
            userLoginCtx.put("partyId", partyId);

            Map<String, Object> createUserLoginResp = dispatcher.runSync("createUserLogin", userLoginCtx);
            if (!ServiceUtil.isSuccess(createUserLoginResp)) {
                Debug.logError("Error creating userLogin for " + email, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new user. ");
                return ERROR;
            }

            // All Org Users should have EMPLOYEE role
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", partyId,
                    "roleTypeId", "EMPLOYEE",
                    "userLogin", userLogin
            );
            Map<String, Object> createPartyRoleResp = dispatcher.runSync("createPartyRole", partyRole);
            if (!ServiceUtil.isSuccess(createPartyRoleResp)) {
                Debug.logError("Error creating party role for " + email, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new user. ");
                return ERROR;
            }

            // Add partyRelationship with ORG Party (once Tenant is ready)
            String organizationPartyKey = UtilProperties.getPropertyValue("admin.properties","customer.organization.party.key", "ORGANIZATION_PARTY_ID");
            String tenantOrganizationPartyId = EntityUtilProperties.getPropertyValue("general", organizationPartyKey, null, delegator);
            Map<String, Object> partyRelationship = UtilMisc.toMap(
                    "partyIdFrom", tenantOrganizationPartyId,
                    "partyIdTo", partyId,
                    "roleTypeIdFrom", "ORGANIZATION_ROLE",
                    "roleTypeIdTo", "EMPLOYEE",
                    "partyRelationshipTypeId", "EMPLOYMENT",
                    "userLogin", UserLoginUtils.getSystemUserLogin(delegator)
            );
            Map<String, Object> createPartyRelationResp = dispatcher.runSync("createPartyRelationship", partyRelationship);
            if (!ServiceUtil.isSuccess(createPartyRelationResp)) {
                Debug.logError("Error creating new Party Relationship between " + tenantOrganizationPartyId + " and "
                        + partyId + " in tenant " + delegator.getDelegatorTenantId(), module);
            }

            // Assign SecurityGroup to user
            GenericValue userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup",
                    UtilMisc.toMap("userLoginId", email,
                            "groupId", securityGroupId,
                            "fromDate", UtilDateTime.nowTimestamp()));
            try {
                userLoginSecurityGroup.create();
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Unable to assign role to the user. ");
                return ERROR;
            }

            // Send Email Notification : sendNewOrgEmployeeEmail
            GenericDelegator mainDelegator = TenantCommonUtils.getMainDelegator();
            String orgPartyId = TenantCommonUtils.getOrgPartyId(mainDelegator, delegator.getDelegatorTenantId());
            Map<String,Object> emailNotificationCtx = UtilMisc.toMap(
                    "userLogin", UserLoginUtils.getSystemUserLogin(mainDelegator),
                    "tenantId", delegator.getDelegatorTenantId(),
                    "employeePartyId", partyId,
                    "employeeEmail", email,
                    "organizationName",PartyHelper.getPartyName(mainDelegator, orgPartyId, false),
                    "employeePartyName", PartyHelper.getPartyName(delegator,partyId, false),
                    "employeePassword", password
            );
            LocalDispatcher mainDispatcher = TenantCommonUtils.getMainDispatcher();
            Map<String, Object> sendEmailNotificationResp = mainDispatcher.runSync("sendNewOrgEmployeeEmail", emailNotificationCtx);
            if (!ServiceUtil.isSuccess(sendEmailNotificationResp)) {
                Debug.logError("Error sending email notification to the user", module);
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ERROR;
        }
        request.setAttribute("createSuccess", "Y");
        return SUCCESS;
    }

    public static String updateUser(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String partyId = request.getParameter("partyId");
        Map<String, Object> inputs = UtilMisc.toMap("partyId", partyId);
        try {
            GenericValue person = delegator.findOne("Person", inputs, false);
            // set new values for firstname, lastname
            person.set("firstName", firstname);
            person.set("lastName", lastname);

            delegator.store(person);

            // Update Security Role
            String securityGroupId = request.getParameter("securityGroupId");
            String partyUserLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, partyId);
            SecurityGroupUtils.updateUserSecurityGroup(delegator, partyUserLoginId, securityGroupId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update the user details.");
            return ERROR;
        }
        request.setAttribute("updateSuccess", "Y");
        return SUCCESS;
    }

    public static String deleteUser(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String partyId = request.getParameter("userPartyId");

        // TODO: Check permission
        try {
            Map<String, Object> removeOrgEmpResp = dispatcher.runSync("removeOrgEmployee",
                    UtilMisc.toMap("userLogin", userLogin,
                            "orgEmployeePartyId", partyId));
            if (!ServiceUtil.isSuccess(removeOrgEmpResp)) {
                request.setAttribute("_ERROR_MESSAGE_", "Error trying to delete user with party id " + partyId);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error trying to delete user with party id " + partyId);
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "User deleted successfully.");
        return SUCCESS;
    }

    public static String updateCompanyDetails(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String orgPartyId = request.getParameter("orgPartyId");
        request.setAttribute("orgPartyId", orgPartyId);
        GenericDelegator mainDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        String organizationName = request.getParameter("organizationName");

        Map<String, Object> inputs = UtilMisc.toMap("partyId", orgPartyId);
        try {
            GenericValue partyGroup = mainDelegator.findOne("PartyGroup", inputs, false);
            partyGroup.set("groupName", organizationName);
            mainDelegator.store(partyGroup);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update the company details.");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "Profile details updated successfully.");
        return SUCCESS;
    }

    public static String activateUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String userPartyId = request.getParameter("partyId");
        request.setAttribute("partyId", userPartyId);

        GenericValue usersLogin = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String userLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, userPartyId);
        try {
            if (UtilValidate.isEmpty(userLoginId)) {
                request.setAttribute("_ERROR_MESSAGE_", "Employee user with id "+ userPartyId+" not found.");
                return ERROR;
            }
            Map<String,Object> updateUserLoginResp = dispatcher.runSync("updateUserLoginSecurity",
                    UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(delegator),
                            "userLoginId", userLoginId,
                            "enabled", "Y",
                            "disabledDateTime", null));

            if(!ServiceUtil.isSuccess(updateUserLoginResp)) {
                request.setAttribute("_ERROR_MESSAGE_", "Error trying to enable user with id "+ userPartyId);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("Success", "N");
            return ERROR;
        }
        request.setAttribute("Success", "Y");
        return SUCCESS;
    }

    public static String suspendUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String userPartyId = request.getParameter("partyId");
        request.setAttribute("partyId", userPartyId);

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String userLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, userPartyId);
        try {
            if (UtilValidate.isEmpty(userLoginId)) {
                request.setAttribute("_ERROR_MESSAGE_", "Employee user with id "+ userPartyId+" not found.");
                return ERROR;
            }
            Map<String,Object> updateUserLoginResp = dispatcher.runSync("updateUserLoginSecurity",
                    UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(delegator), "userLoginId", userLoginId, "enabled", "N"));

            if(!ServiceUtil.isSuccess(updateUserLoginResp)) {
                request.setAttribute("_ERROR_MESSAGE_", "Error trying to suspend user with id "+ userPartyId);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("Success", "N");
            return ERROR;
        }
        request.setAttribute("Success", "Y");
        return SUCCESS;
    }

    public static String sendUserPasswordResetLink(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String userLoginId = request.getParameter("userLoginId");
        String userTenantId = request.getParameter("userTenantId");

        Map<String, Object> result = null;
        try {
            GenericValue employeeUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
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

            String employeePartyId = employeeUserLogin.getString("partyId");
            // Send Email Notification (passwordResetToken)
            GenericDelegator mainDelegator = TenantCommonUtils.getMainDelegator();
            String orgPartyId = TenantCommonUtils.getOrgPartyId(mainDelegator, delegator.getDelegatorTenantId());
            Map<String,Object> emailNotificationCtx = UtilMisc.toMap(
                    "userLogin", UserLoginUtils.getSystemUserLogin(mainDelegator),
                    "tenantId", delegator.getDelegatorTenantId(),
                    "employeePartyId", employeePartyId,
                    "employeeEmail", userLoginId,
                    "organizationName",PartyHelper.getPartyName(mainDelegator, orgPartyId, false),
                    "employeePartyName", PartyHelper.getPartyName(delegator,employeePartyId, false),
                    "passwordResetToken", result.get("token")
            );
            LocalDispatcher mainDispatcher = TenantCommonUtils.getMainDispatcher();
            Map<String, Object> sendEmailNotificationResp = mainDispatcher.runSync("sendEmployeePasswordResetEmail", emailNotificationCtx);
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
            request.setAttribute("Success", "N");
            return ERROR;
        }
        System.out.println(result.get("token"));
        request.setAttribute("_EVENT_MESSAGE_", result.get("token"));
        request.setAttribute("TOKEN", result.get("token"));
        request.setAttribute("Success", "Y");
        return SUCCESS;
    }

    public static String checkEmailAlreadyExists(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String email = request.getParameter("email");
        try {
            // TODO: handle deleted user's email check

            GenericValue person = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", email), false);
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
