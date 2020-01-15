import org.apache.ofbiz.base.util.UtilMisc
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.party.party.PartyHelper
import org.ocpsoft.prettytime.PrettyTime

import java.sql.Timestamp;


PrettyTime prettyTime = new PrettyTime();

getTenantUsersResp = dispatcher.runSync("getTenantUsers", ["userLogin": userLogin, "tenantId": delegator.getDelegatorTenantId()]);

List users = getTenantUsersResp.get("users");

for(Map user: users) {
    String userLoginId = user.get("userLoginId")
    println ">> " + userLoginId

    def userLoginHistories = delegator.findByAnd("UserLoginHistory",UtilMisc.toMap("userLoginId", userLoginId), UtilMisc.toList("fromDate DESC"),false);
    if (userLoginHistories != null && userLoginHistories.size() > 0) {
        def userLoginHistory = userLoginHistories.get(0);
        user.put("lastLoggedInDate", userLoginHistory.fromDate);

        Timestamp lastLoggedInTs = userLoginHistory.fromDate
        String lastLoggedInPrettyTime = prettyTime.format(new Date(lastLoggedInTs.getTime()))
        user.put("lastLoggedInDate", userLoginHistory.fromDate)
        user.put("lastLoggedInPrettyTime", lastLoggedInPrettyTime)
    }
}
context.users = users;

println users





