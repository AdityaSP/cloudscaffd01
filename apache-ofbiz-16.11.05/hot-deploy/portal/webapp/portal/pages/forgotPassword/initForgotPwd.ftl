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
        <#include "../common/preauth_logo.ftl"/>
        <div class="login-main-text">
            <h2>AutoPatt Portal</h2>
            <p>In order to <b>Reset your account password</b>, Please enter your organization and email, you will receive reset password link</p>
        </div>
    </div>
    <div class="login-main">
        <div class="col-md-6 col-sm-12">
            <div class="login-form">
                <h3>Forgot Password</h3>
                <div>
                    <hr/>
                </div>
                <br/>
                <div>
                    <#list errorMessageList as error>
                        <div class="alert alert-danger" role="alert">
                            ${error}
                        </div>
                    </#list>
                </div>

                <form id="login" action="<@ofbizUrl>forgotPassword</@ofbizUrl>" method="post">
                    <div class="form-group">
                        <label>Organization Id</label>
                        <input type="text" class="form-control" placeholder="xyzcorp" name="userTenantId">
                    </div>
                    <div class="form-group">
                        <label>Email Address</label>
                        <input type="text" class="form-control" placeholder="user@xyzcorp.com" name="USERNAME">
                    </div>
                    <button type="submit" class="btn btn-primary">Send Link</button>
                </form>
            </div>
        </div>
    </div>
</div>

