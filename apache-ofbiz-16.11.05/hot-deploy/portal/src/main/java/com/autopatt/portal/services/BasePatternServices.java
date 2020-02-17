package com.autopatt.portal.services;

import com.autopatt.admin.constants.SecurityGroupConstants;
import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.transaction.TransactionUtil;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.party.party.PartyHelper;
import org.apache.ofbiz.service.*;
import org.codehaus.plexus.util.FastMap;

import java.sql.Timestamp;
import java.util.*;

import java.util.List;
import java.util.Map;

public class BasePatternServices{

    public static final String module = BasePatternServices.class.getName();

    public static Map<String, Object> createBasePattern(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> result = new HashMap<String, Object>();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String psid = (String) context.get("psid");
        String baseName = (String) context.get("baseName");
        String baseDescription = (String) context.get("baseDescription");

        String createdBy = userLogin.getString("userLoginId");
        String status = "created";
        String type = "custom_managed_pattern";

        try{
            GenericValue newBasePattern = delegator.makeValue("basePatternApc");
            String bpid = delegator.getNextSeqId("Quote");
            newBasePattern.setString("id", bpid);
            newBasePattern.setString("psid", psid);
            newBasePattern.setString("baseName", baseName);
            newBasePattern.setString("baseDescription", baseDescription);
            newBasePattern.setString("createdBy", createdBy);
            newBasePattern.setString("status", status);
            newBasePattern.setString("type", type);
            delegator.create(newBasePattern);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }




}
