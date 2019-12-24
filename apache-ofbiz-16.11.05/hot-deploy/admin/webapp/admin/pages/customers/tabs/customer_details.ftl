

<form action="<@ofbizUrl>UpdateCustomerDetails</@ofbizUrl>" method="post">
    <input type="hidden" name="orgPartyId" value="${orgPartyId!}"/>
    <div class="col-md-9 my-3">
        <div class="form-group row required">
            <label for="fnamacc" class="col-sm-2 col-form-label">Organization Name <span class="mandatory">*</span></label>
            <div class="col-sm-8">
                <input type="text" class="form-control" value="${organizationName!}" name="organizationName" required>
            </div>
        </div>

        <div class="form-group row">
            <div class="col-sm-2">&nbsp;</div>
            <div class="col-sm-10">
                <button type="submit" class="btn btn-primary ">Update</button>
            </div>
        </div>
    </div>
</form>


