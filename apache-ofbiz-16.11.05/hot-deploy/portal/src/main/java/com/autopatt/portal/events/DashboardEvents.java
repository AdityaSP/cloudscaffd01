package com.autopatt.portal.events;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.util.EntityQuery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            Long ProblemStatementCount = EntityQuery.use(delegator)
                    .select("id")
                    .from("problemStatementApc")
                    .queryCount();
            if (ProblemStatementCount != null) {
                data.put("ProblemStatementCount",ProblemStatementCount);
            } else {
                data.put("ProblemStatementCount",null);
            }

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

            request.setAttribute("data", data);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }
}

