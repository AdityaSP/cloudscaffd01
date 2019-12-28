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
            <h2>AutoPatt Console<br> Login </h2>
            <p>Login here to access the AutoPatt Console.</p>
         </div>
      </div>
      <div class="login-main">
         <div class="col-md-6 col-sm-12">
         <div>

            <div class="login-form">
                <div>
                    <#list errorMessageList as error>
                        <div class="alert alert-danger" role="alert">
                            ${error}
                        </div>
                    </#list>
                </div>
                <h4>Enter Login Details</h4>
                <div><hr/></div>

                <form id="login" action="<@ofbizUrl>login</@ofbizUrl>" method="post">

                    <div class="form-group">
                     <label>Organization Id</label>
                     <input type="text" class="form-control" placeholder="xyzcorp" name="userTenantId">
                  </div>

                    <div class="form-group">
                        <label>Email Address</label>
                        <input type="text" class="form-control" placeholder="user@xyzcorp.com" name="USERNAME">
                    </div>
                    <div class="form-group">
                     <label>Password</label>
                     <input type="password" class="form-control" placeholder="" name="PASSWORD">
                  </div>
                  <button type="submit" class="btn btn-primary">Login</button>
                  <hr/>
                  <a href="<@ofbizUrl>initForgotPwd</@ofbizUrl>" class="text-decoration-underline tz-text">I forgot my password!</a>

               </form>
            </div>
         </div>
      </div>
</div>


