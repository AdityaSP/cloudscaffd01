package com.autopatt.admin.utils;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import java.util.UUID;

public class NewTenantTransactionLogUtils {

    public static final String module = NewTenantTransactionLogUtils.class.getName();


    /** Start a new transaction for new tenant creation, and return new transaction Id */
    public static String startNewTenantTransaction(Delegator delegator, String tenantId) {
        String transactionId = UUID.randomUUID().toString();

        GenericValue newTenantTransactionGv = delegator.makeValue("NewTenantTransaction",
                UtilMisc.toMap("tenantId", tenantId,
                        "transactionId", transactionId,
                        "status", "INITIATED"));
        try {
            newTenantTransactionGv.create();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
        return transactionId;
    }

    public static void logTransactionStep (Delegator delegator, String transactionId, String status, String stepName, String details) {
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
        }
    }
}
