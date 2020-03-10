<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>

<#if !errorMessage?has_content>
    <#assign errorMessage = requestAttributes._ERROR_MESSAGE_!>
</#if>
<#if !errorMessageList?has_content>
    <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_!>
</#if>
<div class="container-fluid">
    <div>
        <#list errorMessageList as error>
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
        </#list>
    </div>
    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>New Admin</h4>
            </div>
            <div class="col-sm-7">
                <a href="<@ofbizUrl>manage_users</@ofbizUrl>" class="btn btn-secondary"><i class="material-icons">keyboard_backspace</i> <span>Back</span></a>
                <#--<a href="#" class="btn btn-primary"><i class="material-icons">&#xE24D;</i> <span>Export to Excel</span></a>-->
            </div>
        </div>
    </div>

    <div class="col-md-9 my-3">
        <form action="<@ofbizUrl>CreateAdmin</@ofbizUrl>" method="post">
            <div class="form-group row required">
                <label for="userFirstName" class="col-sm-2 col-form-label">First Name <span class="mandatory">*</span></label>
                <div class="col-sm-6">
                    <input type="text" class="form-control" id="userFirstName" name="firstname" placeholder="" required>
                </div>
            </div>
            <div class="form-group row">
                <label for="userLastName" class="col-sm-2 col-form-label">Last Name <span class="mandatory">*</span></label>
                <div class="col-sm-6">
                    <input type="text" class="form-control" id="userLastName" name="lastname" placeholder="" required>
                </div>
            </div>
            <div class="form-group row">
                <label for="userEmail" class="col-sm-2 col-form-label">Email <span class="mandatory">*</span></label>
                <div class="col-sm-6">
                    <input type="email" class="form-control" id="userEmail" name="email" placeholder="name@domain.com" onblur="checkIfAdminEmailExists()" required>
                    <span id="email_notAvailable" class="text-danger d-none">This Email address has already been used</span>
                </div>
            </div>
            <div class="form-group row">
                <label for="userPassword" class="col-sm-2 col-form-label">Password  <span class="mandatory">*</span></label>
                <div class="col-sm-6">
                    <div class="input-container">
                        <input type="password" class="form-control" id="password" placeholder="Initial Password" name="password" required
                               onblur="checkPasswordPolicy('password','password_policy_error')"
                        >
                        <i class="fa fa-eye p_eye" aria-hidden="true" id="password_eye"></i>
                    </div>
                    <div id="password_policy_error"></div>
                    <small id="passwordHelpBlock" class="form-text text-muted">
                        User will be prompted to change their password when they login for the first time.
                    </small>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2">&nbsp;</div>
                <div class="col-sm-10">
                    <button type="submit" class="btn btn-primary">Add Admin</button>
                    <a href="<@ofbizUrl>manage_users</@ofbizUrl>" class="btn btn-secondary">Cancel</a>
                </div>
            </div>
        </form>
    </div>
</div>