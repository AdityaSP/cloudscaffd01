<div class="container-fluid">
    <#if requestParameters.createSuccess?? && requestParameters.createSuccess=="Y">
    <div class="alert alert-success toastMsg" role="alert" id="success_div1">
        <i class="material-icons">check</i> User has been added successfully.
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</#if>

<#if requestParameters.updateSuccess?? && requestParameters.updateSuccess=="Y">
<div class="alert alert-success toastMsg" role="alert" id="success_div2">
    <i class="material-icons">check</i> User details updated successfully.
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
    </button>
</div>
</#if>

<#if requestParameters.userReEnabled?? && requestParameters.userReEnabled=="Y">
<div class="alert alert-success" role="alert" id="success_div3">
    <i class="material-icons">check</i> User has been re-enabled successfully.
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
    </button>
</div>
</#if>

<div class="table-title">
    <div class="row">
        <div class="col-sm-5">
            <h4>Users</h4>
        </div>
        <div class="col-sm-7">
            <#if security.hasEntityPermission("PORTAL", "_ADD_USER", session)>
            <a href="<@ofbizUrl>new_user</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">&#xE147;</i> <span>Add New User</span></a>
        </#if>
    </div>
</div>
</div>

<div id="users_list_section">
    ${screens.render("component://portal/widget/PortalScreens.xml#users_list_section")}
</div>
</div>

<form id="delete_user_form" action="<@ofbizUrl>DeleteUser</@ofbizUrl>">
    <input type="hidden" id="deleteUser_partyId">
</form>

<div class="modal fade" id="deleteUserConfirmModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Remove</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to remove <b><span id="deletePartyName"></span></b>?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" onclick="users.removeUser()">Remove</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
         setTimeout(function() {
             $('#success_div1').fadeOut('fast');
             $('#success_div2').fadeOut('fast');
             $('#success_div3').fadeOut('fast');
         }, 4000);

</script>