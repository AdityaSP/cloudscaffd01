<div class="container-fluid">
    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>Settings</h4>
            </div>
        </div>
    </div>
</div>
<form action="<@ofbizUrl>UpdateCompanyDetails</@ofbizUrl>" method="post">
     <#list errorMessageList as error>
          <div class="alert alert-danger mx-3" role="alert">
                ${error}
          </div>
     </#list>
     <#list eventMessageList as success>
        <#if organizationName.groupName != organizationName>
           <div class="alert alert-success mx-3" role="alert">
                 ${success}
           </div>
        </#if>
        <#else>

     </#list>
    <input type="hidden" name="orgPartyId" value="${orgPartyId!}"/>
    <div class="col-md-9 my-3">
        <div class="form-group row required">
            <label for="orgId" class="col-sm-2 col-form-label">Organization Id <span class="mandatory">*</span></label>
            <div class="col-sm-8">
                <input type="text" id="orgId" class="form-control" value="${tenantId!}" name="organizationId" readonly required>
            </div>
        </div>
        <div class="form-group row required">
            <label for="orgName" class="col-sm-2 col-form-label">Organization Name <span class="mandatory">*</span></label>
            <div class="col-sm-8">
                <input type="text" id="orgName" class="form-control" value="${organizationName.groupName!}" name="organizationName" required>
            </div>
        </div>
        <div class="form-group row">
            <div class="col-sm-2">&nbsp;</div>
            <div class="col-sm-10">
                <button type="submit" class="btn btn-primary ">Update</button>
            </div>
        </div>
    </div>
</form>
