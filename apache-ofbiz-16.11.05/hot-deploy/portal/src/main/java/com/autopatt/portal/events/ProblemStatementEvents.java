package com.autopatt.portal.events;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.condition.EntityFunction;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProblemStatementEvents{

    public final static String module = ProblemStatementEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String addProblemStatement(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String problemStatement = request.getParameter("problemStatement");
        String problemDescription = request.getParameter("problemDescription");
        String tag = request.getParameter("tag");

        try {
            Map<String, Object> addproductAPCResp = dispatcher.runSync("createProblemStatement",
                    UtilMisc.<String, Object>toMap("problemStatement", problemStatement, "problemDescription", problemDescription,
                            "tag",tag,"userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addproductAPCResp)) {
                Debug.logError("Error creating addproductAPC for " + addproductAPCResp, module);
                request.setAttribute("message", ERROR);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

    public static String getPatternByPsId(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();
        String psId = request.getParameter("psid");
        System.out.println(psId);
        try {
            List<GenericValue> ProblemStatementList = EntityQuery.use(delegator)
                    .select("id","problemStatement","problemDescription")
                    .from("problemStatementApc")
                    .where("id",psId)
                    .queryList();
            if (ProblemStatementList != null) {
                data.put("problemStatementList",ProblemStatementList);
                //request.setAttribute("data", SearchProblemStatementList);
            } else {
                data.put("problemStatementList",null);
            }

            List<GenericValue> basePatternList = EntityQuery.use(delegator)
                    .select("id","psid","baseDescription","baseName")
                    .from("basePatternApc")
                    .where("psid", psId)
                    .queryList();
            if (basePatternList != null) {
                data.put("basePatternList",basePatternList);
            } else {
                data.put("basePatternList",null);
            }
            List<EntityCondition> ConditionList = new LinkedList<EntityCondition>();
            ConditionList.add(  EntityCondition.makeCondition( EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("psid"), EntityOperator.EQUALS,psId),EntityOperator.AND,
                            EntityCondition.makeCondition( EntityFunction.UPPER_FIELD("bpid"), EntityOperator.EQUALS,"" )));

            List<GenericValue> solutionDesignList = EntityQuery.use(delegator)
                    .select("id","bpid","psid","solutionDesignName","solutionDesignDesc")
                    .from("solutionDesignApc")
                    .where(ConditionList)
                    .queryList();
            if (solutionDesignList != null) {
                data.put("solutionDesignList",solutionDesignList);
            } else {
                data.put("solutionDesignList",null);
            }

            List<GenericValue> tagsList = EntityQuery.use(delegator)
                    .select("tagid","problemId","tagName")
                    .from("problemStatementTagView")
                    .where("problemId", psId)
                    .queryList();
            if (tagsList != null) {
                data.put("tagsList",tagsList);
            } else {
                data.put("tagsList",null);
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

    public static String searchProblemStatements(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String inputSearch = request.getParameter("inputSearch");
        List<EntityCondition> entityConditionList = new LinkedList<EntityCondition>();
        try {
            entityConditionList.add(
                    EntityCondition.makeCondition( EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("problemStatement"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() +'%'),EntityOperator.OR,
                            EntityCondition.makeCondition( EntityFunction.UPPER_FIELD("problemDescription"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() +'%') )
            );
            List<GenericValue> SearchProblemStatementList = EntityQuery.use(delegator)
                    .select("problemStatement","id")
                    .from("problemStatementApc")
                    .where(entityConditionList)
                    .queryList();
            if (SearchProblemStatementList != null) {
                request.setAttribute("data", SearchProblemStatementList);
            } else {
                request.setAttribute("data", null);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }


    public static String getTags(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
       /* String inputSearch = request.getParameter("inputSearch");*/
        try {
            List<GenericValue> TagList = EntityQuery.use(delegator)
                    .select("id","tagName")
                    .from("problemStatementTags")
                    .queryList();
            if (TagList != null) {
                request.setAttribute("data", TagList);
            } else {
                request.setAttribute("data", false);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }


    public static String getProblemStatements(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String psid = request.getParameter("psid");

        try {
            List<GenericValue> ProblemStatementsList = EntityQuery.use(delegator)
                    .select("id","problemStatement","problemDescription")
                    .from("problemStatementApc")
                    .where("id",psid)
                    .queryList();
            if (ProblemStatementsList != null) {
                request.setAttribute("data", ProblemStatementsList);
            } else {
                request.setAttribute("message", ERROR);
                request.setAttribute("data", null);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

    public static String getProblemStatementsByTagId(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String tagId = request.getParameter("tagId");
        try {
            List<GenericValue> ProblemStatementList = EntityQuery.use(delegator)
                    .select("id","problemStatement")
                    .from("problemStatementTag")
                    .where("tagid", tagId)
                    .queryList();
            if (ProblemStatementList != null) {
                request.setAttribute("data", ProblemStatementList);
            } else {
                request.setAttribute("data", false);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

}
