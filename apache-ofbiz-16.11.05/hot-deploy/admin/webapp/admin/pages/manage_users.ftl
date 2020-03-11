<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if !eventMessage?has_content>
<#assign eventMessage = requestAttributes._EVENT_MESSAGE_!>
</#if>
<div class="container-fluid">
    <div>
        <#list eventMessageList as success>
        <div class="alert alert-success toastMsg" role="alert">
            ${success}
        </div>
    </#list>

</div>
    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>Support Users</h4>
            </div>
            <div class="col-sm-7">
                <a href="<@ofbizUrl>new_admin_user</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">&#xE147;</i> <span>New User</span></a>
                <#--<a href="#" class="btn btn-primary"><i class="material-icons">&#xE24D;</i> <span>Export to Excel</span></a>-->
            </div>
        </div>
    </div>

    <div class="table-content">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Date Created</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <#list adminUsers as admin>
                <tr>
                    <td>${admin_index + 1}</td>
                    <td><a href="<@ofbizUrl>edit_admin_user?partyId=${admin.partyId!}</@ofbizUrl>">${admin.fullName!}</a>
                        <div class="small text-muted">${admin.adminUserLoginId!}</div>
                    </td>
                    <td><#if admin.createdDate??>${admin.createdDate!?date}</#if></td>
                     <td>
                         <#if admin.userStatus?? && admin.userStatus == "ACTIVE">
                             <span class="status text-success" >&#8226;</span> <span>Active</span>
                         <#elseif admin.userStatus?? && admin.userStatus == "INACTIVE">
                             <div title="User hasn't logged in yet"><span class="status text-info">&bull;</span> In-Active</div>
                         <#elseif admin.userStatus?? && admin.userStatus == "LOCKED">
                             <div title="User locked due to failed logins"><span class="status text-warning">&bull;</span> Locked </div>
                         <#else>
                             <div title="User has been disabled"><span class="status text-danger">&bull;</span> Suspended </div>
                         </#if>
                     </td>
                    <td>
                      <a href="<@ofbizUrl>edit_admin_user?partyId=${admin.partyId!}</@ofbizUrl>" class="settings" title="Edit"
                         data-toggle="tooltip">
                          <i class="material-icons">edit</i></a>
                         <#if admin.partyId != userLogin.partyId>
                           <a href="#" class="delete" title="Remove" data-toggle="modal" data-target="#deleteAdminUserConfirmModal"
                           data-party-id="${admin.partyId!}" data-party-name="${admin.fullName!}"><i class="material-icons">delete</i></a>
                         </#if>
                    </td>
                  </tr>
            </#list>

            </tbody>
        </table>
    </div>
</div>

<form id="delete_admin_user_form" action="<@ofbizUrl>DeleteAdminUser</@ofbizUrl>">
    <input type="hidden" id="deleteAdminUser_partyId">
</form>

<div class="modal fade" id="deleteAdminUserConfirmModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Remove</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to remove <b><span id="deleteAdminUser_partyName"></span></b>?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" onclick="admins.removeUser()">Remove</button>
            </div>
        </div>
    </div>
</div>