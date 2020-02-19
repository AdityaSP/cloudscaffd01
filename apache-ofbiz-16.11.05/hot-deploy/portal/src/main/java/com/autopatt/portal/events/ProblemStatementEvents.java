package com.autopatt.portal.events;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityFunction;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.datasource.GenericHelperInfo;
import org.apache.ofbiz.entity.jdbc.SQLProcessor;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.security.Security;

public class ProblemStatementEvents{

    public final static String module = ProblemStatementEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static GenericHelperInfo helperInfo ;

    public ProblemStatementEvents(GenericHelperInfo helperInfo) {
        this.helperInfo = helperInfo;
    }

    public static String addProblemStatement(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(getSecurityPermission(request, response, "PORTAL_CREATE_APC",userLogin)){
            getResponse(request, response, "You do not have permission to create.", ERROR);
            return ERROR;
        }

        String problemStatement = request.getParameter("problemStatement");
        String problemDescription = request.getParameter("problemDescription");
        String [] tag = request.getParameter("tag").split(",");
        String problemStatementId = null;
        String createdBy = userLogin.getString("userLoginId");
        String type = "user defined";
        String psid = null;
        try {
            GenericValue newProblemStatement = delegator.makeValue("problemStatementApc");
            problemStatementId = delegator.getNextSeqId("Quote");
            psid = "PS-"+problemStatementId;
            newProblemStatement.setString("id", psid);
            newProblemStatement.setString("problemStatement", problemStatement);
            newProblemStatement.setString("problemDescription", problemDescription);
            newProblemStatement.setString("createdBy", createdBy);
            newProblemStatement.setString("type", type);
            delegator.create(newProblemStatement);
            } catch (GenericEntityException ex) {
                Debug.logError(ex, module);
            getResponse(request, response, "Problem Statement creation failed!", ERROR);
            return ERROR;
        }

        String tagsId = null;
        int tagSize = tag.length;
        try{
            for (int TagNameCount=0; TagNameCount < tagSize; TagNameCount++) {
            List<GenericValue> TagList = EntityQuery.use(delegator)
                    .select("id","tagName")
                    .from("problemStatementTags").where("tagName",tag[TagNameCount])
                    .queryList();
                if (TagList.isEmpty()) {
                    GenericValue newproblemStatementTags = delegator.makeValue("problemStatementTags");
                    tagsId = delegator.getNextSeqId("Quote");
                    newproblemStatementTags.setString("id", tagsId);
                    newproblemStatementTags.setString("tagName", (String) tag[TagNameCount]);
                    delegator.create(newproblemStatementTags);

                    GenericValue newproblemStatementTagProblem = delegator.makeValue("problemStatementTagProblem");
                    String tagProblemId = delegator.getNextSeqId("Quote");
                    newproblemStatementTagProblem.setString("id", tagProblemId);
                    newproblemStatementTagProblem.setString("tagid", tagsId);
                    newproblemStatementTagProblem.setString("problemId", psid);
                    delegator.create(newproblemStatementTagProblem);

                } else {
                    for (int TagIdCount=0; TagIdCount < TagList.size(); TagIdCount++) {
                        
                        tagsId = TagList.get(TagIdCount).getString("id");
                        GenericValue newproblemStatementTagProblem = delegator.makeValue("problemStatementTagProblem");
                        String tagProblemId = delegator.getNextSeqId("Quote");
                        newproblemStatementTagProblem.setString("id", tagProblemId);
                        newproblemStatementTagProblem.setString("tagid", tagsId);
                        newproblemStatementTagProblem.setString("problemId", psid);
                        delegator.create(newproblemStatementTagProblem);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            getResponse(request, response, "Problem Statement creation failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Problem Statement creation successfull", SUCCESS);
        return SUCCESS;
    }

    public static String getPatternByPsId(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();
        String psId = request.getParameter("psid");
        GenericHelperInfo helperInfo = new GenericHelperInfo("org.ofbiz","");
        System.out.println(psId);
        try {
            List<GenericValue> ProblemStatementList = EntityQuery.use(delegator)
                    .select("id","problemStatement","problemDescription","type")
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

    public static String search(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String inputSearch = request.getParameter("inputSearch");
        String [] type= request.getParameter("type").split(",");
        Map<String,Object> data = UtilMisc.toMap();

        for (int typeCount = 0; typeCount < type.length; typeCount++) {
            if (type[typeCount].equals("typeProblemStatement") ) {
                data.put("ProblemStatements", getProblemStatement(inputSearch, delegator));
            } else if (type[typeCount].equals("typeBasePattern") ) {
                //request.setAttribute("data", getBasePattern(inputSearch, delegator));
                data.put("basePatterns", getBasePattern(inputSearch, delegator));
            } else if (type[typeCount].equals("typeSolutionDesign") ) {
               // request.setAttribute("data", getSolutionDesign(inputSearch, delegator));
                data.put("solutionDesigns", getSolutionDesign(inputSearch, delegator));
            }else if(type[typeCount].equals("typeSearchAll")){
                data.put("ProblemStatements", getProblemStatement(inputSearch, delegator));
                data.put("basePatterns", getBasePattern(inputSearch, delegator));
                data.put("solutionDesigns", getSolutionDesign(inputSearch, delegator));
            }
        }
        request.setAttribute("data", data);
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

    private static String getAll(HttpServletRequest request, HttpServletResponse response)  {
//
//        SQLProcessor sqlproc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
//        String qStr = "sql query";
//        sqlproc.prepareStatement(qStr);
//        ResultSet result = sqlproc.executeQuery();


        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String baseName = helperInfo.getHelperBaseName();
        String groupName = helperInfo.getEntityGroupName();
        GenericHelperInfo genericHelper = new GenericHelperInfo(groupName, baseName); ;
        SQLProcessor sqlProcessor = new SQLProcessor(delegator,genericHelper);
        try {
            sqlProcessor.prepareStatement("SELECT * FROM PARTY LMIT 0, 5");
            ResultSet rs1 = sqlProcessor.executeQuery();
            request.setAttribute("data", rs1);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    private static List<GenericValue> getSolutionDesign(String inputSearch, Delegator delegator) {
        List<EntityCondition> entityConditionList = new LinkedList<EntityCondition>();
        entityConditionList.add(
                EntityCondition.makeCondition(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("solutionDesignName"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() + '%'), EntityOperator.OR,
                        EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("solutionDesignDesc"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() + '%')));
        List<GenericValue> solutionDesignList =null;
        try {
            
            solutionDesignList = EntityQuery.use(delegator)
                    .select("solutionDesignName", "id","psid","bpid")
                    .from("solutionDesignApc")
                    .where(entityConditionList)
                    .queryList();
            return solutionDesignList;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return solutionDesignList;
    }

    private static List<GenericValue> getProblemStatement(String inputSearch, Delegator delegator) {
        List<EntityCondition> entityConditionList = new LinkedList<EntityCondition>();
        entityConditionList.add(
                EntityCondition.makeCondition(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("problemStatement"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() + '%'), EntityOperator.OR,
                        EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("problemDescription"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() + '%')));
        List<GenericValue> SearchProblemStatementList = null;
        try {

            SearchProblemStatementList = EntityQuery.use(delegator)
                    .select("problemStatement", "id")
                    .from("problemStatementApc")
                    .where(entityConditionList)
                    .queryList();
            return SearchProblemStatementList;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return SearchProblemStatementList;
    }

    private static List<GenericValue> getBasePattern(String inputSearch, Delegator delegator)  {
        List<EntityCondition> entityConditionList = new LinkedList<EntityCondition>();
        entityConditionList.add(
                EntityCondition.makeCondition(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("baseName"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() + '%'), EntityOperator.OR,
                        EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("baseDescription"), EntityOperator.LIKE, '%' + inputSearch.toUpperCase() + '%')));

        List<GenericValue> basePatterList = null;
        try {
            basePatterList = EntityQuery.use(delegator)
                    .select("baseName", "id","psid")
                    .from("basePatternApc")
                    .where(entityConditionList)
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return basePatterList;
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
                    .select("id","problemStatement","problemDescription","type")
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

    public static String editProblemStatement(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(getSecurityPermission(request, response, "PORTAL_EDIT_APC",userLoginData)){
            getResponse(request, response, "You do not have permission to edit.", ERROR);
            return ERROR;
        }

        String psid = request.getParameter("psid");
        String problemStatement = request.getParameter("problemStatement");
        String problemDescription = request.getParameter("problemDescription");
        String tagName = request.getParameter("tagName");
        String updatedBy = userLoginData.getString("userLoginId");
        Map<String, Object> inputs = UtilMisc.toMap("id", psid);

        String type = "pre-defined";
        String basePatternType = getProblemStatementType(request,response,psid);

        if(!basePatternType.equals(type)) {
            try {
                GenericValue myProblemStatement = delegator.findOne("problemStatementApc", inputs, false);
                myProblemStatement.setString("updatedBy", updatedBy);
                myProblemStatement.set("problemStatement", problemStatement);
                myProblemStatement.set("problemDescription", problemDescription);
                delegator.store(myProblemStatement);
            } catch (GenericEntityException ex) {
                ex.printStackTrace();
                getResponse(request, response, "SolutionDesign edit failed - !", ERROR);
                return ERROR;
            }
        }else{
            getResponse(request, response, "problem statement edit failed - pre-defined!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "problem statement edited successfully ", SUCCESS);
        return SUCCESS;
    }


    private static String getProblemStatementType(HttpServletRequest request, HttpServletResponse response,String id){
        HttpSession session = request.getSession();
        Map<String,Object> data = UtilMisc.toMap();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String problemStatementType = null;
        try {
            GenericValue problemStatementApc = EntityQuery.use(delegator)
                    .select("type").from("problemStatementApc")
                    .where("id", id)
                    .queryOne();
            problemStatementType = problemStatementApc.getString("type");
        }catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("info", "Cannot retrieve type from problem statement");
            data.put("message", ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        return problemStatementType;
    }

    private static boolean getSecurityPermission(HttpServletRequest request, HttpServletResponse response,
                                         String permissionName, GenericValue userLogin){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = dispatcher.getSecurity();
        if (!security.hasPermission(permissionName, userLogin)) {
            return false;
        }
        return true;
    }


    private static HttpServletRequest getResponse(HttpServletRequest request, HttpServletResponse response,
                                                  String info, String message){
        Map<String,Object> data = UtilMisc.toMap();
        data.put("info", info);
        data.put("message", message);
        System.out.println("message =" +message);
        request.setAttribute("data", data);
        return request;
    }

}
