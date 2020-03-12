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

    public static String getScaffoldBySdid(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        try {
            List<GenericValue> scaffoldList = EntityQuery.use(delegator)
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
            getResponse(request,response,"Data retrival failed!", ERROR);
            return ERROR;
        }
        getResponse(request,response,"Data retrieval successfull", SUCCESS);
        return SUCCESS;
    }

    public static String scaffoldAPI(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLoginData = (GenericValue) session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String sdid = request.getParameter("sdid");
        String tenantId = (String) request.getAttribute("partyId"); // or its available in delagator.

        final String targetURL = "https://postb.in/1583992299271-7542994602117";
        final PostMethod post = new PostMethod(targetURL);
        post.addParameter("param1", "paramValue1");
        post.setParameter("param2", "paramValue2");
        post.setParameter("param2", "paramValue2"); // list of parameter value to be set
        final HttpClient httpclient = new HttpClient();
        try {
            final int result = httpclient.executeMethod((HttpMethod)post);
            request.setAttribute("APIResult", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            post.releaseConnection();
        }
        return " ";
    }

    private static HttpServletRequest getResponse(HttpServletRequest request, HttpServletResponse response,
                                                  String info, String message){
        Map<String,Object> data = UtilMisc.toMap();
        data.put("info", info);
        data.put("message", message);
        data.put("data", data);
        System.out.println("message =" +message);
        request.setAttribute("data", data);
        return request;
    }

}
