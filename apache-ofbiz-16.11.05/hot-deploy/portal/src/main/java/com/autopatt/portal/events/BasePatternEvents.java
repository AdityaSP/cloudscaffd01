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
import org.apache.ofbiz.base.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
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
        Map<String,Object> data = UtilMisc.toMap();
        List<String> errorList = new ArrayList<>();
        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_CREATE_APC",userLogin)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }

        String psid = request.getParameter("psid");
        String baseName = UtilCodec.checkStringForHtmlStrictNone("Pattern Name",request.getParameter("baseName"),errorList);
        String baseDescription = UtilCodec.checkStringForHtmlStrictNone("Pattern Description",request.getParameter("baseDescription"),errorList);
        String baseForces = UtilCodec.checkStringForHtmlStrictNone("Forces",request.getParameter("baseForces"),errorList);
        String baseConsequences = UtilCodec.checkStringForHtmlStrictNone("Consequences",request.getParameter("baseConsequences"),errorList);
        request.setAttribute("psid", psid);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }

        try {
            Map<String, Object> addBasePatternResp = dispatcher.runSync("createBasePattern",
                    UtilMisc.<String, Object>toMap("psid", psid, "baseName", baseName,"baseDescription",baseDescription,
                            "baseDescription",baseDescription, "baseForces",baseForces,"baseConsequences",baseConsequences,"userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addBasePatternResp)) {
                getResponse(request, response, "Pattern creation failed!", ERROR);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            getResponse(request, response, "Pattern creation failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Pattern created Successfully", SUCCESS);
        return SUCCESS;

    }


    public static String updateBasePattern(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_CREATE_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }
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
            getResponse(request, response, "Pattern update failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Pattern updated Successfully", SUCCESS);
        return SUCCESS;
    }


    public static String getBasePattern(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String bpid = request.getParameter("bpid");
        try {
            List<GenericValue> BasePatternList = EntityQuery.use(delegator)
                    .select("id","psid","baseName","baseDescription","png","svg","xml","status","baseForces","baseConsequences","type").from("basePatternApc")
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
                    .select("id","psid","bpid","solutionDesignName","solutionDesignDesc","solutionForces","solutionConsequences","type")
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
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_APPROVE_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }
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
            getResponse(request, response, "Pattern approval failed", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Pattern approved successfully", SUCCESS);
        return SUCCESS;
    }

    public static String deleteBasePattern(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        String bpid = request.getParameter("bpid");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_DELETE_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }

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
                getResponse(request, response, "Pattern delete failed - user defined!", ERROR);
                return ERROR;
            }
        } catch (GenericEntityException ex) {
            ex.printStackTrace();
            getResponse(request, response, "Pattern delete failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Pattern deleted successfully ", SUCCESS);
        return SUCCESS;
    }


    public static String editBasePattern(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String,Object> data = UtilMisc.toMap();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        List<String> errorList = new ArrayList<>();
        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_EDIT_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }

        String bpid = request.getParameter("bpid");

        String baseName = UtilCodec.checkStringForHtmlStrictNone("Pattern Name",request.getParameter("baseName"),errorList);
        String baseDescription = UtilCodec.checkStringForHtmlStrictNone("Pattern Description",request.getParameter("baseDescription"),errorList);
        String baseForces = UtilCodec.checkStringForHtmlStrictNone("Forces",request.getParameter("baseForces"),errorList);
        String baseConsequences = UtilCodec.checkStringForHtmlStrictNone("Consequences",request.getParameter("baseConsequences"),errorList);
        String updatedBy = userLoginData.getString("userLoginId");
        Map<String, Object> inputs = UtilMisc.toMap("id", bpid);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }

        String type = "pre-defined";
        String basePatternType = getBasePatternType(request,response,bpid);

        if(!basePatternType.equals(type)) {
            try {
                GenericValue myBasePattern = delegator.findOne("basePatternApc", inputs, false);
                myBasePattern.setString("updatedBy", updatedBy);
                myBasePattern.set("baseName", baseName);
                myBasePattern.set("baseDescription", baseDescription);
                myBasePattern.set("baseForces", baseForces);
                myBasePattern.set("baseConsequences", baseConsequences);
                delegator.store(myBasePattern);
                } catch (GenericEntityException ex) {
                ex.printStackTrace();
                getResponse(request, response, "Pattern edit failed - !", ERROR);
                return ERROR;
            }
        }else{
            getResponse(request, response, "Pattern edit failed - pre-defined!", ERROR);
            return ERROR;
            }
        getResponse(request, response, "Pattern edited successfully ", SUCCESS);
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
            getResponse(request, response, "Cannot retrieve type from base pattern", ERROR);
            return ERROR;
        }
        return basePatternTypeData;
    }

    private static boolean getSecurityPermission(HttpServletRequest request, HttpServletResponse response,
                                                 String permissionName, GenericValue userLogin){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = dispatcher.getSecurity();
        if (security.hasPermission(permissionName, userLogin)) {
            return true;
        }
        return false;
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
