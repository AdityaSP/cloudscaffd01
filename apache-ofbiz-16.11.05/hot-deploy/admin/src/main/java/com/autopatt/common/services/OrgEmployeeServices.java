package com.autopatt.common.services;

import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OrgEmployeeServices {
    public static final String module = OrgEmployeeServices.class.getName();

    /**
     * Service to remove user from Org
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> removeOrgEmployee(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> resp = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orgEmployeePartyId = (String) context.get("orgEmployeePartyId");
        String organizationPartyKey = UtilProperties.getPropertyValue("admin.properties","customer.organization.party.key", "ORGANIZATION_PARTY_ID");
        String tenantOrganizationPartyId = EntityUtilProperties.getPropertyValue("general", organizationPartyKey,null, delegator);
        try {
            // thruDate the PartyRelationship
            List<EntityCondition> condList = new LinkedList<EntityCondition>();
            condList.add(EntityCondition.makeCondition("partyIdFrom", tenantOrganizationPartyId));
            condList.add(EntityCondition.makeCondition("partyIdTo", orgEmployeePartyId));
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

            List<GenericValue> partyRelationships = EntityQuery.use(delegator).from("PartyRelationship").where(condition).queryList();
            if(UtilValidate.isNotEmpty(partyRelationships)) {
                for(GenericValue partyReln: partyRelationships) {
                    partyReln.set("thruDate", UtilDateTime.nowTimestamp());
                }
                delegator.storeAll(partyRelationships);
            }

            // Delete UserLoginSecurityGroup Assoc too
            String userLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, orgEmployeePartyId);
            List<GenericValue> userLoginSecGroups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId), null, false);
            if(UtilValidate.isNotEmpty(userLoginSecGroups)) {
                for(GenericValue userLoginSecGroup : userLoginSecGroups) {
                    userLoginSecGroup.remove();
                }
            }
            // Disable UserLogin entry
            GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
            if(UtilValidate.isNotEmpty(partyUserLogin)) {
                //partyUserLogin.remove(); // Can't delete due to UserLoginHistory
                partyUserLogin.setString("enabled", "N");
                partyUserLogin.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Error trying to delete user with party id "+ orgEmployeePartyId);
        }
        return resp;
    }
    
}
