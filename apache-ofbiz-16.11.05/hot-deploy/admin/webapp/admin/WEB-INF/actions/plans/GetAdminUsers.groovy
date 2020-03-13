import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.party.party.PartyHelper
import com.autopatt.admin.utils.*
import com.autopatt.admin.constants.*;
import org.apache.ofbiz.base.util.UtilValidate

adminDetails = delegator.findByAnd("PartyRoleAndPartyDetail", UtilMisc.toMap("roleTypeId", "AUTOPATT_ADMIN"), null, false);

List<Map> adminList = new ArrayList();
for(GenericValue apAdmin : adminDetails) {
    Map entry = UtilMisc.toMap("firstName", apAdmin.firstName);
    entry.put("lastName", apAdmin.lastName);

    String fullName = PartyHelper.getPartyName(delegator, apAdmin.partyId, false)
    entry.put("fullName", fullName);

    entry.put("createdDate", apAdmin.createdDate);
    entry.put("partyId", apAdmin.partyId);

    // get userLoginId
    def adminUserLoginId = UserLoginUtils.getUserLoginIdForPartyId(delegator, apAdmin.partyId)
    entry.put("adminUserLoginId", adminUserLoginId)

    // get UserLogin from DB
    def adminUserLoginGV  = delegator.findOne("UserLogin", ["userLoginId": adminUserLoginId], false)
    if(UtilValidate.isNotEmpty(adminUserLoginGV)) {
        String adminUserLoginEnabled = adminUserLoginGV.enabled;
        def userStatus = null;
        if (adminUserLoginEnabled == null) {
            userStatus = UserStatusConstants.INACTIVE
        } else if (adminUserLoginEnabled.equalsIgnoreCase("Y")) {
            userStatus = UserStatusConstants.ACTIVE
        } else {
            if (adminUserLoginGV.disabledDateTime == null) {
                userStatus = UserStatusConstants.SUSPENDED
            } else {
                userStatus = UserStatusConstants.LOCKED
            }
        }
        entry.put("userStatus", userStatus);
    }
    adminList.add(entry);
}
context.adminUsers = adminList;






