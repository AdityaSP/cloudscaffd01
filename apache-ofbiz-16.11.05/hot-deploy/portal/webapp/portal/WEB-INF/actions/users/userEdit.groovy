import com.autopatt.common.utils.SecurityGroupUtils
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.util.EntityQuery

partyId = request.getParameter("partyId")
context.partyId = partyId;

Map inputs = UtilMisc.toMap("partyId", partyId)
person = delegator.findOne("Person", inputs, false)
context.person = person

partyUserLogins = delegator.findByAnd("UserLogin", inputs, null, false)
if(partyUserLogins != null && partyUserLogins.size()>0) {
    partyUserLogin = partyUserLogins.get(0)
    context.email = partyUserLogin.userLoginId

    GenericValue userSecurityGroup = SecurityGroupUtils.getUserActiveSecurityGroup(delegator, (String) partyUserLogin.userLoginId)
    context.userSecurityGroup = userSecurityGroup
}

GenericValue onboardedAdminPartyRole = EntityQuery.use(delegator)
        .from("PartyRole")
        .where("partyId", partyId, "roleTypeId", "ONBOARDED_ADMIN")
        .cache(true)
        .queryOne();
if(UtilValidate.isNotEmpty(onboardedAdminPartyRole)) {
    context.isOnboardedAdmin = true
} else {
    context.isOnboardedAdmin = false
}

availableSecurityGroups = SecurityGroupUtils.getAvailableSecurityGroups(delegator)
context.availableSecurityGroups = availableSecurityGroups