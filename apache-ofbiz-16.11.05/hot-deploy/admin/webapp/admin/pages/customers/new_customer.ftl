<div class="container-fluid">
    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>New Client</h4>
            </div>
            <div class="col-sm-7">
                <a href="<@ofbizUrl>customers</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">keyboard_backspace</i> <span>Back</span></a>
                <#--<a href="#" class="btn btn-primary"><i class="material-icons">&#xE24D;</i> <span>Export to Excel</span></a>-->
            </div>
        </div>
    </div>

    <br/>
    <div class="table-content">
        <div class="row">
            <div class="col-7">
                <form id="new_customer_form" action="<@ofbizUrl>CreateCustomer</@ofbizUrl>" method="post">

                    <div class="form-group">
                        <label for="organizationName">Organization Name</label>
                        <input type="text" class="form-control" id="organizationName" name="organizationName" placeholder="XYZ Corporation" required="true" >
                    </div>

                    <div class="form-group">
                        <label for="organizationName">Organization Id <span class="small">(auto-generated)</span></label>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text" id="basic-addon1">https://</span>
                            </div>
                            <input type="text" class="form-control" id="organizationId" placeholder="xyz-org" name="tenantId" aria-label=""
                                   value="${newOrgId!}" readonly aria-describedby="basic-addon2"
                                   onkeypress="return restrictSpecialCharacters(event)" onblur="checkIfOrgIdExists()" required="true">
                            <div class="input-group-append">
                                <span class="input-group-text" id="basic-addon2">.autopatt.com</span>
                            </div>
                        </div>
                        <div id="orgId_notAvailable" class="text-danger d-none">&nbsp This organization id has already been taken</div>

                    </div>
                    <div class="card form-group">
                        <div class="card-header">
                            Contact Details
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="contactFirstName">First Name</label>
                                        <input type="text" class="form-control" id="contactFirstName" name="contactFirstName" placeholder="" required="true" >
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="contactLastName">Last Name</label>
                                        <input type="text" class="form-control" id="contactLastName" name="contactLastName" placeholder="" required="true">
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="contactEmail">Email Address</label>
                                        <input type="email" class="form-control" id="contactEmail" name="contactEmail" placeholder="" required="true">
                                        <small id="emailHelp" class="form-text text-muted">This email will act as username to login to console</small>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="contactPassword">Password</label>
                                        <div class="input-container">
                                            <input type="password" class="form-control" id="password" name="contactPassword"
                                                   onblur="checkPasswordPolicy('password','password_policy_error')"
                                                   placeholder="" required="true">
                                            <i class="fa fa-eye p_eye" aria-hidden="true" id="password_eye"></i>
                                        </div>
                                        <div id="password_policy_error"></div>
                                        <small id="emailHelp" class="form-text text-muted">User will be forced to change password on first login</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <input type="checkbox" name="sendNotificationToContact" value="Y" checked data-toggle="toggle" data-on="Yes" data-off="No" data-style="slow">
                        <span>Send Email notification to the organization contact person?</span>
                    </div>

                    <div id="newCustomerForm_Error" class="alert alert-danger d-none" role="alert">
                        <i class="material-icons">check</i> Error Occured: <span id="newCustomerForm_Error_Message"> </span>.
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div id="newCustomerForm_Processing" class="alert alert-info d-none" role="alert">
                        <div class="spinner-border text-primary" role="status" >
                            <span class="sr-only">Loading...</span>
                        </div>
                        <span>Onboarding new customer, please wait...</span>
                    </div>

                    <button type="submit" class="btn btn-primary" id="newCustomerFormSubmitButton"><i class="material-icons">check_box</i> Create Customer</button>
                    <a href="<@ofbizUrl>customers</@ofbizUrl>" class="btn btn-secondary " id="newCustomerFormCancelButton" role="button">
                        <i class="material-icons">cancel</i> <span>Cancel</span></a>

                    <div>
                        <small id="emailHelp" class="form-text text-muted">
                            <i>Note: Setting up a new organization might take few minutes. Please try to login to new organization console after few mins.</i>
                        </small>
                    </div>

                </form>
            </div>
        </div>
        <br/><br/><br/>
    </div>

</div>
