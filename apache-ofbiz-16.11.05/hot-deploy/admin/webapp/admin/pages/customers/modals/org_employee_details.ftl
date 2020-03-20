

<form action="<@ofbizUrl>updateEmployee</@ofbizUrl>" id="update-employee-form" method="post">
    <input type="hidden" id="updateEmployee_partyId" name="partyId" value="${partyId!}"/>
    <input type="hidden" id="updateEmployee_orgPartyId" name="orgPartyId" value="${orgPartyId!}"/>
    <div class="col-md-12 my-3">
        <div class="form-group row required">
            <label for="fnamacc" class="col-sm-10 col-form-label">First Name <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input type="text" id="updateEmployee_firstName" class="form-control" value=" ${employee.firstName}" name="firstname" required>
            </div>
        </div>
        <div class="form-group row">
            <label for="lnameacc" class="col-sm-10 col-form-label">Last Name <span class="mandatory">*</span></label>
            <div class="col-sm-10">
                <input type="text" id="updateEmployee_lastName" class="form-control" value="${employee.lastName}" name="lastname" required>
            </div>
        </div>
        <div class="form-group row">
            <label for="emailacc" class="col-sm-10 col-form-label"> E-mail </label>
            <div class="col-sm-10">
                <input type="text" id="updateEmployee_email" class="form-control"  value="${email}" name="email" readonly  required>
            </div>
        </div>
        <div class="form-group row">
            <label for="roleacc" class="col-sm-10 col-form-label">Role</label>
            <div class="col-sm-10">
                <select name="securityGroupId" id="updateEmployee_role" class="form-control" required>
                    <#list availableSecurityGroups as secGroup>
                        <option value="${secGroup.groupId!}"
                                <#if userSecurityGroup?? && secGroup.groupId == userSecurityGroup.groupId>selected</#if>
                        >${secGroup.description!}
                        </option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-primary" onclick="saveEmployeeDetails()">Update</button>
        </div>
    </div>
</form>
