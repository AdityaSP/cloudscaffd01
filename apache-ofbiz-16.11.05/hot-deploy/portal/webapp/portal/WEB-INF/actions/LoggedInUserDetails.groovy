import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.util.EntityUtil

context.userLogin = userLogin;

loggedInParty = delegator.findOne("Party", UtilMisc.toMap("partyId", userLogin.partyId), false)
context.loggedInParty = loggedInParty

// Get user role
userSecurityGroups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLogin.userLoginId), null, false)
userSecurityGroups = EntityUtil.filterByDate(userSecurityGroups)
userRoleName = "n/a";

if(UtilValidate.isNotEmpty(userSecurityGroups)) {
    GenericValue userSecurityGroup = userSecurityGroups.get(0);
    securityGroup = userSecurityGroup.getRelatedOne("SecurityGroup", false);
    if(UtilValidate.isNotEmpty(securityGroup))
        userRoleName = securityGroup.get("description")
}
context.userRoleName = userRoleName;