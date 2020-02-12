

<#--<div class="table-title">
    <div class="row">
        <div class="col-sm-5">
            <h2>Total Employees: <span class="badge badge-secondary">${employees!?size}</span></h2>
        </div>
        <div class="col-sm-7">
        </div>
    </div>
</div>-->
<input type="hidden" name="orgPartyId" value="${orgPartyId}"/>

<div>
    <a href="javascript:void(0);" class="btn btn-outline-primary btn-sm mb-2" onclick="loadOrgEmployees()"><i class="fa fa-refresh" aria-hidden="true"></i>
        Refresh</a>
    <div class="float-right">
        <div class="form-group float-right justify-content-end">
            <a href="javascript:void(0);"
               data-target="#createEmployeeModal"
               class="btn btn-outline-primary btn-sm" title="Add a new Employee" data-toggle="modal"
               data-org-party-id="${orgPartyId!}">
                <i class="material-icons">&#xE147;</i> <span>New Employee</span>
            </a>
        </div>
    </div>
</div>

<table class="table table-striped table-hover">
    <caption>Total Employees - <b>${employees!?size}</b> of 5 <span class="small text-muted">(max)</span></caption>

    <thead class="thead-dark">
    <tr>
        <th>#</th>
        <th>Name</th>
        <th>Date Created</th>
        <th>Role</th>
        <th>Status</th>
        <#--<th>Action</th>-->
    </tr>
    </thead>
    <tbody>
    <#if employees??>
        <#list employees as emp>
            <tr>
                <td>${emp_index + 1}</td>
                <td class="user-name">
                    <i class="material-icons" style="font-size:1.6em;">account_circle</i>
                    <a href="#" data-toggle="modal" data-target="#editEmployeeModal"
                       data-party-id="${emp.partyId}" data-party-name="${emp.partyName!}"
                       data-org-party-id="${orgPartyId!}">${emp.partyName!}</a>
                    <div class="small text-muted">${emp.userLogin.userLoginId!}</div>
                </td>
                <td><#if emp.userLogin.createdStamp??>${emp.userLogin.createdStamp!?date}</#if></td>
                <td>${emp.roleName!}</td>
                <td width="20%">
                    <#if emp.userStatus?? && emp.userStatus == "ACTIVE">
                        <span class="status text-success" >&#8226;</span> <span>Active</span>
                    <#elseif emp.userStatus?? && emp.userStatus == "INACTIVE">
                        <div title="User hasn't logged in yet"><span class="status text-info">&bull;</span> In-Active </div>
                    <#elseif emp.userStatus?? && emp.userStatus == "LOCKED">
                        <div title="User locked due to failed logins"><span class="status text-warning">&bull;</span> Locked </div>
                    <#else>
                        <div title="User has been disabled"><span class="status text-danger">&bull;</span> Suspended </div>
                    </#if>
                </td>

                <#--<td width="20%">
                    <#if emp.userStatus?? && emp.userStatus == "ACTIVE">
                        <a href="#"
                           data-target="#suspendEmployeeConfirmModal"
                           class="btn btn-outline-danger" title="Suspend" data-toggle="modal"
                           data-party-id="${emp.partyId}" data-party-name="${emp.partyName!}"
                           data-org-party-id="${orgPartyId!}">
                            <i class="fa fa-lock" aria-hidden="true"></i>
                        </a>
                        <a href="#"
                           data-target="#resetPasswordEmployeeConfirmModal"
                           class="btn btn-outline-info" title="Reset Password" data-toggle="modal"
                           data-party-id="${emp.partyId}" data-party-name="${emp.partyName!}"
                           data-org-party-id="${orgPartyId!}" data-user-login-id="${emp.userLogin.userLoginId!}">
                            <i class="fa fa-key" aria-hidden="true"></i>
                        </a>
                    <#else>
                        <a href="#"
                           data-target="#activateEmployeeConfirmModal"
                           class="btn btn-outline-primary" title="Activate" data-toggle="modal"
                           data-party-id="${emp.partyId}" data-party-name="${emp.partyName!}"
                           data-org-party-id="${orgPartyId!}">
                            <i class="fa fa-unlock" aria-hidden="true"></i>
                        </a>
                    </#if>

                    <a href="#" class="btn btn-outline-danger" title="Remove" data-toggle="modal"
                       data-target="#deleteEmployeeConfirmModal"
                       data-party-id="${emp.partyId}" data-party-name="${emp.partyName!}"
                       data-org-party-id="${orgPartyId!}">
                        <i class="fa fa-trash-o" aria-hidden="true"></i>
                    </a>
                </td>-->
            </tr>
        </#list>
    </#if>
    </tbody>
</table>

<form id="suspend_org_employee_form" action="<@ofbizUrl>ajaxSuspendOrgUser</@ofbizUrl>">
    <input type="hidden" id="suspendEmployee_partyId">
</form>

<form id="enable_org_employee_form" action="<@ofbizUrl>ajaxActivateOrgUser</@ofbizUrl>">
    <input type="hidden" id="enableEmployee_partyId">
</form>

<form id="delete_org_employee_form" action="<@ofbizUrl>ajaxDeleteOrgUser</@ofbizUrl>">
    <input type="hidden" id="deleteEmployee_partyId">
</form>


<div class="modal fade" id="activateEmployeeConfirmModal" tabindex="-1" role="dialog" aria-labelledby="activateEmployeeModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Activate</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to activate account for <b><span id="activatePartyName"></span></b>?
                <br/>
                <small>User will be able to login to Portal.</small>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-success" onclick="activateOrgEmployee();">Activate</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="suspendEmployeeConfirmModal" tabindex="-1" role="dialog" aria-labelledby="suspendEmployeeModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Suspend</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to suspend account for <b><span id="suspendPartyName"></span></b>?
                <br/>
                <small>User will not be able to login to Portal.</small>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" onclick="suspendOrgEmployee()">Suspend</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="resetPasswordEmployeeConfirmModal" tabindex="-1" role="dialog" aria-labelledby="resetPasswordEmployeeModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Password Reset</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to reset password for <b><span id="resetPasswordForPartyName"></span></b>?
                <br/>
                <small>An email will be sent to user with a link to set their password.</small>
            </div>
            <input type="hidden" name="resetPasswordUserLoginId" id="resetPasswordUserLoginId" value=""/>
            <input type="hidden" name="resetPasswordOrgPartyId" id="resetPasswordOrgPartyId" value=""/>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="initResetEmployeePwd()">Reset Password</button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="deleteEmployeeConfirmModal" tabindex="-1" role="dialog" aria-labelledby="deleteEmployeeModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Remove User</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to remove account for <b><span id="deleteEmployeePartyName"></span></b>?
                <br/> <br/>
                <div class="alert alert-danger"><i>Note: This action is not reversible.</i></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" onclick="deleteOrgEmployee();">Remove</button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="editEmployeeModal" tabindex="-1" role="dialog" aria-labelledby="editEmployeeModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel"><span id="editEmployee_name"></span></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div id="editEmployeeModal_Details"></div>
            </div>

        </div>
    </div>
</div>

<div class="modal fade" id="createEmployeeModal" tabindex="-1" role="dialog" aria-labelledby="createEmployeeModal" aria-hidden="true">
    <form id="createEmployee-form">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4>Create Employee</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <input type="hidden" id="createEmployee_orgPartyId" name="orgPartyId" value="${orgPartyId!}"/>
            <div class="modal-body">
                <div class="col-md-12 my-3">
                    <div class="form-group row required">
                        <label for="createEmployee_firstName" class="col-sm-10 col-form-label">First Name <span class="mandatory">*</span></label>
                        <div class="col-sm-10">
                            <input type="text" id="createEmployee_firstName" class="form-control" name="firstName" required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="createEmployee_lastName" class="col-sm-10 col-form-label">Last Name <span class="mandatory">*</span></label>
                        <div class="col-sm-10">
                            <input type="text" id="createEmployee_lastName" class="form-control"  name="lastName" required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="createEmployee_email" class="col-sm-10 col-form-label"> E-mail </label>
                        <div class="col-sm-10">
                            <input type="email" class="form-control" id="createEmployee_email" name="email"  onblur="checkEmailEmp()" required>
                            <span id="email_notExists" class="text-danger d-none">This Email address has already been used</span>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="createEmployee_password" class="col-sm-10 col-form-label">Password  <span class="mandatory">*</span></label>
                        <div class="col-sm-10">
                            <div class="input-container">
                                <input type="password" class="form-control" id="createEmployee_password" name="empPassword" required>
                                <#--<i class="fa fa-eye" aria-hidden="true" id="eye"></i>-->
                            </div>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="createEmployee_role" class="col-sm-10 col-form-label">Role</label>
                        <div class="col-sm-10">
                            <select name="securityGroupId" id="createEmployee_role" class="form-control" required>
                                <option value="AP_PLANNER">Planner</option>
                                <option value="AP_APPROVER">Approver</option>
                                <option value="AP_DEPLOYER">Deployer</option>
                                <option value="AP_FULLADMIN">Administrator</option>
                            </select>
                        </div>
                    </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="addEmployeeDetails();">Create</button>
            </div>
        </div>
        </div>
    </div>
    </form>
</div>








