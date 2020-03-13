package com.autopatt.common.utils;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityUtil;

import java.util.*;

public class SecurityGroupUtils {

    public static final String module = SecurityGroupUtils.class.getName();

    /**
     * Get list of available Security Groups
     * Only Get roles prefixed with AP_
     * @param delegator
     * @return availableSecurityGroups
     */
    public static List<GenericValue> getAvailableSecurityGroups(Delegator delegator) {
        List<GenericValue> availableSecurityGroups = new ArrayList<>();
        try {
            List<GenericValue> securityGroups = delegator.findByAnd("SecurityGroup", null, null, true);
            if (UtilValidate.isNotEmpty(securityGroups)) {
                for(GenericValue group: securityGroups) {
                    if(group.getString("groupId").startsWith("AP_")) {
                        availableSecurityGroups.add(group);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            e.printStackTrace();
        }
        return availableSecurityGroups;
    }

    /** Get One Active Security Group for the User
     *  If more than one found - returns the first entry
     * */
    public static GenericValue getUserActiveSecurityGroup(Delegator delegator, String userLoginId) {
        GenericValue activeSecurityGroup = null;

        List<GenericValue> userSecurityGroups = null;
        try {
            userSecurityGroups = delegator.findByAnd("UserLoginSecurityGroup",
                    UtilMisc.toMap("userLoginId", userLoginId), null, false);
            userSecurityGroups = EntityUtil.filterByDate(userSecurityGroups);

            if(UtilValidate.isNotEmpty(userSecurityGroups)) {
                GenericValue userSecurityGroup = userSecurityGroups.get(0);
                activeSecurityGroup = userSecurityGroup.getRelatedOne("SecurityGroup", false);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return activeSecurityGroup;
    }

    /**
     * Update UserLogin to Security Group association
     * thruDate an existing entry, and create a new entry for new security group id.
     *
     * @param delegator
     * @param userLoginId
     * @param newSecurityGroupId
     */
    public static void updateUserSecurityGroup(Delegator delegator, String userLoginId, String newSecurityGroupId) {
        GenericValue userLoginSecurityGroup = null;
        try {
            List<GenericValue> userSecurityGroups = delegator.findByAnd("UserLoginSecurityGroup",
                    UtilMisc.toMap("userLoginId", userLoginId), null, false);
            userSecurityGroups = EntityUtil.filterByDate(userSecurityGroups);
            if(UtilValidate.isNotEmpty(userSecurityGroups)) {
                userLoginSecurityGroup = userSecurityGroups.get(0);
            }
            if(UtilValidate.isEmpty(userLoginSecurityGroup)) return;

            String existingGroupId = userLoginSecurityGroup.getString("groupId");
            // Change only if currently active group is different from new group-id
            if(!UtilValidate.isEmpty(newSecurityGroupId) && ! newSecurityGroupId.equalsIgnoreCase(existingGroupId)) {
                // thruDate an existing entry
                userLoginSecurityGroup.set("thruDate", UtilDateTime.nowTimestamp());
                userLoginSecurityGroup.store();

                GenericValue newUserLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup",
                        UtilMisc.toMap("userLoginId", userLoginId,
                                "groupId", newSecurityGroupId,
                                "fromDate", UtilDateTime.nowTimestamp()));
                newUserLoginSecurityGroup.create();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

}
