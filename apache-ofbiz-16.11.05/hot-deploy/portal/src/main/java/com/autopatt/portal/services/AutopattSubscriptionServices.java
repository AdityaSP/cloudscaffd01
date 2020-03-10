package com.autopatt.portal.services;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

/**
 * Checks user has valid subscription or not
 */
public class AutopattSubscriptionServices {

    public static final String module = AutopattSubscriptionServices.class.getName();
    private static final String MAX_USER_LOGINS = "maxUserLogins";

    /**
     * Checks user has valid subscription or not
     * implements permissionInterface
     *
     * @param ctx     The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> hasValidSubscriptionCheck(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String tenantId = delegator.getDelegatorTenantId();
        GenericDelegator mainDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

        Map<String,Object> response = UtilMisc.toMap();
        if (UtilValidate.isEmpty(tenantId)) {
            return ServiceUtil.returnFailure("Tenant Id is missing");
        }
        String orgPartyId = TenantCommonUtils.getOrgPartyId(mainDelegator, tenantId);
        if (UtilValidate.isEmpty(orgPartyId)) {
            return ServiceUtil.returnFailure("Org party Id is missing");
        }
        try {
            List<GenericValue> subscriptions = mainDelegator.findByAnd("Subscription", UtilMisc.toMap("partyId", orgPartyId), null, false);
            List<GenericValue> activeSubscriptions = EntityUtil.filterByDate(subscriptions);
            if (UtilValidate.isNotEmpty(activeSubscriptions)) {
                response.put("hasPermission", true);
            } else {
                response.put("hasPermission", false);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Failed to fetch subscription, error: " + e.getMessage());
        }
        return response;
    }

    /**
     * Checks user has valid subscription to add new user
     *
     * @param ctx     The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> hasValidSubscriptionToAddUser(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String tenantId = delegator.getDelegatorTenantId();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator mainDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

        if (UtilValidate.isEmpty(tenantId)) {
            return ServiceUtil.returnFailure("Tenant Id is missing");
        }
        String orgPartyId = TenantCommonUtils.getOrgPartyId(mainDelegator, tenantId);
        if (UtilValidate.isEmpty(orgPartyId)) {
            return ServiceUtil.returnFailure("Org party Id is missing");
        }
        Map<String,Object> response = UtilMisc.toMap();
        response.put("hasPermission", false);

        try {
            List<GenericValue> subscriptions = mainDelegator.findByAnd("Subscription", UtilMisc.toMap("partyId", orgPartyId), null, false);
            List<GenericValue> activeSubscriptions = EntityUtil.filterByDate(subscriptions);
            for (GenericValue subscription : activeSubscriptions) {
                String productId = subscription.getString("productId");
                GenericValue productAttribute = mainDelegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", MAX_USER_LOGINS), false);
                int maxUserLogins = Integer.parseInt(productAttribute.getString("attrValue"));
                Map<String,Object> tenantUsersCountResp = dispatcher.runSync("getTenantUsersCount", UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(mainDelegator), "tenantId", tenantId));
                if(!ServiceUtil.isSuccess(tenantUsersCountResp)) {
                    return ServiceUtil.returnFailure("Unable to fetch users count for tenant : " + tenantId);
                }
                Long tenantUsersCount = (Long) tenantUsersCountResp.get("count");
                if (tenantUsersCount < maxUserLogins) {
                    response.put("hasPermission", true);
                }
            }
        } catch (GenericEntityException | GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Failed to fetch subscription, error: " + e.getMessage());
        }
        return response;
    }


    /**
     * check if given tenantId has a valid subscription
     * This is same as hasValidSubscriptionCheck, but not used as permission-service, instead as a normal service
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> checkForValidSubscription(DispatchContext ctx, Map<String, ? extends Object> context) {
        String tenantId = (String) context.get("tenantId");
        GenericDelegator mainDelegator = TenantCommonUtils.getMainDelegator();

        Map<String,Object> response = UtilMisc.toMap();
        if (UtilValidate.isEmpty(tenantId)) {
            return ServiceUtil.returnFailure("Tenant Id is missing");
        }
        String orgPartyId = TenantCommonUtils.getOrgPartyId(mainDelegator, tenantId);
        if (UtilValidate.isEmpty(orgPartyId)) {
            return ServiceUtil.returnFailure("Org party Id is missing");
        }
        try {
            List<GenericValue> subscriptions = mainDelegator.findByAnd("Subscription", UtilMisc.toMap("partyId", orgPartyId), null, false);
            List<GenericValue> activeSubscriptions = EntityUtil.filterByDate(subscriptions);
            if (UtilValidate.isNotEmpty(activeSubscriptions)) {
                response.put("hasValidSubscription", true);
            } else {
                response.put("hasValidSubscription", false);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Failed to fetch subscription, error: " + e.getMessage());
        }
        return response;
    }

}
