$(function () {
    initiateUsersMgmtModals();

    updatePassword();
});


function initiateUsersMgmtModals() {
    $('#deleteUserConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var deletingPartyId = button.data('party-id') // Extract info from data-* attributes
        var deletingPartyName = button.data('party-name');
        if (deletingPartyName == null) deletingPartyName = "";

        var modal = $(this)
        modal.find('#deletePartyName').text(deletingPartyName);
        $("#deleteUser_partyId").val(deletingPartyId);
    })

    $('#suspendUserConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var suspendingPartyId = button.data('party-id');
        var suspendUserPartyName = button.data('party-name');
        if (suspendUserPartyName == null) suspendUserPartyName = "";

        var modal = $(this);
        modal.find('#suspendUserPartyName').text(suspendUserPartyName)
        $("#suspendUser_partyId").val(suspendingPartyId)
    });

    $('#activateUserConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var activateUserPartyId = button.data('party-id');
        var activateUserPartyName = button.data('party-name');
        if (activateUserPartyName == null) activateUserPartyName = "";

        var modal = $(this)
        modal.find('#activateUserPartyName').text(activateUserPartyName);
        $("#enableUser_partyId").val(activateUserPartyId)
    });
    $('#resetPasswordUserConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var resetPasswordForPartyId = button.data('party-id');
        var resetPasswordForPartyName = button.data('party-name');
        var resetPasswordUserLoginId = button.data('user-login-id');
        if (resetPasswordForPartyName == null) resetPasswordForPartyName = "";

        var modal = $(this)
        modal.find('#resetPasswordForPartyName').text(resetPasswordForPartyName);
        modal.find('#resetPasswordForPartyId').val(resetPasswordForPartyId);
        modal.find('#resetPasswordUserLoginId').val(resetPasswordUserLoginId);
    });
}



function checkPasswordPolicy(textFieldId, errorDivId) {
    var password = $('input[id="' + textFieldId + '"]').val();
    var postData = { password: password };
    var formURL = getAppUrl("validatePasswordPolicy");
    $('#' + errorDivId).html("");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (resp) {
                if (resp._ERROR_MESSAGE_LIST_) {
                    //showErrorToast(resp._ERROR_MESSAGE_LIST_);
                    var errorMsgs = resp._ERROR_MESSAGE_LIST_;
                    var errorHtml = "";
                    for (var i = 0; i < errorMsgs.length; i++) {
                        errorHtml += "<div class=\"small text-danger p-1\"><i class=\"material-icons danger\">error</i> " + errorMsgs[i] + "</div>";
                    }
                    $('#' + errorDivId).html(errorHtml);
                }
            },
            error: function () {
                //TODO: handle error
            }
        });
}

function updatePassword() {
    $('.loginFormSubmitBtn').on('click', function (evt) {
        console.log("loginFormSubmitBtn clicked");

        let formData = {
            "PASSWORD": $('#password').val(),
            "newPassword": $('#newPassword').val(),
            "newPasswordVerify": $('#newPasswordVerify').val()
        }
        console.log(formData);

        $.ajax({
            url: "updatePassword",
            type: "POST",
            data: formData,
            success: function (resp) {
                console.log(resp);
                // window.open('home', '_self');
            },
            error: function (resp) {
                console.log(resp);
                // window.open('changePassword', '_self');
            }
        });
    });
}