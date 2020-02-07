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

public class BasePatternEvents{

    public final static String module = BasePatternEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String addBasePattern(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String psid = request.getParameter("psid");
        String baseName = request.getParameter("baseName");
        String baseDescription = request.getParameter("baseDescription");

        request.setAttribute("psid", psid);

        try {
            Map<String, Object> addBasePatternResp = dispatcher.runSync("createBasePattern",
                    UtilMisc.<String, Object>toMap("psid", psid, "baseName", baseName,
                            "baseDescription",baseDescription, "baseDescription",baseDescription,"userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addBasePatternResp)) {
                Debug.logError("Error creating addBasePatternResp for " + addBasePatternResp, module);
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


    public static String updateBasePattern(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String id = request.getParameter("id");
        Object png = request.getParameter("png");
        Object xml = request.getParameter("xml");
        Object svg = request.getParameter("svg");
        Object json = request.getParameter("json");
        String updatedBy = userLoginData.getString("userLoginId");

        Map<String, Object> inputs = UtilMisc.toMap("id", id);
        try {
            GenericValue myBasePattern = delegator.findOne("basePatternApc", inputs, false);
            myBasePattern.setString("updatedBy", updatedBy);
            myBasePattern.set("png", png);
            myBasePattern.set("svg", svg);
            myBasePattern.set("xml", xml);
            myBasePattern.set("json", json);
            delegator.store(myBasePattern);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }


    public static String getBasePattern(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String bpid = request.getParameter("bpid");
        try {
            List<GenericValue> BasePatternList = EntityQuery.use(delegator)
                    .select("id","psid","baseName","baseDescription","png","svg","xml","status").from("basePatternApc")
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


    public static String getBasePatternByBpid(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String bpid = request.getParameter("bpid");
        try {
            List<GenericValue> BasePatternList = EntityQuery.use(delegator)
                    .select("id","psid","bpid","solutionDesignName","solutionDesignDesc")
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
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

}
