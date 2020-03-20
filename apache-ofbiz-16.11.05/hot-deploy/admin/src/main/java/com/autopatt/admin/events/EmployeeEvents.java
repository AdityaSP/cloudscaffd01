package com.autopatt.admin.events;
import com.autopatt.common.utils.SecurityGroupUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.autopatt.admin.utils.*;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.owasp.esapi.User;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;

public class EmployeeEvents {
    public final static String module = EmployeeEvents.class.getName();
    public static String SUCCESS = "success";
    public static String ERROR = "error";

    public static String createEmployee(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org Party Id",request.getParameter("orgPartyId"),errorList);
        //String orgPartyId = request.getParameter("orgPartyId");
        request.setAttribute("orgPartyId", orgPartyId);

        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegatorByOrgPartyId(orgPartyId);
        LocalDispatcher tenantDispatcher = TenantCommonUtils.getTenantDispatcherByOrgPartyId(orgPartyId);

        /*String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String empEmail = request.getParameter("email");
        String empPassword = request.getParameter("empPassword");
        String securityGroupId = request.getParameter("securityGroupId");*/
        String firstName = UtilCodec.checkStringForHtmlStrictNone("First Name",request.getParameter("firstName"),errorList);
        String lastName = UtilCodec.checkStringForHtmlStrictNone("Last Name",request.getParameter("lastName"),errorList);
        String empEmail = UtilCodec.checkStringForHtmlStrictNone("Email",request.getParameter("email"),errorList);
        String empPassword = UtilCodec.checkStringForHtmlStrictNone("Emp Password",request.getParameter("empPassword"),errorList);
        String securityGroupId = UtilCodec.checkStringForHtmlStrictNone("Security Group Id",request.getParameter("securityGroupId"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        // Use system userlogin to perform operations which require authorized user
        GenericValue sysUserLogin = UserLoginUtils.getSystemUserLogin(tenantDelegator);

        // 1. Create Party & Person
        try {
            Map<String, Object> createPersonResp = tenantDispatcher.runSync("createPerson", UtilMisc.<String, Object>toMap("firstName", firstName,
                    "lastName", lastName,
                    "userLogin", sysUserLogin));
            if (!ServiceUtil.isSuccess(createPersonResp)) {
                Debug.logError("Error creating new employee user for " + empEmail, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new employee user. ");
                return ERROR;
            }
            String partyId = (String) createPersonResp.get("partyId");

            // checking email
            try {
                GenericValue person = tenantDelegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", empEmail), false);
                if (person != null) {
                    request.setAttribute("_ERROR_MESSAGE_", "Email already exists");
                    return ERROR;
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add user, email already exists");
                return ERROR;
            }

            // 2. Create UserLogin
            Map<String, Object> userLoginCtx = UtilMisc.toMap("userLogin", sysUserLogin);
            userLoginCtx.put("userLoginId", empEmail);
            userLoginCtx.put("currentPassword", empPassword);
            userLoginCtx.put("currentPasswordVerify", empPassword);
            userLoginCtx.put("requirePasswordChange", "Y"); // enforce password change for new user
            userLoginCtx.put("partyId", partyId);

            Map<String, Object> createUserLoginResp = tenantDispatcher.runSync("createUserLogin", userLoginCtx);
            if (!ServiceUtil.isSuccess(createUserLoginResp)) {
                Debug.logError("Error creating employee userLogin for " + empEmail, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new employee user. ");
                return ERROR;
            }

            // 3. Add Role
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", partyId,
                    "roleTypeId", "EMPLOYEE",
                    "userLogin", sysUserLogin
            );
            Map<String, Object> createPartyRoleResp = tenantDispatcher.runSync("createPartyRole", partyRole);
            if (!ServiceUtil.isSuccess(createPartyRoleResp)) {
                Debug.logError("Error creating party role for employee" + empEmail, module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to add new employee user. ");
                return ERROR;
            }

            // 4. Add partyRelationship with ORG Party --- this is little complex, let me know
            String organizationPartyKey = UtilProperties.getPropertyValue("admin.properties","customer.organization.party.key", "ORGANIZATION_PARTY_ID");
            String tenantOrganizationPartyId = EntityUtilProperties.getPropertyValue("general", organizationPartyKey, null, tenantDelegator);
            Map<String, Object> partyRelationship = UtilMisc.toMap(
                    "partyIdFrom", tenantOrganizationPartyId,
                    "partyIdTo", partyId,
                    "roleTypeIdFrom", "ORGANIZATION_ROLE",
                    "roleTypeIdTo", "EMPLOYEE",
                    "partyRelationshipTypeId", "EMPLOYMENT",
                    "userLogin", sysUserLogin
            );
            Map<String, Object> createPartyRelationResp = tenantDispatcher.runSync("createPartyRelationship", partyRelationship);
            if (!ServiceUtil.isSuccess(createPartyRelationResp)) {
                Debug.logError("Error creating new employee Party Relationship between " + tenantOrganizationPartyId + " and "
                        + partyId + " in tenant " + tenantDelegator.getDelegatorTenantId(), module);
            }

            // 5. Assign SecurityGroup to user
            GenericValue userLoginSecurityGroup = tenantDelegator.makeValue("UserLoginSecurityGroup",
                    UtilMisc.toMap("userLoginId", empEmail,
                            "groupId", securityGroupId,
                            "fromDate", UtilDateTime.nowTimestamp()));
            try {
                userLoginSecurityGroup.create();
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Unable to assign role to the user. ");
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

    public static String updateEmployee(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
        //String orgPartyId = request.getParameter("orgPartyId");
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org party id",request.getParameter("orgPartyId"),errorList);
        request.setAttribute("orgPartyId", orgPartyId);

        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegatorByOrgPartyId(orgPartyId);

        /*String firstname=request.getParameter("firstname");
        String lastname=request.getParameter("lastname");
        String partyId = request.getParameter("partyId");*/
        String firstname = UtilCodec.checkStringForHtmlStrictNone("First name",request.getParameter("firstname"),errorList);
        String lastname = UtilCodec.checkStringForHtmlStrictNone("Last name",request.getParameter("lastname"),errorList);
        String partyId = UtilCodec.checkStringForHtmlStrictNone("Party Id",request.getParameter("partyId"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        Map<String, Object> inputs = UtilMisc.toMap("partyId", partyId); // party id should come from request
        try {
            GenericValue person = tenantDelegator.findOne("Person", inputs , false);
            person.set("firstName",firstname);
            person.set("lastName",lastname);
            tenantDelegator.store(person);

            // Update Security Role
            String securityGroupId = request.getParameter("securityGroupId");
            String partyUserLoginId = UserLoginUtils.getUserLoginIdForPartyId(tenantDelegator, partyId);
            SecurityGroupUtils.updateUserSecurityGroup(tenantDelegator, partyUserLoginId, securityGroupId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update the employee details.");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "employee details updated successfully.");
        return SUCCESS;
    }

    /**
     * Suspend an Org employee user
     * @param request
     * @param response
     * @return
     */
    public static String suspendEmployee(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
        //String orgPartyId = request.getParameter("orgPartyId");
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org Party Id",request.getParameter("orgPartyId"),errorList);
        request.setAttribute("orgPartyId", orgPartyId);

        //String orgEmployeePartyId = request.getParameter("orgEmployeePartyId");
        String orgEmployeePartyId = UtilCodec.checkStringForHtmlStrictNone("Org Employee Party Id",request.getParameter("orgEmployeePartyId"),errorList);
        request.setAttribute("orgEmployeePartyId", orgEmployeePartyId);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        //TODO: Add user permission check

        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegatorByOrgPartyId(orgPartyId);
        LocalDispatcher tenantDispatcher = TenantCommonUtils.getTenantDispatcherByOrgPartyId(orgPartyId);
        String userLoginId = UserLoginUtils.getUserLoginIdForPartyId(tenantDelegator, orgEmployeePartyId);
        try {
            if (UtilValidate.isEmpty(userLoginId)) {
                request.setAttribute("_ERROR_MESSAGE_", "Employee user with id "+ orgEmployeePartyId+" not found.");
                return ERROR;
            }
            Map<String,Object> updateUserLoginResp = tenantDispatcher.runSync("updateUserLoginSecurity",
                    UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator), "userLoginId", userLoginId, "enabled", "N"));

            if(!ServiceUtil.isSuccess(updateUserLoginResp)) {
                request.setAttribute("_ERROR_MESSAGE_", "Error trying to suspend user with id "+ orgEmployeePartyId);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error trying to suspend user with id "+ orgEmployeePartyId);
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "User suspending successfully.");
        return SUCCESS;
    }


    /**
     * Enable an inactive/suspended user login id
     * @param request
     * @param response
     * @return
     */
    public static String activateEmployee(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
        //String orgPartyId = request.getParameter("orgPartyId");
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org Party Id",request.getParameter("orgPartyId"),errorList);
        request.setAttribute("orgPartyId", orgPartyId);

        //String orgEmployeePartyId = request.getParameter("orgEmployeePartyId");
        String orgEmployeePartyId = UtilCodec.checkStringForHtmlStrictNone("Org Employee Party Id",request.getParameter("orgEmployeePartyId"),errorList);
        request.setAttribute("orgEmployeePartyId", orgEmployeePartyId);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        //TODO: Add user permission check

        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegatorByOrgPartyId(orgPartyId);
        LocalDispatcher tenantDispatcher = TenantCommonUtils.getTenantDispatcherByOrgPartyId(orgPartyId);
        String userLoginId = UserLoginUtils.getUserLoginIdForPartyId(tenantDelegator, orgEmployeePartyId);
        try {
            if (UtilValidate.isEmpty(userLoginId)) {
                request.setAttribute("_ERROR_MESSAGE_", "Employee user with id "+ orgEmployeePartyId+" not found.");
                return ERROR;
            }
            Map<String,Object> updateUserLoginResp = tenantDispatcher.runSync("updateUserLoginSecurity",
                    UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator),
                            "userLoginId", userLoginId,
                            "enabled", "Y",
                            "disabledDateTime", null));

            if(!ServiceUtil.isSuccess(updateUserLoginResp)) {
                request.setAttribute("_ERROR_MESSAGE_", "Error trying to enable user with id "+ orgEmployeePartyId);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error trying to enable user with id "+ orgEmployeePartyId);
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "User enabled successfully.");
        return SUCCESS;
    }

    /**
     * Delete an Org User - by removing PartyRelationship & UserLogin
     * @param request
     * @param response
     * @return
     */
    public static String ajaxDeleteOrgUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
        //String orgPartyId = request.getParameter("orgPartyId");
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org Party Id",request.getParameter("orgPartyId"),errorList);
        request.setAttribute("orgPartyId", orgPartyId);
        //String orgEmployeePartyId = request.getParameter("orgEmployeePartyId");
        String orgEmployeePartyId = UtilCodec.checkStringForHtmlStrictNone("Org Employee Party Id",request.getParameter("orgEmployeePartyId"),errorList);
        request.setAttribute("orgEmployeePartyId", orgEmployeePartyId);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        //TODO: Add user permission check
        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegatorByOrgPartyId(orgPartyId);
        LocalDispatcher tenantDispatcher = TenantCommonUtils.getTenantDispatcherByOrgPartyId(orgPartyId);
        try {
            Map<String,Object> removeOrgEmpResp = tenantDispatcher.runSync("removeOrgEmployee",
                    UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator),
                            "orgEmployeePartyId", orgEmployeePartyId));

            if(!ServiceUtil.isSuccess(removeOrgEmpResp)) {
                request.setAttribute("_ERROR_MESSAGE_", "Error trying to delete user with id "+ orgEmployeePartyId);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error trying to delete user with party id "+ orgEmployeePartyId);
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "User deleted successfully.");
        return SUCCESS;
    }

    public static String checkEmailForEmp(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        List<String> errorList = new ArrayList<>();
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org Party Id",request.getParameter("orgPartyId"),errorList);
       // String orgPartyId = request.getParameter("orgPartyId");
        request.setAttribute("orgPartyId", orgPartyId);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegatorByOrgPartyId(orgPartyId);

        String email = request.getParameter("email");
        try {
            GenericValue person = tenantDelegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", email),false);
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