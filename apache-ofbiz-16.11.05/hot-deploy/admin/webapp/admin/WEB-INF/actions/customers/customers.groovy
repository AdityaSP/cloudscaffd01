import com.autopatt.admin.utils.TenantCommonUtils
import com.autopatt.admin.utils.UserLoginUtils
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil

tenantOrgParties = delegator.findByAnd("TenantOrgParty", null, UtilMisc.toList("createdStamp") ,false);

def customers = new ArrayList();
if(UtilValidate.isNotEmpty(tenantOrgParties)) {
    for(GenericValue tenantOrg: tenantOrgParties) {
        Map<String,Object> customer = UtilMisc.toMap()

        customer.put("tenantId", tenantOrg.tenantId)
        customer.put("orgPartyId", tenantOrg.orgPartyId)
        customer.put("createdStamp", tenantOrg.createdStamp)

        def hasValidSubCheckResp = dispatcher.runSync("checkForValidSubscription",
                UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(delegator), "tenantId", tenantOrg.tenantId))
        customer.put("hasActiveSubscription", hasValidSubCheckResp.get("hasValidSubscription"))

        def getSubscriptionsResp = dispatcher.runSync("getSubscriptions",
                UtilMisc.toMap("orgPartyId", tenantOrg.orgPartyId,
                        "status", "ACTIVE",
                        "userLogin", userLogin))

        Set subscribedProductIds = []
        if(ServiceUtil.isSuccess(getSubscriptionsResp)) {
            def activeSubscriptions = getSubscriptionsResp.get("subscriptions")
            for(def sub: activeSubscriptions) {
                if(sub.status == "ACTIVE")  subscribedProductIds.add(sub.productId)
            }
        }
        customer.put("subscribedProductIds", subscribedProductIds)
        customers.add(customer)
    }
}

context.customers = customers;
