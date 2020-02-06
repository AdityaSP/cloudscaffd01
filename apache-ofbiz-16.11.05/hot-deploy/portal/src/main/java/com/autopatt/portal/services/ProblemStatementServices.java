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


public class ProblemStatementServices{

    public static final String module = ProblemStatementServices.class.getName();

    public static Map<String, Object> createProblemStatement(DispatchContext ctx, Map<String, ? extends Object> context) {

        Map<String, Object> result = new HashMap<String, Object>();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String problemStatement = (String) context.get("problemStatement");
        String problemDescription = (String) context.get("problemDescription");
        String tag = (String) context.get("tag");
        String createdBy = userLogin.getString("userLoginId");

        String problemStatementId = null;
        try{
            GenericValue newProblemStatement = delegator.makeValue("problemStatementApc");
            /*  newProblemStatement.setNextSeqId();*/
            problemStatementId = delegator.getNextSeqId("Quote");
            newProblemStatement.setString("id", problemStatementId);
            newProblemStatement.setString("problemStatement", problemStatement);
            newProblemStatement.setString("problemDescription", problemDescription);
            newProblemStatement.setString("createdBy", createdBy);
            delegator.create(newProblemStatement);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        String tagsId = null;
        try{
            GenericValue newproblemStatementTags = delegator.makeValue("problemStatementTags");
            tagsId = delegator.getNextSeqId("Quote");
            newproblemStatementTags.setString("id", tagsId);
            /* newproblemStatementTags.setNextSeqId();*/
            newproblemStatementTags.setString("tagName", tag);
            delegator.create(newproblemStatementTags);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }


        try{
            GenericValue  newproblemStatementTagProblem = delegator.makeValue("problemStatementTagProblem");
            String tagProblemId = delegator.getNextSeqId("Quote");
            newproblemStatementTagProblem.setString("id", tagProblemId);
            newproblemStatementTagProblem.setString("tagid", tagsId);
            newproblemStatementTagProblem.setString("problemId", problemStatementId);
            delegator.create(newproblemStatementTagProblem);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return result;
    }




}
