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
            <div class="col-sm-4">
                <h4>Edit Plan</h4>
            </div>
            <div class="col-sm-8">
                <a href="<@ofbizUrl>manage_plans</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">keyboard_backspace</i> <span>Back</span></a>
                <#--<a href="#" class="btn btn-primary"><i class="material-icons">&#xE24D;</i> <span>Export to Excel</span></a>-->
            </div>
        </div>
    </div>
</div>
<div class="col-sm-7">

</div>

<form action="<@ofbizUrl>UpdatePlan</@ofbizUrl>" method="post">
    <div class="col-md-9 my-3">
        <div class="form-group row required">
            <label for="productId" class="col-sm-2 col-form-label">Plan Id</label>
            <div class="col-sm-10">
                <input id="productId" type="text" class="form-control" readonly value="${product.productId!}" name="planId" required/>
            </div>
        </div>
        <div class="form-group row required">
            <label for="productName" class="col-sm-2 col-form-label">Plan Name <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input id="productName" type="text" class="form-control" value="${product.productName!}" name="productName" required/>
            </div>
        </div>
        <div class="form-group row">
            <label for="productPrice" class="col-sm-2 col-form-label ">Price <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input id="productPrice" type="number" step="any" class="form-control"  value="${priceGv.price}" name="price" required>
            </div>
        </div>
<#--        <div class="form-group row">
            <label for="maxAdmins" class="col-sm-2 col-form-label">Max Admins <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input type="number" id="maxAdmins" class="form-control" value="${maxAdmins!}" name="maxAdmins" />
            </div>
        </div>-->
        <div class="form-group row">
            <label for="maxUsers" class="col-sm-2 col-form-label">Max Users <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input id="maxUsers" type="number" class="form-control" value="${maxUserLogins!}" name="maxUserLogins" required/>
            </div>
        </div>
        <div class="form-group row">
            <label for="maxPatterns" class="col-sm-2 col-form-label">Max Patterns <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input id="maxPatterns" type="number" class="form-control" value="${maxPatterns!"0"}" name="maxPatterns" required/>
            </div>
        </div>

        <div class="form-group row">
            <div class="col-sm-2">&nbsp;</div>
            <div class="col-sm-10">
                <button type="submit" class="btn btn-primary">Update</button>
                <a href="<@ofbizUrl>manage_plans</@ofbizUrl>" class="btn btn-secondary">Cancel</a>
            </div>
        </div>
    </div>
</form>
