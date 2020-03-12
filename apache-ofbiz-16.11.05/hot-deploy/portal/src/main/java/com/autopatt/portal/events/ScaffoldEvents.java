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

public class ScaffoldEvents{

    public final static String module = ScaffoldEvents.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String compileScaffoldSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = delegator.getDelegatorTenantId();
        final String targetURL = "https://postb.in/1583992299271-7542994602117";
        final PostMethod post = new PostMethod(targetURL);
        post.addParameter("tenantId", "tenantId");
        //post.setParameter("param2", "paramValue2");
        final HttpClient httpclient = new HttpClient();
        Map<String,Object> data = UtilMisc.toMap();
        List<GenericValue> scaffoldList = null;
        try {
            final int result = httpclient.executeMethod((HttpMethod) post);
            request.setAttribute("compileScaffoldSolutionDesignStatusCode", result);
        } catch (Exception e) {
          scaffoldList = EntityQuery.use(delegator)
                    .select("id","sdId","xml","csStatus","compileLogs","runtimeLogs","createdBy").from("scaffold")
                    .where("sdId", sdid)
                    .queryList();

            if (scaffoldList != null) {
                request.setAttribute("data", scaffoldList);
            } else {
                request.setAttribute("data", null);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            data.put("info", "Data retrival failed!");
            data.put("message", ERROR);
            data.put("data", scaffoldList);
            request.setAttribute("data", data);
            return ERROR;
        } finally {
            post.releaseConnection();
        }

        request.setAttribute("APIResult", result);
        data.put("info", "Data retrieval successfull");
        data.put("message", SUCCESS);
        data.put("data", scaffoldList);
        request.setAttribute("data", data);
        return SUCCESS;
    }

    public static String deployScaffoldSolutionDesign(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = delegator.getDelegatorTenantId();
        final String targetURL = "https://postb.in/1583992299271-7542994602117";
        final PostMethod post = new PostMethod(targetURL);
        post.addParameter("param1", "paramValue1");
        post.setParameter("param2", "paramValue2");
        post.setParameter("param2", "paramValue2"); // list of parameter value to be set
        final HttpClient httpclient = new HttpClient();
        try {
            final int result = httpclient.executeMethod((HttpMethod)post);
            request.setAttribute("APIResult", result);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", ERROR);
            return ERROR;
        } finally {
            post.releaseConnection();
        }

        request.setAttribute("APIResult", result);
        return SUCCESS;
    }

    public static String getScaffoldSolutionDesignlogs(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        try {
                List<GenericValue> scaffoldList = EntityQuery.use(delegator)
                .select("id", "sdId", "xml", "csStatus", "compileLogs", "runtimeLogs", "createdBy")
                .from("scaffold")
                .where("sdId", sdid)
                .queryList();

            if (scaffoldList != null) {
                request.setAttribute("data", scaffoldList);
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
