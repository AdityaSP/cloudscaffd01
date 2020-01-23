var admins = {
    removeUser: _removeUser
};
$(function () {
    // initialize things..

});
function _removeUser() {
    var userPartyId = $("#deleteAdminUser_partyId").val();
    console.log("deleting " + userPartyId)
    var postData = {adminPartyId: userPartyId};
    var formURL = $("#delete_admin_user_form").attr("action");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#deleteAdminUserConfirmModal').modal('hide');
                showSuccessToast("Admin User Deleted Successfully");
                location.reload();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}

// Check if email already exists for admin user
function checkIfAdminEmailExists() {
    var email = $("#userEmail").val()
    var postData = {email: email};
    var formURL = getUrl("checkIfEmailAlreadyExists");
    $("#email_notAvailable").addClass("d-none");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function(resp) {
                if(resp.EMAIL_EXISTS === "YES") {
                    $("#email_notAvailable").removeClass("d-none");
                } else {
                }
            },
            error: function (EMAIL_EXISTS) {
            }
        });
}
function checkIfOrgIdExists() {
    var tenantId = $("#organizationId").val()
    var postData = {tenantId: tenantId};
    var formURL = getUrl("checkIfOrgIdAlreadyExists");
    $("#orgId_notAvailable").addClass("d-none");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function(resp) {
                console.log("id")
                if(resp.ORGID_EXISTS === "YES") {
                    $("#orgId_notAvailable").removeClass("d-none");
                } else {
                }
            },
            error: function (EMAIL_EXISTS) {
            }
        });
}

function restrictSpecialCharacters(event) {
    var regex = new RegExp("^[a-zA-Z0-9_-]+$");
    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        return false;
    }
}
