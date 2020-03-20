package com.autopatt.admin.events;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;

public class SubscriptionEvents {

    public final static String module = SubscriptionEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    private static Properties SUBSCRIPTION_PROPERTIES = UtilProperties.getProperties("subscription.properties");

    public static String createSubscription(HttpServletRequest request, HttpServletResponse response) {

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
       /* String orgPartyId = request.getParameter("orgPartyId");
        String productId = request.getParameter("productId");
        String validFromStr = request.getParameter("validFrom");
        String validToStr = request.getParameter("validTo");*/
        String orgPartyId = UtilCodec.checkStringForHtmlStrictNone("org Party Id",request.getParameter("orgPartyId"),errorList);
        String productId = UtilCodec.checkStringForHtmlStrictNone("product Id",request.getParameter("productId"),errorList);
        String validFromStr = UtilCodec.checkStringForHtmlStrictNone("valid From",request.getParameter("validFrom"),errorList);
        String validToStr = UtilCodec.checkStringForHtmlStrictNone("valid To",request.getParameter("validTo"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        Debug.log("Received request to assign product " + productId + " subscription to org party " + orgPartyId, module);
        Map<String, Object> resp = null;

        Timestamp validFrom;
        Timestamp validTo = null;
        try {
            TimeZone tz = TimeZone.getDefault();
            if (UtilValidate.isEmpty(validFromStr)) {
                validFrom = UtilDateTime.nowTimestamp();
            } else {
                validFrom = UtilDateTime.stringToTimeStamp(validFromStr, "yyyy-MM-dd", tz, null);
            }
            validFrom = UtilDateTime.getDayStart(validFrom);
            if (UtilValidate.isNotEmpty(validToStr)) {
                validTo = UtilDateTime.stringToTimeStamp(validToStr, "yyyy-MM-dd", tz, null);
                validTo = UtilDateTime.getDayEnd(validTo);
            }
            //check from date is greater than to date
            if (null != validTo && validFrom.after(validTo)) {
                Debug.logError("ValidFrom date is greater than ValidTo date", module);
                request.setAttribute("_ERROR_MESSAGE_", "ValidFrom date is greater than ValidTo date");
                return ERROR;
            }
        } catch (ParseException e) {
            Debug.logError(e, module);
            Debug.logError("Failed to parse From or To date", module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to parse From or To date");
            return ERROR;
        }

        //check any Active subscription already exists for this date range
        String allowMultiActive = SUBSCRIPTION_PROPERTIES.getProperty("autopatt.subscription.allow.multiactive", "false");
        if ("false".equals(allowMultiActive)) {
            if (checkOverlapsActiveSubscription(request, dispatcher, userLogin, orgPartyId, validFrom, validTo)) {
                return ERROR;
            }
        }

        try {
            resp = dispatcher.runSync("assignSubscriptionToTenant",
                    UtilMisc.<String, Object>toMap("orgPartyId", orgPartyId,
                            "productId", productId, "validFrom", validFrom, "validTo", validTo,
                            "userLogin", userLogin));

            if (!ServiceUtil.isSuccess(resp)) {
                Debug.logError("Error assigning product " + productId + " subscription to org party " + orgPartyId, module);
                request.setAttribute("_ERROR_MESSAGE_", resp.get("errorMessage"));
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Error subscribing org party. ");
            return ERROR;
        }
        return SUCCESS;
    }

    public static String revokeSubscription(HttpServletRequest request, HttpServletResponse response) {

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
       /* String subscriptionId = request.getParameter("subscriptionId");
        String validToStr = request.getParameter("validTo");
        String revokeEffective = request.getParameter("revokeEffective");*/
        String subscriptionId = UtilCodec.checkStringForHtmlStrictNone("subscription Id",request.getParameter("subscriptionId"),errorList);
        String validToStr = UtilCodec.checkStringForHtmlStrictNone("valid To",request.getParameter("validTo"),errorList);
        String revokeEffective = UtilCodec.checkStringForHtmlStrictNone("revoke Effective",request.getParameter("revokeEffective"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        Debug.log("Received request to revoke subscription " + subscriptionId, module);
        Map<String, Object> resp = null;

        if ("REVOKE_LATER".equals(revokeEffective) && UtilValidate.isEmpty(validToStr)) {
            Debug.logError("ValidTo date is required if revoking later", module);
            request.setAttribute("_ERROR_MESSAGE_", "ValidTo date is required if revoking later");
            return ERROR;
        }

        Timestamp validTo = null;
        try {
            TimeZone tz = TimeZone.getDefault();
            if ("REVOKE_LATER".equals(revokeEffective)) {
                validTo = UtilDateTime.stringToTimeStamp(validToStr, "yyyy-MM-dd", tz, null);
                validTo = UtilDateTime.getDayEnd(validTo);
                if (UtilDateTime.nowTimestamp().after(validTo)) {
                    request.setAttribute("_ERROR_MESSAGE_", "ValidTo date must be greater than current time");
                    return ERROR;
                }
            } else {
                validTo = UtilDateTime.nowTimestamp();
            }
        } catch (ParseException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to parse ValidTo date");
            return ERROR;
        }

        try {
            resp = dispatcher.runSync("updateSubscriptionThruDate",
                    UtilMisc.<String, Object>toMap("subscriptionId", subscriptionId, "validTo", validTo,
                            "userLogin", userLogin));

            if (!ServiceUtil.isSuccess(resp)) {
                Debug.logError("Error while revoking subscription " + subscriptionId, module);
                request.setAttribute("_ERROR_MESSAGE_", resp.get("errorMessage"));
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Error while revoking subscription tenant.");
            return ERROR;
        }
        return SUCCESS;
    }

    public static String renewSubscription(HttpServletRequest request, HttpServletResponse response) {

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        List<String> errorList = new ArrayList<>();
      /*  String subscriptionId = request.getParameter("subscriptionId");
        String validToStr = request.getParameter("validTo");
        String renewEffective = request.getParameter("renewEffective");*/
        String subscriptionId = UtilCodec.checkStringForHtmlStrictNone("subscription Id",request.getParameter("subscriptionId"),errorList);
        String validToStr = UtilCodec.checkStringForHtmlStrictNone("valid To",request.getParameter("validTo"),errorList);
        String renewEffective = UtilCodec.checkStringForHtmlStrictNone("renew Effective",request.getParameter("renewEffective"),errorList);
        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }

        Debug.log("Received request to renew subscription " + subscriptionId, module);
        Map<String, Object> resp = null;

        if ("RENEW_TILL".equals(renewEffective) && UtilValidate.isEmpty(validToStr)) {
            Debug.logError("ValidTo date is required if not renewing forever", module);
            request.setAttribute("_ERROR_MESSAGE_", "ValidTo date is required if not renewing forever");
            return ERROR;
        }

        Timestamp validTo = null;
        try {
            TimeZone tz = TimeZone.getDefault();
            if ("RENEW_TILL".equals(renewEffective)) {
                validTo = UtilDateTime.stringToTimeStamp(validToStr, "yyyy-MM-dd", tz, null);
                validTo = UtilDateTime.getDayEnd(validTo);
                if (UtilDateTime.nowTimestamp().after(validTo)) {
                    request.setAttribute("_ERROR_MESSAGE_", "ValidTo date must be greater than current time");
                    return ERROR;
                }
            }
        } catch (ParseException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Failed to parse ValidTo date");
            return ERROR;
        }

        //check any Active subscription already exists for this date range
        String allowMultiActive = SUBSCRIPTION_PROPERTIES.getProperty("autopatt.subscription.allow.multiactive", "false");
        if ("false".equals(allowMultiActive)) {
            try {
                GenericValue subscription = delegator.findOne("Subscription", false, "subscriptionId", subscriptionId);
                if (null == subscription) {
                    request.setAttribute("_ERROR_MESSAGE_", "Subscription not found");
                    return ERROR;
                }
                String orgPartyId = subscription.getString("partyId");
                Timestamp fromDate = subscription.getTimestamp("fromDate");
                if (checkOverlapsActiveSubscription(request, dispatcher, userLogin, orgPartyId, fromDate, validTo)) {
                    return ERROR;
                }
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Failed to check multi active subscription feature");
                return ERROR;
            }

        }

        try {
            resp = dispatcher.runSync("updateSubscriptionThruDate",
                    UtilMisc.<String, Object>toMap("subscriptionId", subscriptionId, "validTo", validTo,
                            "userLogin", userLogin));

            if (!ServiceUtil.isSuccess(resp)) {
                Debug.logError("Error while renewing subscription " + subscriptionId, module);
                request.setAttribute("_ERROR_MESSAGE_", resp.get("errorMessage"));
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Error while renewing subscription tenant.");
            return ERROR;
        }
        return SUCCESS;
    }

    public static boolean checkOverlapsActiveSubscription(HttpServletRequest request, LocalDispatcher dispatcher, GenericValue userLogin, String orgPartyId, Timestamp validFrom, Timestamp validTo) {
        try {
            Map<String, Object> respActSubs = dispatcher.runSync("getSubscriptions",
                    UtilMisc.<String, Object>toMap("orgPartyId", orgPartyId, "status", "ACTIVE", "productId", null, "userLogin", userLogin));
            if (ServiceUtil.isSuccess(respActSubs)) {
                List<Map> activeSubscriptionList = (List<Map>) respActSubs.get("subscriptions");
                if (!CollectionUtils.isEmpty(activeSubscriptionList)) {
                    for (Map activeSubscription : activeSubscriptionList) {
                        Timestamp fromDate = activeSubscription.containsKey("fromDate") ? (Timestamp) activeSubscription.get("fromDate") : null;
                        Timestamp thruDate = activeSubscription.containsKey("thruDate") ? (Timestamp) activeSubscription.get("thruDate") : null;
                        if ((fromDate.before(validFrom) && (null == thruDate || thruDate.after(validFrom)))
                                || (fromDate.after(validFrom) && (null == validTo || fromDate.before(validTo)))) {
                            request.setAttribute("_ERROR_MESSAGE_", "Active subscription already exists for this date range");
                            return true;
                        }
                    }
                }
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", "Error while reading active subscriptions. ");
            return true;
        }
        return false;
    }

    public static String ajaxDeleteSubscription(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        List<String> errorList = new ArrayList<>();
        //String subscriptionId = request.getParameter("subscriptionId");
        String subscriptionId = UtilCodec.checkStringForHtmlStrictNone("Consequences",request.getParameter("subscriptionId"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        try {
            GenericValue subscription = delegator.findOne("Subscription", UtilMisc.toMap("subscriptionId", subscriptionId),false);
            if (!UtilValidate.isEmpty(subscription)) {
                subscription.remove();
            }
        } catch (GenericEntityException ex) {
            ex.printStackTrace();
            request.setAttribute("success", "N");
        }
        request.setAttribute("success", "Y");
        return SUCCESS;
    }
}
