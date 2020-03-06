package com.autopatt.portal.events;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.GenericValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class DashboardEvents
{
    public final static String module = ProblemStatementEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static String getAPCDetailsInCount(HttpServletRequest request, HttpServletResponse response)
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Long> data = UtilMisc.toMap();
        try {

            Long createdBasePatternCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("basePatternApc")
                    .where("status","created")
                    .queryCount();
            if (createdBasePatternCount != null) {
                data.put("createdBasePatternCount",createdBasePatternCount);
            } else {
                data.put("createdBasePatternCount",null);
            }

            Long underDevelopmentPatternCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("basePatternApc")
                    .where("status","Under-Development")
                    .queryCount();
            if (underDevelopmentPatternCount != null) {
                data.put("underDevelopmentPatternCount",underDevelopmentPatternCount);
            } else {
                data.put("underDevelopmentPatternCount",null);
            }

            Long approvedBasePatternCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("basePatternApc")
                    .where("status","approved")
                    .queryCount();
            if (approvedBasePatternCount != null) {
                data.put("approvedBasePatternCount",approvedBasePatternCount);
            } else {
                data.put("approvedBasePatternCount",null);
            }

            Long createdSolutionDesignCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("solutionDesignApc")
                    .where("status","created")
                    .queryCount();
            if (createdSolutionDesignCount != null) {
                data.put("createdSolutionDesignCount",createdSolutionDesignCount);
            } else {
                data.put("createdSolutionDesignCount",null);
            }

            Long underDevelopmentSolutionCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("solutionDesignApc")
                    .where("status","Under-Development")
                    .queryCount();
            if (underDevelopmentPatternCount != null) {
                data.put("underDevelopmentSolutionCount",underDevelopmentSolutionCount);
            } else {
                data.put("underDevelopmentSolutionCount",null);
            }

            Long approvedSolutionDesignCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("solutionDesignApc")
                    .where("status","approved")
                    .queryCount();
            if (approvedSolutionDesignCount != null) {
                data.put("approvedSolutionDesignCount",approvedSolutionDesignCount);
            } else {
                data.put("approvedSolutionDesignCount",null);
            }

            request.setAttribute("data", data);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

    public static String getChartData(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String status =  request.getParameter("status");
        String type =  request.getParameter("type");
        Map<String,List<GenericValue>> data = UtilMisc.toMap();
        try {

            if(status.equals("Available-Patterns") || status.equals("Available-Solution")){
                status = "approved";
            } else if (status.equals("Created-Solution") || status.equals("Created-Patterns") ){
                status = "created";
            }

            if(type.equals("pattern")) {
                List<GenericValue> patternList = EntityQuery.use(delegator)
                        .select("id","psid", "baseName")
                        .from("basePatternApc")
                        .where("status", status)
                        .queryList();
                if (patternList != null) {
                    data.put("basePatterns",patternList);
                } else {
                    data.put("basePatterns",null);
                }
            } else if(type.equals("solution")) {
                List<GenericValue> solutionList = EntityQuery.use(delegator)
                        .select("id","psid","bpid","solutionDesignName")
                        .from("solutionDesignApc")
                        .where("status", status)
                        .queryList();
                if (solutionList != null) {
                    data.put("solutionDesigns",solutionList);
                } else {
                    data.put("solutionDesigns",null);
                }
            } else{
                request.setAttribute("message", "wrong input");
                return ERROR;
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("data", data);
        return SUCCESS;
    }
}
