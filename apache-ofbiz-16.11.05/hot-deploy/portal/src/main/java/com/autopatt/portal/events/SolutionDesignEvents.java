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

        request.setAttribute("psid", psid);

        try {
            Map<String, Object> addSolutionDesignResp = dispatcher.runSync("createSolutionDesign",
                    UtilMisc.<String, Object>toMap("psid", psid,"bpid", bpid, "solutionDesignName", solutionDesignName,
                            "solutionDesignDesc",solutionDesignDesc, "userLogin",userLogin));
            if (!ServiceUtil.isSuccess(addSolutionDesignResp)) {
                Debug.logError("Error creating addBasePatternResp for " + addSolutionDesignResp, module);
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
            request.setAttribute("message", ERROR);
            return ERROR;
        }
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


}
