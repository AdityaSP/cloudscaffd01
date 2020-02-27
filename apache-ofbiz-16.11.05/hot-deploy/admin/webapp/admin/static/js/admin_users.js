var admins = {
    removeUser: _removeUser
};
$(function () {

    updatePassword();

});
function _removeUser() {
    var userPartyId = $("#deleteAdminUser_partyId").val();
    console.log("deleting " + userPartyId)
    var postData = { adminPartyId: userPartyId };
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
    var postData = { email: email };
    var formURL = getUrl("checkIfEmailAlreadyExists");
    $("#email_notAvailable").addClass("d-none");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (resp) {
                if (resp.EMAIL_EXISTS === "YES") {
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
    var postData = { tenantId: tenantId };
    var formURL = getUrl("checkIfOrgIdAlreadyExists");
    $("#orgId_notAvailable").addClass("d-none");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (resp) {
                console.log("id")
                if (resp.ORGID_EXISTS === "YES") {
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

function updatePassword() {

    $('.changePasswordFormSubmitBtn').on('click', function (evt) {
        let formData = {
            "PASSWORD": $('#password').val(),
            "newPassword": $('#newPassword').val(),
            "newPasswordVerify": $('#newPasswordVerify').val()
        }
        $.ajax({
            url: "updatePassword",
            type: "POST",
            data: formData,
            success: function (resp) {
                renderError(resp);
            },
            error: function (resp) {
                renderError(resp);
            }
        });
    });
}

function renderError(res) {
    if (res.message == "success") {
        $('.toastMsgDiv').children().remove();
        if (typeof (res.info) == "object") {
            for (let i = 0; i < res.info.length; i++) {
                $('.toastMsgDiv').append(`<div class="alert alert-success">${res.info[i]}</div>`);
            }
        } else {
            $('.toastMsgDiv').append(`<div class="alert alert-success">${res.info}</div>`);
        }
        $('input:not(.changePasswordFormSubmitBtn)').val('');
        $('.changePasswordFormSubmitBtn').attr('disabled', true);
        setTimeout(function () {
            window.open('logout', '_self');
        }, 2500);

    } else if (res.message == "error") {
        $('.toastMsgDiv').children().remove();
        if (typeof (res.info) == "object") {
            for (let i = 0; i < res.info.length; i++) {
                $('.toastMsgDiv').append(`<div class="alert alert-danger">${res.info[i]}</div>`);
            }
        } else {
            $('.toastMsgDiv').append(`<div class="alert alert-danger">${res.info}</div>`);
        }
        $('input:not(.changePasswordFormSubmitBtn)').val('');

    } else {
        console.log(res);
    }
}