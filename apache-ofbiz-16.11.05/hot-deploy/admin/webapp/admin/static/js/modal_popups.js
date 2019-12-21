

$(function() {
    initializeDeleteUserModal();
});


function initializeDeleteUserModal() {
    $('#editEmployeeModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var editPartyId = button.data('party-id') // Extract info from data-* attributes
        var orgPartyId = button.data('org-party-id');
        var editPartyName = button.data('party-name');
        if(editPartyName == null) editPartyName = "";

        var modal = $(this)
        modal.find('#editEmployee_name').text(editPartyName)

        $("#editEmployeeModal_Details").load(getUrl("/org_employee_details?orgPartyId="+orgPartyId+"&partyId="+editPartyId))
    });

    $('#suspendEmployeeConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var suspendingPartyId = button.data('party-id');
        var suspendingPartyName = button.data('party-name');
        if(suspendingPartyName == null) suspendingPartyName = "";

        var modal = $(this);
        console.log("suspendingPartyId " + suspendingPartyId)
        modal.find('#suspendPartyName').text(suspendingPartyName)
        $("#suspendEmployee_partyId").val(suspendingPartyId)
    });

    $('#activateEmployeeConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var activatePartyId = button.data('party-id');
        var activatePartyName = button.data('party-name');
        if(activatePartyName == null) activatePartyName = "";

        var modal = $(this)
        modal.find('#activatePartyName').text(activatePartyName)
    });
    $('#resetPasswordEmployeeConfirmModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var resetPasswordForPartyId = button.data('party-id');
        var resetPasswordForPartyName = button.data('party-name');
        if(resetPasswordForPartyName == null) resetPasswordForPartyName = "";

        var modal = $(this)
        modal.find('#resetPasswordForPartyName').text(resetPasswordForPartyName)
    });
}