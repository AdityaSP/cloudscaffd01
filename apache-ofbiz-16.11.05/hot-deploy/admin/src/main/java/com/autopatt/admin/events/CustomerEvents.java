package com.autopatt.admin.events;

import com.autopatt.admin.utils.NewTenantTransactionLogUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

import java.util.List;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;

public class CustomerEvents {
    public final static String module = CustomerEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String createCustomer(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();

        Debug.log("Initiating the process to onboard new customer", module);
        /*String tenantId = request.getParameter("tenantId");
        String organizationName = request.getParameter("organizationName");
        String contactFirstName = request.getParameter("contactFirstName");
        String contactLastName = request.getParameter("contactLastName");
        String contactEmail = request.getParameter("contactEmail");
        String contactPassword = request.getParameter("contactPassword");
        String sendNotificationToContact = request.getParameter("sendNotificationToContact");*/
        String tenantId = UtilCodec.checkStringForHtmlStrictNone("Tenant Id",request.getParameter("tenantId"),errorList);
        String organizationName = UtilCodec.checkStringForHtmlStrictNone("Organization Name",request.getParameter("organizationName"),errorList);
        String contactFirstName = UtilCodec.checkStringForHtmlStrictNone("Contact First Name",request.getParameter("contactFirstName"),errorList);
        String contactLastName = UtilCodec.checkStringForHtmlStrictNone("Contact Last Name",request.getParameter("contactLastName"),errorList);
        String contactEmail = UtilCodec.checkStringForHtmlStrictNone("Contact Email",request.getParameter("contactEmail"),errorList);
        String contactPassword = UtilCodec.checkStringForHtmlStrictNone("Contact Password",request.getParameter("contactPassword"),errorList);
        String sendNotificationToContact = UtilCodec.checkStringForHtmlStrictNone("Send Notification to Contact",request.getParameter("sendNotificationToContact"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }

        if (UtilValidate.isEmpty(sendNotificationToContact)) sendNotificationToContact = "N";

        if(UtilValidate.isEmpty(tenantId)) {
            Debug.logError("Organization Id is empty, and is required. ", module);
            request.setAttribute("_ERROR_MESSAGE_", "Organization id is required");
            return ERROR;
        }
        tenantId = tenantId.trim();
        tenantId = tenantId.replaceAll("[^\\w]",""); // remove special chars
        try {
            GenericValue tenant = delegator.findOne("Tenant", UtilMisc.toMap("tenantId", tenantId), false);
            if (tenant != null) {
                request.setAttribute("_ERROR_MESSAGE_", "Organization id already exists");
                return ERROR;
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to create customer,Organization id already exists");
            return ERROR;
        }
        Map<String, Object> onboardCustomerResp = null;
        try {

            String newTenantTransactionId = NewTenantTransactionLogUtils.startNewTenantTransaction(dispatcher, tenantId, organizationName);
            //Async call - use status on Org Party to know the result
            dispatcher.runAsync("onboardNewCustomer", UtilMisc.<String, Object>toMap("tenantId", tenantId,
                    "organizationName", organizationName,
                    "contactFirstName", contactFirstName,
                    "contactLastName", contactLastName,
                    "contactEmail", contactEmail,
                    "contactPassword", contactPassword,
                    "sendNotificationToContact", sendNotificationToContact,
                    "transactionId", newTenantTransactionId,
                    "userLogin", userLogin));

            request.setAttribute("transactionId", newTenantTransactionId);

            /*if(!ServiceUtil.isSuccess(onboardCustomerResp)) {
                Debug.logError("Error onboarding new customer with organization Id: " + tenantId, module);
                request.setAttribute("_ERROR_MESSAGE_", "Error onboarding new customer. ");
                return ERROR;
            }*/

        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("success", "N");
        }
        request.setAttribute("success", "Y");
        return SUCCESS;
    }

    // TODO: add that method here.. no need of new Java file always


    public static String UpdateCustomerDetails(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
        //String orgPartyId = request.getParameter("orgPartyId");
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("Org Party Id",request.getParameter("orgPartyId"),errorList);
        request.setAttribute("orgPartyId", orgPartyId);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
       // String organizationName = request.getParameter("organizationName");
        String organizationName = UtilCodec.checkStringForHtmlStrictNone("Organization Name",request.getParameter("organizationName"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        Map<String, Object> inputs = UtilMisc.toMap("partyId", orgPartyId);
        try {
            GenericValue partygroup = delegator.findOne("PartyGroup", inputs, false);
            partygroup.set("groupName", organizationName);
            delegator.store(partygroup);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update the customer details.");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "Profile details updated successfully.");
        return SUCCESS;
    }

    public static String checkIfOrgIdAlreadyExists(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator mainDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        List<String> errorList = new ArrayList<>();
        String tenantId = UtilCodec.checkStringForHtmlStrictNone("Tenant Id",request.getParameter("tenantId"),errorList);
        // String tenantId = request.getParameter("tenantId");
        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        try {
            GenericValue tenant = mainDelegator.findOne("Tenant", UtilMisc.toMap("tenantId", tenantId), false);
            if (tenant == null) {
                request.setAttribute("ORGID_EXISTS", "NO");
            } else {
                request.setAttribute("ORGID_EXISTS", "YES");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Organization id already exists");
            return ERROR;
        }
        request.setAttribute("_EVENT_MESSAGE_", "Available to use");
        return SUCCESS;
    }
}
