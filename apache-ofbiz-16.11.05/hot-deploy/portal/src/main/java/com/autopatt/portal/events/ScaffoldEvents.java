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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.sql.Timestamp;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.security.Security;

public class ScaffoldEvents {

    public final static String module = ScaffoldEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String compileScaffoldSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = delegator.getDelegatorTenantId();
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        final String targetURL = "https://postb.in/1583992299271-7542994602117";
        final PostMethod post = new PostMethod(targetURL);
        post.addParameter("tenantId", tenantId);
        post.addParameter("sdid", sdid);
        final HttpClient httpclient = new HttpClient();
        try {
            final int result = httpclient.executeMethod((HttpMethod) post);
            request.setAttribute("compileScaffoldSolutionDesignStatusCode", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        } finally {
            post.releaseConnection();
        }
        return SUCCESS;
    }

    public static String deployScaffoldSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = delegator.getDelegatorTenantId();
        final String targetURL = "https://postb.in/1583992299271-7542994602117";
        final PostMethod post = new PostMethod(targetURL);
        post.addParameter("tenantId", tenantId);
        post.addParameter("sdid", sdid);
        final HttpClient httpclient = new HttpClient();
        try {
            final int result = httpclient.executeMethod((HttpMethod) post);
            request.setAttribute("APIResult", result);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        } finally {
            post.releaseConnection();
        }

        return SUCCESS;
    }

    public static String getScaffoldSolutionDesignlogs(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        Map<String, Object> data = UtilMisc.toMap();
        try {
            List<GenericValue> scaffoldLogList = EntityQuery.use(delegator)
                    .select("id", "sdId", "xml", "csStatus", "compileLogs", "runtimeLogs", "createdBy")
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
