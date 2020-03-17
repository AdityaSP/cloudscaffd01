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

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.base.util.*;
import java.util.*;
import java.sql.Timestamp;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.security.Security;

public class ScaffoldEvents {

    public final static String module = ScaffoldEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    private static Properties SCAFFOLD_URL_PROPERTIES = UtilProperties.getProperties("scaffoldURL.properties");

    public static String compileScaffoldSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = delegator.getDelegatorTenantId();
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String createdBy = userLogin.getString("userLoginId");
        String targetURL = SCAFFOLD_URL_PROPERTIES.getProperty("autopatt.APC.compileURL","false");
        final PostMethod post = new PostMethod(targetURL);
        Map<String, Object> data = UtilMisc.toMap();
       post.setParameter("tenant_name", "xyzcorp");// hardcoded value to work with dev environment
        // post.setParameter("tenant_name", tenantId);
        // post.setParameter("sd_id", sdid);
        post.setParameter("sd_id", "SD-10097");
        post.setParameter("user",createdBy);
        final HttpClient httpclient = new HttpClient();
        try {
            final int httpStatusCode = httpclient.executeMethod((HttpMethod) post);
            if(httpStatusCode == 200) {
                Map<String, Object> compileScaffoldSolutionDesignResponse = UtilMisc.toMap();
                compileScaffoldSolutionDesignResponse.put("compileLogs", post.getResponseBodyAsString());
                compileScaffoldSolutionDesignResponse.put("message", SUCCESS);
                data.put("compileScaffoldSolutionDesignResponse",compileScaffoldSolutionDesignResponse);
            } else {
                data.put("info", "Unable reach the server");
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.put("message", ERROR);
            return ERROR;
        } finally {
            post.releaseConnection();
        }

        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }

    public static String deployScaffoldSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = delegator.getDelegatorTenantId();
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String createdBy = userLogin.getString("userLoginId");
        Map<String, Object> data = UtilMisc.toMap();
        String targetURL = SCAFFOLD_URL_PROPERTIES.getProperty("autopatt.APC.deployURL","false");
        final PostMethod post = new PostMethod(targetURL);
        post.setParameter("tenant_name", tenantId);
        post.setParameter("sd_id", sdid);
        post.setParameter("user",createdBy);
        final HttpClient httpclient = new HttpClient();
        try {
            final int httpStatusCode = httpclient.executeMethod((HttpMethod) post);
            if(httpStatusCode == 200) {
                Map<String, Object> deployScaffoldSolutionDesignResponse = UtilMisc.toMap();
                deployScaffoldSolutionDesignResponse.put("runtimeLogs", post.getResponseBodyAsString());
                deployScaffoldSolutionDesignResponse.put("message", SUCCESS);
                data.put("deployScaffoldSolutionDesignResponse",deployScaffoldSolutionDesignResponse);
            } else {
                data.put("info", "Unable reach the server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.put("message", ERROR);
            return ERROR;
        } finally {
            post.releaseConnection();
        }

        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }

    public static String getScaffoldSolutionDesignlogs(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        Map<String, Object> data = UtilMisc.toMap();
        try {
            List<GenericValue> scaffoldLogList = EntityQuery.use(delegator)
                    .select("sdId", "xml", "csStatus", "compileLogs", "runtimeLogs", "createdBy")
                    .from("scaffold")
                    .where("sdId", sdid)
                    .queryList();

            if (scaffoldLogList != null) {
                data.put("scaffoldLogList",scaffoldLogList);
            } else {
                data.put("scaffoldLogList",null);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("message", ERROR);
            return ERROR;
        }

        data.put("message", SUCCESS);
        request.setAttribute("data", data);
        return SUCCESS;
    }
}