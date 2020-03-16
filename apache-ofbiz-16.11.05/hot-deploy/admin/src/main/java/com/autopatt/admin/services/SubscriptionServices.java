package com.autopatt.admin.services;

import com.autopatt.admin.utils.TenantCommonUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.order.shoppingcart.CartItemModifyException;
import org.apache.ofbiz.order.shoppingcart.ShoppingCart;
import org.apache.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.ofbiz.service.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Product subscription to tenant
 */
public class SubscriptionServices {

    public static final String module = CustomerOnboardingServices.class.getName();
    private static Properties SUBSCRIPTION_PROPERTIES = UtilProperties.getProperties("subscription.properties");

    /**
     * Assigns subscription to tenant
     *
     * @param ctx     The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> assignSubscriptionToTenant(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> sendResp;
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String orgPartyId = (String) context.get("orgPartyId");
        String productId = (String) context.get("productId");
        Timestamp validFrom = (Timestamp) context.get("validFrom");
        Timestamp validTo = (Timestamp) context.get("validTo");

        //load properties
        String productStoreId = SUBSCRIPTION_PROPERTIES.getProperty("autopatt.product.store", "AUTOPATT_STORE");
        String currency = SUBSCRIPTION_PROPERTIES.getProperty("autopatt.currency", "USD");

        Debug.logInfo("Initiating process to assign product " + productId + " subscription to org party " + orgPartyId, module);
        String orderId;
        try {
            ShoppingCart cart = new ShoppingCart(delegator, productStoreId, null, locale, currency);
            try {
                cart.setUserLogin(userLogin, dispatcher);
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            cart.setOrderType("SALES_ORDER");
            cart.setOrderPartyId(orgPartyId);

            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
            ShoppingCartItem productItem = ShoppingCartItem.makeItem(Integer.valueOf(0), product, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, null,
                    null, null, null, null, null, null, null,
                    null, "PRODUCT_ORDER_ITEM", null, null, cart, Boolean.FALSE, Boolean.FALSE, null,
                    Boolean.TRUE, Boolean.TRUE);

            // save the order (new tx)
            Map<String, Object> createResp;
            createResp = dispatcher.runSync("createOrderFromShoppingCart", UtilMisc.toMap("shoppingCart", cart), 90, true);
            if (ServiceUtil.isError(createResp)) {
                return createResp;
            }

            orderId = (String) createResp.get("orderId");
            Map<String, Object> statusChangeRequestMap = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "ORDER_APPROVED", "setItemStatus", "Y", "userLogin", userLogin);
            Map<String, Object> newSttsResult = null;

            newSttsResult = dispatcher.runSync("changeOrderStatus", statusChangeRequestMap);
            if (ServiceUtil.isError(newSttsResult)) {
                return newSttsResult;
            }
            sendResp = ServiceUtil.returnSuccess();
            sendResp.put("orderId", orderId);
        } catch (GenericServiceException | CartItemModifyException | GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Unable to create order, error: " + e.getMessage());
        }

        try {
            GenericValue newSubscription = delegator.makeValue("Subscription");
            newSubscription.set("partyId", orgPartyId);
            newSubscription.set("productId", productId);
            newSubscription.set("orderId", orderId);
            newSubscription.set("fromDate", validFrom);
            if (null != validTo) {
                newSubscription.set("thruDate", validTo);
            }

            Map<String, Object> createSubscriptionMap = ctx.getModelService("createSubscription").makeValid(newSubscription, ModelService.IN_PARAM);
            createSubscriptionMap.put("userLogin", userLogin);

            Map<String, Object> createSubscriptionResult = dispatcher.runSync("createSubscription", createSubscriptionMap);
            if (ServiceUtil.isError(createSubscriptionResult)) {
                return createSubscriptionResult;
            }
            sendResp.put("subscriptionId", createSubscriptionResult.get("subscriptionId"));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Unable to create subscription model, error: " + e.getMessage());
        }
        return sendResp;
    }

    public static Map<String, Object> getSubscriptions(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String orgPartyId = (String) context.get("orgPartyId");
        String status = null == context.get("status") ? "ALL" : (String) context.get("status");
        String productId = null == context.get("productId") ? "ALL" : (String) context.get("productId");
        List<Map> subscriptionsList = new ArrayList<>();
        try {
            Map<String, String> filterMap = new HashMap<>();

            if(UtilValidate.isNotEmpty(orgPartyId)) {
                filterMap.put("partyId", orgPartyId);
            }
            if (!"ALL".equals(productId)) {
                filterMap.put("productId", productId);
            }
            List<GenericValue> subscriptions = delegator.findByAnd("Subscription", filterMap, UtilMisc.toList("-subscriptionId"), false);
            Map<String, Object> resultMap = ServiceUtil.returnSuccess();
            Timestamp moment = UtilDateTime.nowTimestamp();
            if (UtilValidate.isNotEmpty(subscriptions)) {
                for (GenericValue subscription : subscriptions) {
                    Map subscriptionMap = UtilMisc.toMap();
                    java.sql.Timestamp fromDate = subscription.getTimestamp("fromDate");
                    java.sql.Timestamp thruDate = subscription.getTimestamp("thruDate");
                    subscriptionMap.put("id", subscription.getString("subscriptionId"));
                    subscriptionMap.put("productId", subscription.getString("productId"));
                    subscriptionMap.put("fromDate", fromDate);
                    subscriptionMap.put("thruDate", thruDate);
                    subscriptionMap.put("createdDate", subscription.getTimestamp("createdStamp"));
                    if (fromDate != null && fromDate.after(moment)) {
                        subscriptionMap.put("status", "FUTURE");
                    } else if (thruDate != null && thruDate.before(moment)) {
                        subscriptionMap.put("status", "INACTIVE");
                    } else {
                        subscriptionMap.put("status", "ACTIVE");
                    }
                    subscriptionMap.put("orgPartyId", orgPartyId);

                    //filter status
                    if ("ALL".equals(status)) {
                        subscriptionsList.add(subscriptionMap);
                    } else if ("INACTIVE".equals(status) && "INACTIVE".equals(subscriptionMap.get("status"))) {
                        subscriptionsList.add(subscriptionMap);
                    } else if ("ACTIVE".equals(status) && "ACTIVE".equals(subscriptionMap.get("status"))) {
                        subscriptionsList.add(subscriptionMap);
                    } else if ("FUTURE".equals(status) && "FUTURE".equals(subscriptionMap.get("status"))){
                        subscriptionsList.add(subscriptionMap);
                    }
                }
            }
            resultMap.put("subscriptions", subscriptionsList);
            return resultMap;
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Failed to fetch subscription, error: " + e.getMessage());
        }
    }

    public static Map<String, Object> updateSubscriptionThruDate(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> sendResp = null;
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String subscriptionId = (String) context.get("subscriptionId");
        Timestamp validTo = (Timestamp) context.get("validTo");

        try {
            GenericValue subscription = delegator.findOne("Subscription", false, "subscriptionId", subscriptionId);
            if (UtilValidate.isEmpty(subscription)) {
                return ServiceUtil.returnFailure("Subscription not found");
            }
            subscription.set("thruDate", validTo);
            Map<String, Object> updateSubscriptionMap = ctx.getModelService("updateSubscription").makeValid(subscription, ModelService.IN_PARAM);
            updateSubscriptionMap.put("userLogin", userLogin);

            Map<String, Object> updateSubscriptionResult = dispatcher.runSync("updateSubscription", updateSubscriptionMap);
            if (ServiceUtil.isError(updateSubscriptionResult)) {
                return updateSubscriptionResult;
            }
            sendResp = ServiceUtil.returnSuccess();
        } catch (GenericEntityException | GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Failed to fetch subscription, error: " + e.getMessage());
        }
        return sendResp;
    }


}
