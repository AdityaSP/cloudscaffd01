

<div class="row ">
    <div class="col-md-6">
        <form class="form-inline">
            <input type="hidden" id="orgPartyId" name="orgPartyId" value="${orgPartyId}"/>
            <div class="form-group mx-sm-3 mb-2 ">
                <label for="filterSubscriptionsByStatus" class="p-2">Status</label>
                <select class="form-control form-control-sm" id="filterSubscriptionsByStatus" name="status">
                    <option value="ALL">All</option>
                    <option value="ACTIVE" <#if status?? && status=="ACTIVE">selected</#if>>Active</option>
                    <option value="INACTIVE" <#if status?? && status=="INACTIVE">selected</#if>>Expired</option>
                </select>
            </div>

            <div class="form-group mx-sm-3 mb-2">
                <label for="filterSubscriptionsByProduct" class="p-2">Plan</label>
                <select class="form-control form-control-sm" id="filterSubscriptionsByProduct" name="productId">
                    <option value="ALL">All</option>
                    <#list plans as plan>
                    <option value="${plan.productId}" <#if productId?? && productId==plan.productId>selected</#if>>${plan.productId} - ${plan.productName}</option>
                    </#list>
                </select>
            </div>
            <a href="javascript:void(0);" class="btn btn-outline-primary btn-sm mb-2"
            onclick="listSubscriptions()">Apply</a>
        </form>
    </div>
    <div class="col-md-6 ">
        <div class="form-group float-right justify-content-end">
                <a href="javascript:void(0);"
                data-target="#createSubscriptionModal"
                class="btn btn-outline-primary btn-sm" title="Add a new subscription" data-toggle="modal"
                data-org-party-id="${orgPartyId!}">
                    <i class="material-icons">&#xE147;</i> <span>New Subscription</span>
                </a>
            </div>
    </div>
</div>


<div>
    <table class="table table-striped table-hover">
        <#if subscriptions?? && subscriptions?size &gt; 0 >
            <caption>Total Subscriptions - <b>${subscriptions!?size}</b></caption>
        </#if>
        <thead class="thead-dark">
        <tr>
            <th>#</th>
            <th>Plan</th>
            <th>Date Created</th>
            <th>Valid From</th>
            <th>Valid Till</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <#if subscriptions?? && subscriptions?size &gt; 0 >
            <#list subscriptions as subscription>
                <tr>
                    <td>${subscription_index + 1}</td>
                    <td class="user-name">
                        <i class="material-icons" style="font-size:1.6em;">cloud_circle</i>
                        ${subscription.productId}
                        <#assign subscribedProduct= (delegator.findOne("Product", {"productId" : subscription.productId}, false))/>
                        <#if subscribedProduct??>
                            - ${subscribedProduct.productName!}
                        </#if>
                    </td>
                    <td>${subscription.createdDate!?date}</td>
                    <td>${subscription.fromDate!?date}</td>
                    <td>
                        <#if subscription.thruDate??>${subscription.thruDate!?date}
                        <#else>
                            NA
                        </#if>
                    </td>
                    <td>
                        <#if subscription.status?? && subscription.status == "ACTIVE">
                            <span class="status text-success">&#8226;</span> <span>Active</span>
                        <#elseif subscription.status?? && subscription.status == "FUTURE">
                            <span class="status text-warning">&#8226;</span> <span>Future</span>
                        <#else>
                            <span class="status text-danger">&bull;</span> <span>Expired</span>
                        </#if>
                    </td>
                    <td width="20%">
                        <#if subscription.status?? && subscription.status == "ACTIVE" >
                            <a href="#"
                               data-target="#revokeSubscriptionModal"
                               class="btn btn-outline-danger" title="Revoke" data-toggle="modal"
                               data-org-party-id="${orgPartyId!}" data-subscription-id="${subscription.id!}">
                                <i class="fa fa-lock" aria-hidden="true"></i>
                            </a>
                        <#elseif subscription.status?? && subscription.status == "FUTURE">
                            <a href="#"
                               class="btn btn-outline-danger" title="Remove" data-toggle="modal"
                               data-target="#deleteSubscriptionConfirmModal"
                               data-org-party-id="${orgPartyId!}" data-subscription-id="${subscription.id!}">
                            <i class="fa fa-trash-o" aria-hidden="true"></i>
                            </a>
                        <#else>
                            <a href="#"
                               data-target="#renewSubscriptionModal"
                               class="btn btn-outline-primary" title="Renew" data-toggle="modal"
                               data-org-party-id="${orgPartyId!}" data-subscription-id="${subscription.id!}">
                                <i class="fa fa-unlock" aria-hidden="true"></i>
                            </a>
                        </#if>
                    </td>
                </tr>
            </#list>
        <#else>
            <tr>
                <td colspan="10" align="center"> No subscriptions found.</td>
            </tr>
        </#if>
        </tbody>
    </table>
</div>

<div class="modal fade" id="createSubscriptionModal" tabindex="-1" role="dialog"
     aria-labelledby="createSubscriptionModal"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="titleModalLabel">New Subscription</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form class="form">
                    <input type="hidden" name="orgPartyId" value="${orgPartyId}"/>
                    <div class="form-group mx-sm-3 mb-2">
                        <label for="newSubscriptionProduct" class="p-2">Choose Plan</label>

                        <select class="form-control form-control-sm" id="productId" name="productId" required>
                            <#list plans as plan>
                            <option value="${plan.productId!}">${plan.productId} - ${plan.productName!}</option>
                            </#list>
                        </select>

                    </div>
                    <div class="form-group mx-sm-3 mb-2 ">
                        <label for="newSubscriptionValidFrom" class="p-2">Valid From</label>
                        <input class="form-control form-control-sm" type="date" id="validFrom" name="validFrom"
                               required>
                    </div>
                    <div class="form-group mx-sm-3 mb-2 ">
                        <label for="newSubscriptionValidTo" class="p-2">Valid Till</label>
                        <input class="form-control form-control-sm" type="date" id="validTo" name="validTo" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="newSubscriptionSubmit" class="btn btnome-secondary" data-dismiss="modal">Cancel
                </button>
                <button class="btn btn-primary" onclick="addNewSubscription()">Add</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="revokeSubscriptionModal" tabindex="-1" role="dialog"
     aria-labelledby="revokeSubscriptionModal"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="titleModalLabel">Revoke Subscription</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div>Choose when the subscription revocation should be effective:</div>
                <br/>
                <form class="form">
                    <div class="form-group mx-sm-3 mb-2 ">
                        <input type="radio" name="revokeNow" id="radio_revoke_immediately" class="form-check-input is-pointer" value="REVOKE_NOW"
                        onclick="$('#revokeValidToDiv').addClass('d-none')"checked>
                        <label class="form-check-label is-pointer" for="radio_revoke_immediately">Revoke Immediately</label>
                    </div>
                    <div class="form-group mx-sm-3 mb-2 ">
                        <input type="radio" name="revokeNow" id="radio_revoke_later" class="form-check-input is-pointer" value="REVOKE_LATER"
                        onclick="$('#revokeValidToDiv').removeClass('d-none')">
                        <label class="form-check-label is-pointer" for="radio_revoke_later">Revoke Later</label>
                    </div>
                    <input type="hidden" name="subscriptionId" id="subscriptionId" value=""/>
                    <div id="revokeValidToDiv" class="form-group mx-sm-3 mb-2 d-none">
                        <label for="newSubscriptionValidTo" class="p-2">Choose Date:</label>
                        <input class="form-control form-control-sm" type="date" id="revokeValidTo" name="revokeValidTo">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="revokeSubscriptionCancelButton" class="btn btn-secondary"
                        data-dismiss="modal">Cancel
                </button>
                <button class="btn btn-warning" onclick="revokeSubscription()">Apply</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="renewSubscriptionModal" tabindex="-1" role="dialog"
     aria-labelledby="renewSubscriptionModal"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="titleModalLabel">Renew Subscription</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div>Choose till when the subscription should be active:</div>
                <br/>
                <form class="form">
                    <div class="form-group mx-sm-3 mb-2 ">
                        <input type="radio" name="renewEffective" id="radio_renew_forever" class="form-check-input is-pointer" value="RENEW_FOREVER"
                               onclick="$('#renewTillDateDiv').addClass('d-none')"checked>
                        <label class="form-check-label is-pointer" for="radio_renew_forever">Renew Forever</label>
                    </div>
                    <div class="form-group mx-sm-3 mb-2 ">
                        <input type="radio" name="renewEffective" id="radio_renew_till" class="form-check-input is-pointer" value="RENEW_TILL"
                               onclick="$('#renewTillDateDiv').removeClass('d-none')">
                        <label class="form-check-label is-pointer" for="radio_renew_till">Renew till</label>
                    </div>
                    <input type="hidden" name="renewSubscriptionId" id="renewSubscriptionId" value=""/>
                    <div id="renewTillDateDiv" class="form-group mx-sm-3 mb-2 d-none">
                        <label for="renewTillDate" class="p-2">Choose Date:</label>
                        <input class="form-control form-control-sm" type="date" id="renewTillDate" name="renewTillDate">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="renewSubscriptionCancelButton" class="btn btn-secondary"
                        data-dismiss="modal">Cancel
                </button>
                <button class="btn btn-warning" onclick="renewSubscription()">Apply</button>
            </div>
        </div>
    </div>
</div>
<form id="delete_subscription_form" action="<@ofbizUrl>ajaxDeleteSubscription</@ofbizUrl>">
    <input type="hidden" id="deleteSubscription_partyId">
</form>
<div class="modal fade" id="deleteSubscriptionConfirmModal" tabindex="-1" role="dialog" aria-labelledby="deleteSubscriptionConfirmModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirm Remove Subscription</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Are you sure you want to remove for this subscription</b>?
                <br/> <br/>
                <div class="alert alert-danger"><i>Note: This action is not reversible.</i></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" onclick="deleteSubscription();">Remove</button>
            </div>
        </div>
    </div>
</div>

