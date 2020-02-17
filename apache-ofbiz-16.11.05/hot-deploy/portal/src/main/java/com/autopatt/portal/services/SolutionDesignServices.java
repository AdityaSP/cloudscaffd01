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

public class SolutionDesignServices{

    public static final String module = SolutionDesignServices.class.getName();

    public static Map<String, Object> createSolutionDesign(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> result = new HashMap<String, Object>();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String psid = (String) context.get("psid");
        String bpid = (String) context.get("bpid");
        String solutionDesignName = (String) context.get("solutionDesignName");
        String solutionDesignDesc = (String) context.get("solutionDesignDesc");
        String solutionForces = (String) context.get("solutionForces");
        String solutionBeneficiary = (String) context.get("solutionBeneficiary");
        String createdBy = userLogin.getString("userLoginId");
        String status = "created";
        String type = "custom_managed_pattern";

        try{
            GenericValue newSolutionDesign = delegator.makeValue("solutionDesignApc");
            String sdid = delegator.getNextSeqId("Quote");
            newSolutionDesign.setString("id", sdid);
            newSolutionDesign.setString("psid", psid);
            newSolutionDesign.setString("bpid", bpid);
            newSolutionDesign.setString("solutionDesignName", solutionDesignName);
            newSolutionDesign.setString("solutionDesignDesc", solutionDesignDesc);
            newSolutionDesign.setString("solutionForces", solutionForces);
            newSolutionDesign.setString("solutionBeneficiary", solutionBeneficiary);
            newSolutionDesign.setString("createdBy", createdBy);
            newSolutionDesign.setString("status", status);
            newSolutionDesign.setString("type", type);
            delegator.create(newSolutionDesign);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }


}
