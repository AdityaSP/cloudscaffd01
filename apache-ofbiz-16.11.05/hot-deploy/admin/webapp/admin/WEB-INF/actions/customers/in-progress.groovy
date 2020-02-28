import com.autopatt.admin.utils.TenantCommonUtils
import com.autopatt.admin.utils.UserLoginUtils
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil


def transactions = delegator.findByAnd("NewTenantTransaction", null, UtilMisc.toList("createdStamp DESC"), false)

context.transactions = transactions