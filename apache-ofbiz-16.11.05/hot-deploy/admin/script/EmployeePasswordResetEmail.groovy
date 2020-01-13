import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilProperties
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil

String productStoreId = UtilProperties.getPropertyValue("subscription.properties","autopatt.product.store", "AUTOPATT_STORE");
String PORTAL_HOST_URL = UtilProperties.getPropertyValue("portal.properties","autopatt.portal.host", "http://localhost:8080/portal");

Debug.logInfo("-=-=-=- Sending Employee password reset Email -=-=-=-", "")
result = ServiceUtil.returnSuccess()
String module = "EmployeePasswordResetEmail.groovy"

String organizationName = context.organizationName
String employeePartyId = context.employeePartyId
String employeePartyName = context.employeePartyName
String employeeEmail = context.employeeEmail
String passwordResetToken = context.passwordResetToken

if (employeeEmail) {
    Debug.logInfo("----- Sending email to: $employeeEmail -----", module)

    String emailType = "EMP_PWD_RESET"
    GenericValue productStoreEmailSetting = delegator.findOne("ProductStoreEmailSetting",
            UtilMisc.toMap("productStoreId",productStoreId, "emailType", emailType), false)

    if(UtilValidate.isNotEmpty(productStoreEmailSetting)) {
        Map bodyParameters = UtilMisc.toMap("employeePartyId", employeePartyId )
        bodyParameters.put("employeeEmail", employeeEmail)
        bodyParameters.put("employeePartyName", employeePartyName)
        bodyParameters.put("organizationName", organizationName)
        bodyParameters.put("PORTAL_HOST_URL", PORTAL_HOST_URL)
        bodyParameters.put("passwordResetToken", passwordResetToken)

        Debug.logInfo("Body Parameters: " + bodyParameters, module);

        dispatcher.runSync("sendMailFromScreen",
                UtilMisc.toMap("userLogin", userLogin,
                        "sendTo", employeeEmail,
                        "sendFrom", productStoreEmailSetting.getString("fromAddress"),
                        "subject", productStoreEmailSetting.getString("subject"),
                        "bodyScreenUri", productStoreEmailSetting.getString("bodyScreenLocation"),
                        "bodyParameters", bodyParameters));

        result.successMessage = (String) "Email Sent to [$employeeEmail] "
        result.result = "Email Sent"
    } else  {
        return ServiceUtil.returnFailure("No Product store email setting found")
    }

} else {
    Debug.logError("Got no SendTo email id", module)
    return ServiceUtil.returnFailure("Got no SendTo email id")
}

return result
