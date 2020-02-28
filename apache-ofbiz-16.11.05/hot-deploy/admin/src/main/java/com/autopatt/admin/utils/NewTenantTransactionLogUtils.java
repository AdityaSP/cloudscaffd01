package com.autopatt.admin.utils;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewTenantTransactionLogUtils {
    public static final String module = NewTenantTransactionLogUtils.class.getName();


    /** Start a new transaction for new tenant creation, and return new transaction Id */
    public static String startNewTenantTransaction(LocalDispatcher dispatcher, String tenantId, String tenantOrgName) {
        String transactionId = null;

        Map<String,Object> createTenantTnxResp = null;
        try {
            Map<String,Object> inputCtx = UtilMisc.<String, Object> toMap("tenantId", tenantId,
                    "tenantId", tenantId,
                    "tenantOrgName", tenantOrgName,
                    "userLogin", UserLoginUtils.getSystemUserLogin(dispatcher.getDelegator()));

            createTenantTnxResp = dispatcher.runSync("createNewTenantTransactionService", inputCtx, 300, true);
            transactionId = (String) createTenantTnxResp.get("transactionId");
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return null;
        }
        if(UtilValidate.isEmpty(transactionId)) return null;

        logTransactionStep(dispatcher, transactionId, "SCHEDULED", "Scheduled", "Onboarding process scheduled for tenant: " + tenantId);
        transactionId = (String) createTenantTnxResp.get("transactionId");
        return transactionId;
    }

    public static void logTransactionStep (LocalDispatcher dispatcher, String transactionId, String status, String stepName, String details) {
        Map<String,Object> createTenantTnxLogResp = null;
        try {
            Map<String, Object>  inputCtx =  UtilMisc.<String, Object> toMap("transactionId", transactionId,
                    "status", status,
                    "stepName", stepName,
                    "details", details,
                    "userLogin", UserLoginUtils.getSystemUserLogin(dispatcher.getDelegator()));

            createTenantTnxLogResp = dispatcher.runSync("createNewTenantTransactionLogService", inputCtx, 300, true);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
    }


    /**
     * Service to create new tenant transaction
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> createNewTenantTransactionService(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<>();
        Delegator delegator = ctx.getDelegator();
        String transactionId = UUID.randomUUID().toString();

        String tenantId = (String) context.get("tenantId");
        String tenantOrgName = (String) context.get("tenantOrgName");

        GenericValue newTenantTransactionGv = delegator.makeValue("NewTenantTransaction",
                UtilMisc.toMap("tenantId", tenantId,
                        "tenantOrgName", tenantOrgName,
                        "transactionId", transactionId,
                        "status", "INITIATED"));
        try {
            newTenantTransactionGv.create();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure("Unable to create new tenant transaction");
        }
        result.put("transactionId", transactionId);
        return result;
    }

    /**
     * Service to log an step of tenantion create transaction
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> createNewTenantTransactionLogService(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<>();
        Delegator delegator = ctx.getDelegator();
        String transactionId = (String) context.get("transactionId");
        String status = (String) context.get("status");
        String stepName = (String) context.get("stepName");
        String details = (String) context.get("details");

        String transactionLogId = delegator.getNextSeqId("NewTenantTransactionLog");
        GenericValue newTenantTransactionGv = delegator.makeValue("NewTenantTransactionLog",
                UtilMisc.toMap("transactionLogId", transactionLogId,
                        "transactionId", transactionId,
                        "status", status,
                        "stepName", stepName,
                        "details", details));
        try {
            newTenantTransactionGv.create();
            GenericValue tenantTransaction = delegator.findOne("NewTenantTransaction", UtilMisc.toMap("transactionId", transactionId), false);
            if(UtilValidate.isNotEmpty(tenantTransaction)) {
                tenantTransaction.set("status", status);
                tenantTransaction.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure(e.getMessage());
        }

        result.put("transactionLogId", transactionLogId);
        return result;
    }

}
