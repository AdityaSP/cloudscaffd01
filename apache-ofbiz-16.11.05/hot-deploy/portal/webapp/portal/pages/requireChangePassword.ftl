
<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException??><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#if !errorMessage?has_content>
    <#assign errorMessage = requestAttributes._ERROR_MESSAGE_!>
</#if>
<#if !errorMessageList?has_content>
    <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_!>
</#if>
<#if !eventMessage?has_content>
    <#assign eventMessage = requestAttributes._EVENT_MESSAGE_!>
</#if>
<#if !eventMessageList?has_content>
    <#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_!>
</#if>

<div class="container-fluid">

    <div class="login-sidenav">
        <div class="login-main-text">
            <h2>AutoPatt Console</h2>
            <p>In order to <b>protect your account</b>, we require you to change your password as you have just logged in using a temporary password.</p>
        </div>
    </div>
    <div class="login-main">
        <div class="col-md-6 col-sm-12">
            <div class="login-form">
                <h3>Change Your Password</h3>
                <div><hr/></div>
                <br/>
                <div>
                    <#list errorMessageList as error>
                        <div class="alert alert-danger" role="alert">
                            ${error}
                        </div>
                    </#list>
                </div>

                <form id="login" action="<@ofbizUrl>requireChangePassword</@ofbizUrl>" method="post">
                    <input type="hidden" name="requirePasswordChange" value="Y"/>
                    <input type="hidden" name="USERNAME" value="${requestAttributes.USERNAME!}"/>
                    <input type="hidden" name="userTenantId" value="${delegator.getDelegatorTenantId()}"/>

                    <div class="form-group">
                        <label>Current Password</label>
                        <div class="input-container">
                            <input type="password" class="form-control" id="password" name="PASSWORD" required>
                            <i class="fa fa-eye p_eye" aria-hidden="true" id="password_eye"></i>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>New Password</label>
                        <div class="input-container">
                            <input type="password" class="form-control" id="newPassword" name="newPassword"
                                   onblur="checkPasswordPolicy('newPassword','password_policy_error')"
                                   required>
                            <i class="fa fa-eye p_eye" aria-hidden="true" id="newPassword_eye"></i>
                        </div>
                        <div id="password_policy_error"></div>
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <div class="input-container">
                            <input type="password" class="form-control" id="newPasswordVerify" name="newPasswordVerify"
                                   onblur="checkPasswordPolicy('newPasswordVerify','confirm_password_policy_error')"
                                   required>
                            <i class="fa fa-eye p_eye" aria-hidden="true" id="newPasswordVerify_eye"></i>
                        </div>
                        <div id="confirm_password_policy_error"></div>
                    </div>
                    <button type="submit" class="btn btn-primary">Change</button>
                </form>
            </div>
        </div>
    </div>


