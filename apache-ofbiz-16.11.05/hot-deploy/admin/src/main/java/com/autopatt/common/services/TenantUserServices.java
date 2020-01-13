package com.autopatt.common.services;

import com.autopatt.admin.constants.UserStatusConstants;
import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.common.utils.SecurityGroupUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.party.party.PartyHelper;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TenantUserServices {
    public static final String module = TenantUserServices.class.getName();

    /** Get list of users for given tenant
     *
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> getTenantUsers(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> resp = ServiceUtil.returnSuccess();
        String tenantId = (String) context.get("tenantId");
        List<Map> users = new ArrayList<>();

        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegator(tenantId);
        if(UtilValidate.isEmpty(tenantDelegator)) {
            return ServiceUtil.returnError("Invalid tenant Id.");
        }
        // Get Organization Party Id
        String organizationPartyKey = UtilProperties.getPropertyValue("admin.properties","customer.organization.party.key", "ORGANIZATION_PARTY_ID");
        String tenantOrganizationPartyId = EntityUtilProperties.getPropertyValue("general", organizationPartyKey,null, tenantDelegator);
        if(UtilValidate.isEmpty(tenantOrganizationPartyId)) {
            return ServiceUtil.returnError("Unable to find "+organizationPartyKey+" for tenant " + tenantId);
        }

        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyIdFrom", tenantOrganizationPartyId));
        condList.add(EntityCondition.makeCondition("roleTypeIdFrom", "ORGANIZATION_ROLE"));
        condList.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
        condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
        condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("thruDate", null),
                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())),
                EntityOperator.OR);
        condList.add(thruCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);

        List<GenericValue> partyRelationships = null;
        try {
            partyRelationships = EntityQuery.use(tenantDelegator).from("PartyRelationship").where(condition).queryList();
            if(UtilValidate.isNotEmpty(partyRelationships)) {
                for(GenericValue partyRelationship: partyRelationships) {
                    Map<String,Object> userEntry = UtilMisc.toMap();
                    String userPartyId = partyRelationship.getString("partyIdTo");

                    userEntry.put("partyId", userPartyId);
                    userEntry.put("partyName", PartyHelper.getPartyName(tenantDelegator, userPartyId, false));
                    List<GenericValue> userLogins = tenantDelegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", userPartyId), null, false);
                    if(UtilValidate.isNotEmpty(userLogins)) {
                        GenericValue userLoginEntry = userLogins.get(0);
                        userEntry.put("userLogin", userLoginEntry);
                        userEntry.put("userLoginId", userLoginEntry.getString("userLoginId"));

                        if(UtilValidate.isEmpty(userLoginEntry.getString("enabled"))) {
                            userEntry.put("userStatus", UserStatusConstants.INACTIVE); // user hasn't logged in yet
                        } else if("Y".equalsIgnoreCase(userLoginEntry.getString("enabled")) ) {
                            userEntry.put("userStatus", UserStatusConstants.ACTIVE);
                        } else {
                            Timestamp disabledDateTime = userLoginEntry.getTimestamp("disabledDateTime");
                            if( UtilValidate.isEmpty(disabledDateTime) ) {
                                // if no date-time when account will be enabled again, then user is suspended
                                userEntry.put("userStatus", UserStatusConstants.SUSPENDED);
                            } else {
                                userEntry.put("userStatus", UserStatusConstants.LOCKED);
                            }
                        }

                        GenericValue userSecurityGroup = SecurityGroupUtils.getUserActiveSecurityGroup(tenantDelegator, userLoginEntry.getString("userLoginId"));
                        if(UtilValidate.isNotEmpty(userSecurityGroup)) {
                            String userRoleName = userSecurityGroup.getString("description");
                            userEntry.put("roleName", userRoleName);
                        }
                    }
                    users.add(userEntry);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem finding PartyRelationships. ", module);
            return null;
        }

        resp.put("users", users);
        return resp;
    }


    /**
     * Service to get count of tenant users, for given tenant
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> getTenantUsersCount(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> resp = ServiceUtil.returnSuccess();

        String tenantId = (String) context.get("tenantId");
        Delegator tenantDelegator = TenantCommonUtils.getTenantDelegator(tenantId);
        if(UtilValidate.isEmpty(tenantDelegator)) {
            return ServiceUtil.returnError("Invalid tenant Id.");
        }
        // Get Organization Party Id
        String organizationPartyKey = UtilProperties.getPropertyValue("admin.properties","customer.organization.party.key", "ORGANIZATION_PARTY_ID");
        String tenantOrganizationPartyId = EntityUtilProperties.getPropertyValue("general", organizationPartyKey,null, tenantDelegator);
        if(UtilValidate.isEmpty(tenantOrganizationPartyId)) {
            return ServiceUtil.returnError("Unable to find ORGANIZATION_PARTY for tenant " + tenantId);
        }

        Long userCount = 0L;
        // Use a view entity with aggregation (COUNT) to get counts from PartyRelationship
        try {
            List<EntityCondition> condList = new LinkedList<EntityCondition>();
            condList.add(EntityCondition.makeCondition("partyIdFrom", tenantOrganizationPartyId));
            condList.add(EntityCondition.makeCondition("roleTypeIdFrom", "ORGANIZATION_ROLE"));
            condList.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
            condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
            condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
            EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("thruDate", null),
                    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())),
                    EntityOperator.OR);
            condList.add(thruCond);
            EntityCondition condition = EntityCondition.makeCondition(condList);

            List<GenericValue>  partyRelationshipCounts = EntityQuery.use(tenantDelegator).from("PartyRelationshipCount").where(condition).queryList();
            if(UtilValidate.isNotEmpty(partyRelationshipCounts)) {
                GenericValue partyRelCount = partyRelationshipCounts.get(0);
                userCount = partyRelCount.getLong("partyIdTo");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        resp.put("count", userCount);
        return resp;
    }



}
