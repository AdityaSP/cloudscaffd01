

<div class="table-title">
    <div class="row">
        <div class="col-sm-5">
            <h2>Total Employees: <span class="badge badge-secondary">${employees!?size}</span></h2>
        </div>
        <div class="col-sm-7">
<#--            <a href="<@ofbizUrl>new_user</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">&#xE147;</i> <span>New Employee</span></a>-->
        </div>
    </div>
</div>

<table class="table table-striped table-hover">
    <thead>
    <tr>
        <th>#</th>
        <th>Name</th>
        <th>Date Created</th>
        <th>Role</th>
        <th>Status</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <#if employees??>
        <#list employees as emp>
            <tr>
                <td>${emp_index + 1}</td>
                <td class="user-name">
                    <!-- TODO: clicking on this - show a popup modal with user details (email, phone etc) -->
                    <i class="material-icons" style="font-size:1.6em;">account_circle</i>
                    <a href="#" data-toggle="modal" data-target="#editEmployeeModal"
                       data-party-id="${emp.partyId}" data-party-name="${emp.firstName!} ${emp.lastName!}"
                       title="${emp.userLogin.userLoginId!}">${emp.firstName!} ${emp.lastName!}</a>
                </td>
                <td><#if emp.createdDate??>${emp.createdDate!?date}</#if></td>
                <td>${emp.roleName!}</td>
                <td>
                    <#if emp.userStatus?? && emp.userStatus == "ACTIVE">
                        <span class="status text-success" >&#8226;</span> <span>Active</span>
                    <#elseif emp.userStatus?? && emp.userStatus == "INACTIVE">
                        <span class="status text-warning">&bull;</span> Inactive
                    <#else>
                        <span class="status text-danger">&bull;</span> Suspended
                    </#if>
                </td>

                <td width="20%">
                    <#if emp.userStatus?? && emp.userStatus == "ACTIVE">
                    <a href="#"
                       data-target="#suspendEmployeeConfirmModal"
                       data-party-id="${emp.partyId}" data-party-name="${emp.firstName!} ${emp.lastName!}"
                       class="btn btn-outline-danger" title="Suspend" data-toggle="modal">
                        <i class="fa fa-lock" aria-hidden="true"></i>
                    </a>
                    <#else>
                    <a href="#"
                       class="btn btn-outline-primary" title="Enable" data-toggle="modal">
                        <i class="fa fa-unlock" aria-hidden="true"></i>
                    </a>
                    </#if>
                    <a href="#" class="btn btn-outline-info" title="Reset Password" data-toggle="modal" data-target="#deleteEmployeeConfirmModal"
                       data-party-id="${emp.partyId}" data-party-name="${emp.firstName!} ${emp.lastName!}"><i class="fa fa-key" aria-hidden="true"></i>
                    </a>
                    <#--<a href="#" class="btn btn-outline-danger" title="Remove" data-toggle="modal" data-target="#deleteEmployeeConfirmModal"
                       data-party-id="${emp.partyId}" data-party-name="${emp.firstName!} ${emp.lastName!}"><i class="fa fa-trash-o" aria-hidden="true"></i>
                    </a>-->
                </td>
            </tr>
        </#list>
    </#if>
    </tbody>
</table>


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
                Are you sure you want to suspend <b><span id="suspendPartyName"></span></b>?
                <br/>
                User will not be able to login to Portal.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger">Suspend</button>
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
                Show user details here.<br/>
                Option to change Role
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary">Update</button>
            </div>
        </div>
    </div>
</div>






