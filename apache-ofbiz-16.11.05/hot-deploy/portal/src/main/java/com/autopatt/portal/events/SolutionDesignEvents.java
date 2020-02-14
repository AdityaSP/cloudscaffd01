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

public class SolutionDesignEvents{

    public final static String module = SolutionDesignEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String addSolutionDesign(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String psid = request.getParameter("psid");
        String bpid = request.getParameter("bpid");

        String solutionDesignName = request.getParameter("solutionDesignName");
        String solutionDesignDesc = request.getParameter("solutionDesignDesc");
        String solutionForces = request.getParameter("solutionForces");
        String solutionBeneficiary = request.getParameter("solutionBeneficiary");

        request.setAttribute("psid", psid);

        try {
            Map<String, Object> addSolutionDesignResp = dispatcher.runSync("createSolutionDesign",
                    UtilMisc.<String, Object>toMap("psid", psid,"bpid", bpid, "solutionDesignName", solutionDesignName,
                            "solutionDesignDesc",solutionDesignDesc, "solutionForces",solutionForces,"solutionBeneficiary",
                            solutionBeneficiary,"userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addSolutionDesignResp)) {
                request.setAttribute("info", "SolutionDesign creation failed!");
                request.setAttribute("message", ERROR);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            request.setAttribute("info", "SolutionDesign creation failed!");
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("info", "SolutionDesign creation Successfull");
        request.setAttribute("message", SUCCESS);
        return SUCCESS;

    }


    public static String updateSolutionDesign(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");

        String id = request.getParameter("id");
        Object png = request.getParameter("png");
        Object xml = request.getParameter("xml");
        Object svg = request.getParameter("svg");
        Object json = request.getParameter("json");
        String updatedBy = userLoginData.getString("userLoginId");

        Map<String, Object> inputs = UtilMisc.toMap("id", id);
        try {
            GenericValue myBasePattern = delegator.findOne("solutionDesignApc", inputs, false);
            myBasePattern.setString("updatedBy", updatedBy);
            myBasePattern.set("png", png);
            myBasePattern.set("svg", svg);
            myBasePattern.set("xml", xml);
            myBasePattern.set("json", json);
            delegator.store(myBasePattern);

        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("info", "SolutionDesign update failed!");
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("info", "SolutionDesign update successfull");
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

    public static String getSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        try {
            List<GenericValue> SolutionDesignList = EntityQuery.use(delegator)
                    .select("id","psid","bpid","solutionDesignName","solutionDesignDesc","png","svg","xml","status").from("solutionDesignApc")
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
            request.setAttribute("info", "SolutionDesign approval failed!");
            request.setAttribute("message", ERROR);
            return ERROR;
        }
        request.setAttribute("info", "SolutionDesign approval successfull");
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

    public static String deleteSolutionDesign(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        String sdid = request.getParameter("sdid");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        try {
            GenericValue deleteSolutionDesign = delegator.findOne("solutionDesignApc", UtilMisc.toMap("id", sdid),false);
            if (!UtilValidate.isEmpty(deleteSolutionDesign)) {
                deleteSolutionDesign.remove();
            }
        } catch (GenericEntityException ex) {
            ex.printStackTrace();
            request.setAttribute("info", "SolutionDesign deletion failed!");
            request.setAttribute("message", ERROR);
        }
        request.setAttribute("info", "SolutionDesign deletion ");
        request.setAttribute("message", SUCCESS);
        return SUCCESS;
    }

}
