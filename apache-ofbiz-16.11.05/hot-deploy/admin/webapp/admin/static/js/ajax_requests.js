$(function () {
    // initialize things..
});


$("#new_customer_form").submit(function (event) {
    console.log("Creating new customer....");
    event.preventDefault();

    var postData = $(this).serializeArray();
    var formURL = $(this).attr("action");

    console.log(postData);

    $("#newCustomerFormSubmitButton").attr("disabled", true);
    $('#newCustomerFormCancelButton').addClass('disabled');
    $('#newCustomerForm_Processing').removeClass("d-none");

    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                //data: return data from server
                console.log("request completed... redirecting to.. " + getUrl("customers"))
                window.location.replace(getUrl("customers") + "?createInitiated=Y");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
                $("#newCustomerFormSubmitButton").attr("disabled", false);
                $('#newCustomerFormCancelButton').removeClass('disabled');
                $('#newCustomerForm_Processing').addClass("d-none");
                $('#newCustomerForm_Error').removeClass("d-none");
            }
        });
    //e.unbind(); //unbind. to stop multiple form submit.
});

function listSubscriptions() {
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var status = $('select[id="filterSubscriptionsByStatus"]').val();
    var productId = $('select[id="filterSubscriptionsByProduct"]').val();
    console.log("orgPartyId: " + orgPartyId + "&status=" + status + "&productId=" + productId);
    $("#customer_subscriptions").load(getUrl("filter_subscriptions?orgPartyId=" + orgPartyId + "&status=" + status + "&productId=" + productId))
}

function addNewSubscription() {
    console.log("debug: ");
    var orgPartyId = $('input[name="orgPartyId"]').val();
    var productId = $('select[id="productId"]').val();
    var validFrom = $('input[name="validFrom"]').val();
    var validTo = $('input[name="validTo"]').val();
    console.log("debug end: ");
    var postData = {"orgPartyId":orgPartyId,productId:productId,"validFrom":validFrom,"validTo":validTo};
    var formURL = getUrl("newSubscription");
    $.ajax(
        {
            url: formURL,
            type: "POST",
            data: postData,
            success: function (data, textStatus, jqXHR) {
                //data: return data from server
                console.log("request completed... redirecting to.. " + getUrl("customers"))
                //window.location.replace(getUrl("customers") + "?createInitiated=Y");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}


