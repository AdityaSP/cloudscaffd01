package com.autopatt.admin.events;
import org.apache.ofbiz.base.conversion.NumberConverters;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.GenericValue;
import com.autopatt.portal.events.UserMgmtEvents;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;
import org.apache.ofbiz.base.util.*;
import java.util.ArrayList;
import com.autopatt.admin.utils.CommonUtils;

public class PlanMgmtEvents {

    public final static String module = UserMgmtEvents.class.getName();
    public static String SUCCESS = "success";
    public static String ERROR = "error";

    public static String updatePlan(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<String> errorList = new ArrayList<>();
      /*  String productName = request.getParameter("productName");
        String productId = request.getParameter("planId");
        String priceStr = request.getParameter("price");
        String attrName=request.getParameter("maxAdmin");
        String attrValue=request.getParameter("maxUserLogins");*/
        String productName = UtilCodec.checkStringForHtmlStrictNone("Product Name",request.getParameter("productName"),errorList);
        String productId = UtilCodec.checkStringForHtmlStrictNone("Plan Id",request.getParameter("planId"),errorList);
        String priceStr = UtilCodec.checkStringForHtmlStrictNone("Price",request.getParameter("price"),errorList);
        String attrName = UtilCodec.checkStringForHtmlStrictNone("Max Admin",request.getParameter("maxAdmin"),errorList);
        String attrValue = UtilCodec.checkStringForHtmlStrictNone("Max User Logins",request.getParameter("maxUserLogins"),errorList);

        if(!errorList.isEmpty()){
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
            CommonUtils.getResponse(request, response, errorList.get(0), ERROR);
            return ERROR;
        }
        BigDecimal price = null;
        try{
            price = new BigDecimal(priceStr);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Invalid Price entered");
            return ERROR;
        }
        Map<String, Object> inputs = UtilMisc.toMap("productId", productId);
        try {
            GenericValue product = delegator.findOne("Product", inputs , false);
            product.set("productName",productName);
            delegator.store(product);

            List<GenericValue> productPrices = product.getRelated("ProductPrice", null,null,false);
            if(UtilValidate.isNotEmpty(productPrices)) {
                GenericValue priceGv = productPrices.get(0);
                priceGv.set("price", price);
                delegator.store(priceGv);
            }

            List<GenericValue> productAttributes= product.getRelated("ProductAttribute",null,null,false);
            if(UtilValidate.isNotEmpty(productAttributes)){
                for(GenericValue productAttr : productAttributes){
                    productAttr.set("attrValue",request.getParameter(productAttr.getString("attrName")));
                    delegator.store(productAttr);
                }
            }

        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error trying to update the Plan details.");
            return ERROR;
        }
        request.setAttribute("updateSuccess","Y");
        request.setAttribute("_EVENT_MESSAGE_", "Plan details updated successfully.");
        return SUCCESS;
    }

}
