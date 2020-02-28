

<div class="container-fluid">

    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>On-boarding Transactions</h4>
            </div>
            <div class="col-sm-7">
                <a href="<@ofbizUrl>customers</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">keyboard_backspace</i> <span>Back</span></a>
                <a href="<@ofbizUrl>in-progress</@ofbizUrl>" class="btn btn-primary" title="Refresh"><i class="material-icons">refresh</i> <span>Refresh</span></a>
            </div>
        </div>
    </div>

    <div class="table-content">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>Organization</th>
                <th>Transaction Id</th>
                <th>Status</th>
                <th>Started On</th>
                <th>Last Updated</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <#if transactions?? && transactions?size &gt; 0 >
                <#list transactions as tnx>
                    <tr>
                        <td>${tnx_index + 1}</td>
                        <td>${tnx.tenantId!}
                            <#if tnx.tenantOrgName??>
                            - ${tnx.tenantOrgName!}
                            </#if>
                        </td>
                        <td>
                            <a href="<@ofbizUrl>view_transaction?transactionId=${tnx.transactionId!}</@ofbizUrl>">
                                ${tnx.transactionId!}
                            </a>
                        </td>
                        <td>${tnx.status!}</td>
                        <td>${tnx.createdStamp!?datetime}</td>
                        <td>${tnx.lastUpdatedStamp!?datetime}</td>
                        <td width="20%">
                            <a href="<@ofbizUrl>view_transaction?transactionId=${tnx.transactionId!}</@ofbizUrl>" class="btn btn-outline-primary" title="View" data-toggle="tooltip"><i class="material-icons">list_alt</i></a>
                        </td>
                    </tr>
                </#list>
            <#else>
                <tr>
                    <td colspan="10">No transactions found.</td>
                </tr>
            </#if>
            </tbody>
        </table>
    </div>


</div>