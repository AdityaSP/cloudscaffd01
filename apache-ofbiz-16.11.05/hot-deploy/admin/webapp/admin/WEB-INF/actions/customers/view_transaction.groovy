import org.apache.ofbiz.base.util.UtilMisc

def transactionId = parameters.transactionId

println "Fetching transaction details for " + transactionId
//TODO: Fetch transaction details and logs

def transaction = delegator.findOne("NewTenantTransaction", UtilMisc.toMap("transactionId", transactionId), false)
context.transaction = transaction

def transactionLogs = delegator.findByAnd("NewTenantTransactionLog", UtilMisc.toMap("transactionId", transactionId), UtilMisc.toList("createdStamp"), false)
context.transactionLogs = transactionLogs