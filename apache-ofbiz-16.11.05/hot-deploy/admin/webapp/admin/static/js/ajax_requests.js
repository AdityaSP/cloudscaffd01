$(function () {
    // initialize things..
});


$("#new_customer_form").submit(function (event) {
    event.preventDefault();

    var postData = $(this).serializeArray();
    var formURL = $(this).attr("action");
    $('#newCustomerForm_Processing').addClass("d-none");

    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (resp) {
                //data: return data from server
                console.log(resp)
                if(resp.success === "Y") {
                    window.location.replace(getUrl("customers") + "?createInitiated=Y&transactionId="+resp.transactionId);
                } else {
                    showErrorToast("Unable to create new customer, Organization Id already exists")
                    $("#newCustomerFormSubmitButton").attr("disabled", false);
                    $('#newCustomerFormCancelButton').removeClass('disabled',false);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
    //e.unbind(); //unbind. to stop multiple form submit.
});

function listSubscriptions() {
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var status = $('select[id="filterSubscriptionsByStatus"]').val();
    var productId = $('select[id="filterSubscriptionsByProduct"]').val();
    $("#customer_subscriptions").load(getUrl("filter_subscriptions?orgPartyId=" + orgPartyId + "&status=" + status + "&productId=" + productId),
    function() {
        initializeOrgSubscriptionModals();
    });
}
function deleteSubscription() {
    var subscriptionId = $("#deleteSubscription_partyId").val()
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var postData = {orgPartyId: orgPartyId, subscriptionId: subscriptionId};
    var formURL = $("#delete_subscription_form").attr("action");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (resp) {
                if(resp.success==="Y") {
                    $('#deleteSubscriptionConfirmModal').modal('hide');
                    showSuccessToast("Subscription Deleted Successfully");
                    setTimeout(function () {
                        listSubscriptions();
                    }, 500);
                } else {
                    showErrorToast("Cannot delete the subscription")
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}


function loadOrgEmployees() {
    var orgPartyId = $('#orgPartyId').val();
    $("#customer_employees").load(getUrl("org_employees?orgPartyId=" + orgPartyId), function () {
        initializeOrgEmployeeModals();
    });
}

function suspendOrgEmployee() {
    var employeePartyId = $("#suspendEmployee_partyId").val()
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var postData = {orgPartyId: orgPartyId, orgEmployeePartyId: employeePartyId};
    var formURL = $("#suspend_org_employee_form").attr("action");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#suspendEmployeeConfirmModal').modal('hide');
                showSuccessToast("User Suspended Successfully");
                setTimeout(function () {
                    loadOrgEmployees();
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}


function activateOrgEmployee() {
    var employeePartyId = $("#enableEmployee_partyId").val()
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var postData = {orgPartyId: orgPartyId, orgEmployeePartyId: employeePartyId};
    var formURL = $("#enable_user_form").attr("action");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#activateEmployeeConfirmModal').modal('hide');
                showSuccessToast("User Activated Successfully");
                setTimeout(function () {
                    loadOrgEmployees();
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}

function deleteOrgEmployee() {
    var employeePartyId = $("#deleteEmployee_partyId").val()
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var postData = {orgPartyId: orgPartyId, orgEmployeePartyId: employeePartyId};
    var formURL = $("#delete_org_employee_form").attr("action");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#deleteEmployeeConfirmModal').modal('hide');
                showSuccessToast("User Deleted Successfully");
                setTimeout(function () {
                    loadOrgEmployees();
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}

function addNewSubscription() {
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var productId = $('select[id="productId"]').val();
    var validFrom = $('input[name="validFrom"]').val();
    var validTo = $('input[name="validTo"]').val();
    var postData = {"orgPartyId": orgPartyId, productId: productId, "validFrom": validFrom, "validTo": validTo};
    var formURL = getUrl("createSubscription");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                if (data._ERROR_MESSAGE_ == null) {
                    $('#createSubscriptionModal').modal('hide');
                    showSuccessToast("Subscription added successfully");
                    setTimeout(function () {
                        listSubscriptions();
                    }, 500);
                } else {
                    showErrorToast(data._ERROR_MESSAGE_ )
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
                showErrorToast("Error: " + errorThrown)
            }
        });
}

function revokeSubscription() {
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var subscriptionId = $('input[id="subscriptionId"]').val();
    var revokeEffective = $('input[name="revokeNow"]:checked').val();
    var validTo = $('input[name="revokeValidTo"]').val();
    var postData = {"orgPartyId": orgPartyId, "subscriptionId":subscriptionId, "revokeEffective": revokeEffective, "validTo": validTo};
    var formURL = getUrl("revokeSubscription");
    var now = new Date();
    var day = ("0" + now.getDate()).slice(-2);
    var month = ("0" + (now.getMonth() + 1)).slice(-2);
    var today = now.getFullYear() +"-"+(month)+"-"+(day);
    if(!(document.getElementById('radio_revoke_immediately').checked)){
        if(validTo < today){
             showErrorToast("Selected Date is before the Current Date");
             return;
        }
    }
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#revokeSubscriptionModal').modal('hide');
                showSuccessToast("Subscription revoked successfully");
                setTimeout(function () {
                    listSubscriptions();
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}


function renewSubscription() {
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var renewSubscriptionId = $('input[id="renewSubscriptionId"]').val();
    var renewEffective = $('input[name="renewEffective"]:checked').val();
    var validTo = $('input[name="renewTillDate"]').val();
    var postData = {"orgPartyId": orgPartyId, "subscriptionId":renewSubscriptionId, "renewEffective": renewEffective, "validTo": validTo};
    var formURL = getUrl("renewSubscription");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#renewSubscriptionModal').modal('hide');
                showSuccessToast("Subscription renewed successfully");
                setTimeout(function () {
                    listSubscriptions();
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}

function saveEmployeeDetails() {
    var userPartId = $("#updateEmployee_partyId").val();
    var userOrgPartId = $("#updateEmployee_orgPartyId").val();
    var firstName = $("#updateEmployee_firstName").val();
    var lastName = $("#updateEmployee_lastName").val();
    var userEmail = $("#updateEmployee_email").val();
    var userRole = $("#updateEmployee_role").val();
    var postData = {partyId: userPartId, orgPartyId:userOrgPartId, firstname: firstName, lastname: lastName, email: userEmail, securityGroupId: userRole};
    var formURL = $("#update-employee-form").attr("action");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#editEmployeeModal').modal('hide');
                showSuccessToast("Employee User Updated Successfully");
                setTimeout(function () {
                    loadOrgEmployees();
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}
function addEmployeeDetails() {
    var userOrgPartId = $("#createEmployee_orgPartyId").val();
    var firstName = $("#createEmployee_firstName").val();
    var lastName = $("#createEmployee_lastName").val();
    var empEmail = $("#createEmployee_email").val();
    var empRole = $("#createEmployee_role").val();
    var empPassword = $("#createEmployee_password").val();
    var postData = {
        orgPartyId: userOrgPartId,
        firstName: firstName,
        lastName: lastName,
        email: empEmail,
        securityGroupId: empRole,
        empPassword: empPassword
    };
    var formURL = getUrl("createEmployee");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function(resp) {
                if(resp.Success === "Y") {
                    $('#createEmployeeModal').modal('hide');
                    showSuccessToast("Employee has been added successfully");
                    setTimeout(function () {
                        loadOrgEmployees();
                    }, 500);
                } else {
                    //$('#createEmployeeModal').modal('show');
                    showErrorToast("Email already exists")
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}

function initResetEmployeePwd() {
  /*  var orgPartyId = $('input[id="resetPasswordOrgPartyId"]').val();
    var userLoginId = $('input[id="resetPasswordUserLoginId"]').val();
    var postData = {"orgPartyId": orgPartyId, "userLoginId":userLoginId};*/
    var orgPartyId = App.unescapeHtmlText($('input[id="resetPasswordOrgPartyId"]').val());
    var userLoginId = App.unescapeHtmlText($('input[id="resetPasswordUserLoginId"]').val());
   var postData = {"orgPartyId": orgPartyId, "userLoginId":userLoginId};
    var formURL = getUrl("initResetEmployeePwd");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                $('#resetPasswordEmployeeConfirmModal').modal('hide');
                showSuccessToast("Reset password initiated successfully, User will receive mail with reset link");
                setTimeout(function () {
                }, 500);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}

function filterUsersForReport() {
    var status = $('select[id="filterUsersByStatus"]').val();
    var tenantId = $('select[id="filterUsersByTenant"]').val();
    $("#users_report").load(getUrl("filterUsersForReport?status=" + status + "&tenantId=" + tenantId),
        function () {
            showSuccessToast("Users loaded successfully");
        });
}

function filterSubscriptionsForReport() {
    var status = $('select[id="filterSubscriptionsReportByStatus"]').val();
    var tenantId = $('select[id="filterSubscriptionReportByTenant"]').val();
    var planId = $('select[id="filterSubscriptionsReportByProduct"]').val();
    $("#subscriptions_report").load(getUrl("filterSubscriptionsForReport?status=" + status + "&tenantId=" + tenantId + "&planId=" + planId),
        function () {
            showSuccessToast("Subscriptions loaded successfully");
        });
}

function checkEmailEmp() {
    var userOrgPartId = $("#createEmployee_orgPartyId").val();
    var email = $("#createEmployee_email").val()
    var postData = {email: email, orgPartyId: userOrgPartId};
    var formURL = getUrl("checkEmailForEmp");
    $("#email_notExists").addClass("d-none");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function(resp) {
                if(resp.EMAIL_EXISTS === "YES") {
                    $("#email_notExists").removeClass("d-none");
                } else {
                    //$("#emailInfo").html("FALSE");
                }
            },
            error: function (EMAIL_EXISTS) {
                //TODO: handle error
            }
        });
}

function checkPasswordPolicy(textFieldId, errorDivId) {
    var password = $('input[id="'+textFieldId+'"]').val();
    var postData = {password: password};
    var formURL = getUrl("validatePasswordPolicy");
    $('#'+errorDivId).html("");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (resp) {
                if(resp._ERROR_MESSAGE_LIST_){
                    //showErrorToast(resp._ERROR_MESSAGE_LIST_);
                    var errorMsgs = resp._ERROR_MESSAGE_LIST_;
                    var errorHtml = "";
                    for(var i=0;i <errorMsgs.length; i++) {
                        errorHtml += "<div class=\"small text-danger p-1\"><i class=\"material-icons danger\">error</i> "+ errorMsgs[i]+"</div>";
                    }
                    $('#'+errorDivId).html(errorHtml);
                }
            },
            error: function () {
                //TODO: handle error
            }
        });
}

function openRevokeValidToDivFn(){
    $('#revokeValidToDiv').removeClass('d-none');
     var now = new Date();
     var day = ("0" + now.getDate()).slice(-2);
     var month = ("0" + (now.getMonth() + 1)).slice(-2);
     var today = now.getFullYear() +"-"+(month)+"-"+(day);
    $('#revokeValidTo').attr('min', today);
}
