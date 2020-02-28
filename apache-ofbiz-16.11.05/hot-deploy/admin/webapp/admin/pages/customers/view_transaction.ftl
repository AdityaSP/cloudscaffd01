
<div class="container-fluid">

    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>Transaction Logs for - ${transaction.tenantId!}</h4>
            </div>
            <div class="col-sm-7">
                <a href="<@ofbizUrl>in-progress</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">keyboard_backspace</i> <span>Back</span></a>
            </div>
        </div>
    </div>

    <#if transaction?? >
        <div class="table-content">
            <div>Organization: ${transaction.tenantId!} - ${transaction.tenantOrgName!}</div>
            <div>Transaction Id: ${transaction.transactionId!}</div>
            <div>Started On: ${transaction.createdStamp?datetime}</div>
            <div>Last modified On: ${transaction.lastUpdatedStamp?datetime}</div>
            <div>Status:
                <#if transaction.status == 'FAILED'>
                <b class="text-danger">${transaction.status!}</b>
                <#elseif transaction.status == 'COMPLETED'>
                    <b class="text-success">${transaction.status!}</b>
                <#else>
                    <b class="text-dark">${transaction.status!}</b>
                </#if>

            </div>
        </div>
        <hr/>
        <h5>Logs</h5>
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <#--<th>Log Id</th>-->
                <th>Transaction<br/> Status</th>
                <th>Started On</th>
                <th>Step</th>
                <th>Details</th>
            </tr>
            </thead>
            <tbody>
            <#if transactionLogs?? && transactionLogs?size &gt; 0 >
                <#list transactionLogs as log>
                    <tr>
                        <td>${log_index + 1}</td>
                        <#--<td>${log.transactionLogId!}</td>-->
                        <td>${log.status!}</td>
                        <td>${log.createdStamp!?datetime}</td>
                        <td>${log.stepName}</td>
                        <td width="50%">
                            ${log.details!}
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

    <#else>
        <div>Invalid transaction id</div>
    </#if>


</div>