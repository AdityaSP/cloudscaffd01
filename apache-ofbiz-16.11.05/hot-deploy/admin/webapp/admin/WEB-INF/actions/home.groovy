import com.autopatt.admin.constants.UserStatusConstants
import com.autopatt.admin.utils.UserLoginUtils
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.party.party.PartyHelper
import org.ocpsoft.prettytime.PrettyTime;
import org.apache.ofbiz.service.ServiceUtil

import java.sql.Timestamp

PrettyTime prettyTime = new PrettyTime();

adminDetails = delegator.findByAnd("PartyRoleAndPartyDetail", UtilMisc.toMap("roleTypeId", "AUTOPATT_ADMIN"), UtilMisc.toList("firstName"), false);
String productId = parameters.productId

List<Map> adminList = new ArrayList()
def maxUsersToShow = 5
for(GenericValue apAdmin : adminDetails) {

    Map entry = UtilMisc.toMap("firstName", apAdmin.firstName)
    entry.put("lastName", apAdmin.lastName)

    String fullName = PartyHelper.getPartyName(delegator, apAdmin.partyId, false)
    entry.put("fullName", fullName)

    def adminUserLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, apAdmin.partyId)
    entry.put("adminUserLoginId", adminUserLoginId)

    def adminUserLoginGV  = delegator.findOne("UserLogin", ["userLoginId": adminUserLoginId], false)
    String adminUserLoginEnabled = adminUserLoginGV.enabled
    def userStatus = null;
    if(adminUserLoginEnabled == null) {
        userStatus = UserStatusConstants.INACTIVE
    } else if(adminUserLoginEnabled.equalsIgnoreCase("Y")){
        userStatus = UserStatusConstants.ACTIVE
    } else {
        if(adminUserLoginGV.disabledDateTime == null){
            userStatus = UserStatusConstants.SUSPENDED
        } else {
            userStatus = UserStatusConstants.LOCKED
        }
    }
    entry.put("userStatus", userStatus)

    def adminLoggedDetail = delegator.findByAnd("UserLoginHistory",
            UtilMisc.toMap("userLoginId",adminUserLoginGV.userLoginId, "successfulLogin", "Y"),
            UtilMisc.toList("fromDate DESC"),false)
    if(adminLoggedDetail != null && adminLoggedDetail.size() > 0 ){
        def userDetail = adminLoggedDetail.get(0);
        Timestamp lastLoggedInTs = userDetail.fromDate

        String lastLoggedInPrettyTime = prettyTime.format(new Date(lastLoggedInTs.getTime()))
        entry.put("lastLoggedInDate", userDetail.fromDate)
        entry.put("lastLoggedInPrettyTime", lastLoggedInPrettyTime)

        if(adminUserLoginId == userLogin.userLoginId && adminLoggedDetail.size()>1) {
            // Current User - prev login info
            def previousLoginHistory = adminLoggedDetail.get(1);

            if(UtilValidate.isNotEmpty(previousLoginHistory)) {
                context.loggedInUserLastLoggedIn = previousLoginHistory.fromDate
                String previousLoginPrettyTime = prettyTime.format(new Date(previousLoginHistory.fromDate.getTime()))
                context.previousLoginPrettyTime = previousLoginPrettyTime

            }
        }
    }

    adminList.add(entry)
    if(adminList.size()>maxUsersToShow) break;
}
context.adminUsers = adminList;

tenantOrgParties = delegator.findByAnd("TenantOrgParty", null, UtilMisc.toList("createdStamp") ,false);

context.totalCustomerCount = tenantOrgParties.size();

// TODO: Later fetch active customers, expired customers

products = delegator.findByAnd("Product", UtilMisc.toMap("productTypeId", "SUBSCRIPTION_PLAN"), null, false);
List<Map> plansList = new ArrayList();
def maxSubscriptionCountForPlan = 0
for(GenericValue product : products) {
    Map entry = UtilMisc.toMap("productId", product.productId);
    entry.put("productName", product.productName);

    def getSubscriptionsResp = dispatcher.runSync("getSubscriptions",
            UtilMisc.toMap("status", "ACTIVE","productId", product.productId,"userLogin", userLogin))
    if(ServiceUtil.isSuccess(getSubscriptionsResp)) {
        def activeSubscriptions = getSubscriptionsResp.get("subscriptions")
        def planSubscriptionCount = activeSubscriptions.size();
        if(maxSubscriptionCountForPlan < planSubscriptionCount) maxSubscriptionCountForPlan = planSubscriptionCount
        entry.put("activeSubscriptionsCount", planSubscriptionCount)
    }
    plansList.add(entry);
}
context.maxSubscriptionCountForPlan = maxSubscriptionCountForPlan;
context.plans = plansList;