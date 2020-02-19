package com.autopatt.portal.events;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityFunction;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.sql.Timestamp;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.security.Security;

public class BasePatternEvents{

    public final static String module = BasePatternEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String addBasePattern(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        // Check permission
        Security security = dispatcher.getSecurity();
        if (!security.hasPermission("PORTAL_CREATE_APC", userLogin)) {
            data.put("info", "You do not have permission to create.");
            System.out.println("You do not have permission to create."  );
            data.put("message",ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        
        String psid = request.getParameter("psid");
        String baseName = request.getParameter("baseName");
        String baseDescription = request.getParameter("baseDescription");
        String baseForces = request.getParameter("baseForces");
        String baseBenefits = request.getParameter("baseBenefits");

        Map<String,Object> data = UtilMisc.toMap();
        request.setAttribute("psid", psid);

        try {
            Map<String, Object> addBasePatternResp = dispatcher.runSync("createBasePattern",
                    UtilMisc.<String, Object>toMap("psid", psid, "baseName", baseName,"baseDescription",baseDescription,
                            "baseDescription",baseDescription, "baseForces",baseForces,"baseBenefits",baseBenefits,"userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addBasePatternResp)) {
                Debug.logError("Error creating addBasePatternResp for " + addBasePatternResp, module);
                data.put("info", "BasePattern creation failed!");
                data.put("message",ERROR);
                request.setAttribute("data", data);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            data.put("info", "BasePattern creation failed!");
            data.put("message",ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        data.put("info", "BasePattern creation Successfull");
        data.put("message",SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;

    }


    public static String updateBasePattern(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();

        String id = request.getParameter("id");
        Object png = request.getParameter("png");
        Object xml = request.getParameter("xml");
        Object svg = request.getParameter("svg");
        Object json = request.getParameter("json");
        String updatedBy = userLoginData.getString("userLoginId");
        String status = "Under-Development";

        Map<String, Object> inputs = UtilMisc.toMap("id", id);
        try {
            GenericValue myBasePattern = delegator.findOne("basePatternApc", inputs, false);
            myBasePattern.setString("updatedBy", updatedBy);
            myBasePattern.set("png", png);
            myBasePattern.set("svg", svg);
            myBasePattern.set("xml", xml);
            myBasePattern.set("json", json);
            myBasePattern.set("status", status);
            delegator.store(myBasePattern);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("info", "BasePattern update failed!");
            data.put("message",ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        data.put("info", "BasePattern update Successfull");
        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }


    public static String getBasePattern(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String bpid = request.getParameter("bpid");
        try {
            List<GenericValue> BasePatternList = EntityQuery.use(delegator)
                    .select("id","psid","baseName","baseDescription","png","svg","xml","status","baseForces","baseBenefits","type").from("basePatternApc")
                    .where("id", bpid)
                    .queryList();

            if (BasePatternList != null) {
                request.setAttribute("data", BasePatternList);
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


    public static String getSolutionDesignByBpid(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String bpid = request.getParameter("bpid");
        String psId = request.getParameter("psid");

        try {
            List<EntityCondition> ConditionList = new LinkedList<EntityCondition>();
            ConditionList.add(  EntityCondition.makeCondition( EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("psid"), EntityOperator.EQUALS,psId),EntityOperator.AND,
                    EntityCondition.makeCondition( EntityFunction.UPPER_FIELD("bpid"), EntityOperator.EQUALS,bpid )));

            List<GenericValue> BasePatternList = EntityQuery.use(delegator)
                    .select("id","psid","bpid","solutionDesignName","solutionDesignDesc","solutionForces","solutionBenefits","type")
                    .from("solutionDesignApc")
                    .where("bpid", bpid)
                    .queryList();
            if (BasePatternList != null) {
                request.setAttribute("data", BasePatternList);
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

    public static String approveBasePattern(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Map<String,Object> data = UtilMisc.toMap();

        String psid = request.getParameter("psid");
        String bpid = request.getParameter("bpid");

        request.setAttribute("psid", psid);
        request.setAttribute("bpid", bpid);
        String status = "approved";

        Map<String, Object> inputs = UtilMisc.toMap("id", bpid);
        try {
            GenericValue solutionDesign = delegator.findOne("basePatternApc", inputs, false);
            solutionDesign.setString("status", status);
            delegator.store(solutionDesign);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("info", "BasePattern approval failed");
            data.put("message", ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        data.put("info", "BasePattern approval Successfull");
        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }

    public static String deleteBasePattern(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        String bpid = request.getParameter("bpid");
        Map<String,Object> data = UtilMisc.toMap();

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        try {
            String type = "pre-defined";
            String basePatternType = getBasePatternType(request,response,bpid);
            if(!basePatternType.equals(type)) {
                GenericValue deleteBasePattern = delegator.findOne("basePatternApc", UtilMisc.toMap("id", bpid), false);
                if (!UtilValidate.isEmpty(deleteBasePattern)) {
                    deleteBasePattern.remove();
                }
            }else{
                data.put("info", "BasePattern delete failed - user defined!");
                data.put("message", ERROR);
                request.setAttribute("data", data);
                return ERROR;
            }
        } catch (GenericEntityException ex) {
            ex.printStackTrace();
            data.put("info", "BasePattern delete failed!");
            data.put("message", ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        data.put("info", "BasePattern deleted successfully ");
        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }


    public static String editBasePattern(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        // Check permission
        Security security = dispatcher.getSecurity();
        if (!security.hasPermission("PORTAL_EDIT_APC", userLoginData)) {
            data.put("info", "You do not have permission to edit base pattern.");
            System.out.println("You do not have permission to edit base pattern."  );
            data.put("message",ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }

        String bpid = request.getParameter("bpid");
        String baseName = request.getParameter("baseName");
        String baseDescription = request.getParameter("baseDescription");
        String baseForces = request.getParameter("baseForces");
        String baseBenefits = request.getParameter("baseBenefits");
        String updatedBy = userLoginData.getString("userLoginId");
        Map<String, Object> inputs = UtilMisc.toMap("id", bpid);

        String type = "pre-defined";
        String basePatternType = getBasePatternType(request,response,bpid);

        if(!basePatternType.equals(type)) {
            try {
                GenericValue myBasePattern = delegator.findOne("basePatternApc", inputs, false);
                myBasePattern.setString("updatedBy", updatedBy);
                myBasePattern.set("baseName", baseName);
                myBasePattern.set("baseDescription", baseDescription);
                myBasePattern.set("baseForces", baseForces);
                myBasePattern.set("baseBenefits", baseBenefits);
                delegator.store(myBasePattern);
                } catch (GenericEntityException ex) {
                ex.printStackTrace();
                data.put("info", "BasePattern edit failed - !");
                data.put("message", ERROR);
                request.setAttribute("data", data);
                return ERROR;
            }
        }else{
            data.put("info", "BasePattern edit failed - pre-defined!");
            data.put("message", ERROR);
            request.setAttribute("data", data);
            return ERROR;
            }
        data.put("info", "BasePattern edited successfully ");
        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }


    private static String getBasePatternType(HttpServletRequest request, HttpServletResponse response,String id){
        HttpSession session = request.getSession();
        Map<String,Object> data = UtilMisc.toMap();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String basePatternTypeData = null;
        try {
            GenericValue basePattern = EntityQuery.use(delegator)
                    .select("type").from("basePatternApc")
                    .where("id", id)
                    .queryOne();
            basePatternTypeData = basePattern.getString("type");
        }catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("info", "Cannot retrieve type from base pattern");
            data.put("message", ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        return basePatternTypeData;
    }

}
