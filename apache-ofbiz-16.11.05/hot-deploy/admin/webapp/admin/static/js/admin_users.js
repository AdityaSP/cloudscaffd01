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
function checkEmail() {
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
function checkEmail() {
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
