package com.autopatt.portal.events;

import com.autopatt.admin.utils.TenantCommonUtils;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.sql.Timestamp;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.security.Security;

public class SolutionDesignEvents{

    public final static String module = SolutionDesignEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String addSolutionDesign(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_CREATE_APC",userLogin)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }
        String psid = request.getParameter("psid");
        String bpid = request.getParameter("bpid");
        String solutionDesignName = request.getParameter("solutionDesignName");
        String solutionDesignDesc = request.getParameter("solutionDesignDesc");
        String solutionForces = request.getParameter("solutionForces");
        String solutionConsequences = request.getParameter("solutionConsequences");
        request.setAttribute("psid", psid);

        try {
            Map<String, Object> addSolutionDesignResp = dispatcher.runSync("createSolutionDesign",
                    UtilMisc.<String, Object>toMap("psid", psid,"bpid", bpid, "solutionDesignName", solutionDesignName,
                            "solutionDesignDesc",solutionDesignDesc, "solutionForces",solutionForces,"solutionConsequences",
                            solutionConsequences,"userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addSolutionDesignResp)) {
                getResponse(request, response, "SolutionDesign creation failed!", ERROR);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            getResponse(request, response, "Solution Design creation failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Solution Design created Successfully", SUCCESS);
        return SUCCESS;

    }


    public static String updateSolutionDesign(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Map<String,Object> data = UtilMisc.toMap();
        // Check Permission
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
            GenericValue myBasePattern = delegator.findOne("solutionDesignApc", inputs, false);
            myBasePattern.setString("updatedBy", updatedBy);
            myBasePattern.set("png", png);
            myBasePattern.set("svg", svg);
            myBasePattern.set("xml", xml);
            myBasePattern.set("json", json);
            myBasePattern.set("status", status);
            delegator.store(myBasePattern);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            getResponse(request, response, "Solution Design update failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Solution Design updated successfully", SUCCESS);
        return SUCCESS;
    }

    public static String getSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        try {
            List<GenericValue> SolutionDesignList = EntityQuery.use(delegator)
                    .select("id","psid","bpid","solutionDesignName","solutionDesignDesc","png","svg","xml","status","solutionForces","solutionConsequences","type").from("solutionDesignApc")
                    .where("id", sdid)
                    .queryList();

            if (SolutionDesignList != null) {
                request.setAttribute("data", SolutionDesignList);
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

    public static String approveSolutionDesign(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_APPROVE_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }
        String sdid = request.getParameter("sdid");
        String psid = request.getParameter("psid");
        String bpid = null;
        if (UtilValidate.isNotEmpty(request.getParameter("bpid"))){
            bpid = request.getParameter("bpid");
        }

        request.setAttribute("sdid", sdid);
        request.setAttribute("psid", psid);
        request.setAttribute("bpid", bpid);
        String status = "approved";
        Map<String, Object> inputs = UtilMisc.toMap("id", sdid);

        try {
            GenericValue solutionDesign = delegator.findOne("solutionDesignApc", inputs, false);
            solutionDesign.setString("status", status);
            delegator.store(solutionDesign);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            getResponse(request, response, "Solution Design approval failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Solution Design approved successfully", SUCCESS);
        return SUCCESS;
    }

    public static String deleteSolutionDesign(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        String sdid = request.getParameter("sdid");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_DELETE_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }

        try {
            String type = "pre-defined";
            String solDesignType = getSolutionDesignType(request,response,sdid);

            if(!solDesignType.equals(type)) {
                GenericValue deleteSolutionDesign = delegator.findOne("solutionDesignApc", UtilMisc.toMap("id", sdid), false);
                if (!UtilValidate.isEmpty(deleteSolutionDesign)) {
                    deleteSolutionDesign.remove();
                }
            }else{
                getResponse(request, response, "SolutionDesign deletion failed - user defined!", ERROR);
                return ERROR;
            }
        } catch (GenericEntityException ex) {
            ex.printStackTrace();
            getResponse(request, response, "Solution Design deletion failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Solution Design deleted successfully", SUCCESS);
        return SUCCESS;
    }

    public static String editSolutionDesign(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String,Object> data = UtilMisc.toMap();

        // Check permission
        if(!getSecurityPermission(request, response, "PORTAL_EDIT_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }

        String sdid = request.getParameter("sdid");
        String solutionDesignName = request.getParameter("solutionDesignName");
        String solutionDesignDesc = request.getParameter("solutionDesignDesc");
        String solutionForces = request.getParameter("solutionForces");
        String solutionConsequences = request.getParameter("solutionConsequences");
        String updatedBy = userLoginData.getString("userLoginId");
        Map<String, Object> inputs = UtilMisc.toMap("id", sdid);
System.out.println("updatedBy ="+updatedBy);
        String type = "pre-defined";
        String solutionDesignType = getSolutionDesignType(request,response,sdid);

        if(!solutionDesignType.equals(type)) {
            try {
                GenericValue mySolutionDesign= delegator.findOne("solutionDesignApc", inputs, false);
                mySolutionDesign.setString("updatedBy", updatedBy);
                mySolutionDesign.set("solutionDesignName", solutionDesignName);
                mySolutionDesign.set("solutionDesignDesc", solutionDesignDesc);
                mySolutionDesign.set("solutionForces", solutionForces);
                mySolutionDesign.set("solutionConsequences", solutionConsequences);
                delegator.store(mySolutionDesign);
            } catch (GenericEntityException ex) {
                getResponse(request, response, "SolutionDesign edit failed - !", ERROR);
                ex.printStackTrace();
                return ERROR;
            }
        }else{
            getResponse(request, response, "Solution Design edit failed - pre-defined!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Solution Design edited successfully ", SUCCESS);
        return SUCCESS;
    }


    private static String getSolutionDesignType(HttpServletRequest request, HttpServletResponse response,String id){
        HttpSession session = request.getSession();
        Map<String,Object> data = UtilMisc.toMap();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String solutionDesignApcType = null;
        try {
            GenericValue solutionDesignApc = EntityQuery.use(delegator)
                    .select("type").from("solutionDesignApc")
                    .where("id", id)
                    .queryOne();
            solutionDesignApcType = solutionDesignApc.getString("type");
        }catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("info", "Cannot retrieve type from solution design");
            data.put("message", ERROR);
            request.setAttribute("data", data);
            return ERROR;
        }
        return solutionDesignApcType;
    }


    public static String updateStatusToApproveRequested(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Map<String,Object> data = UtilMisc.toMap();
        // Check Permission
        if(!getSecurityPermission(request, response, "PORTAL_EDIT_APC",userLoginData)){
            getResponse(request, response, "You do not have permission.", ERROR);
            return ERROR;
        }

        String sdid = request.getParameter("sdid");
        String updatedBy = userLoginData.getString("userLoginId");
        String status = "approve-requested";
        Map<String, Object> inputs = UtilMisc.toMap("id", sdid);
        try {
            GenericValue approveRequestedData = delegator.findOne("solutionDesignApc", inputs, false);
            approveRequestedData.setString("updatedBy", updatedBy);
            approveRequestedData.set("status", status);
            delegator.store(approveRequestedData);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            getResponse(request, response, "Status change failed!", ERROR);
            return ERROR;
        }
        getResponse(request, response, "Status changed successfully", SUCCESS);
        return SUCCESS;
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
