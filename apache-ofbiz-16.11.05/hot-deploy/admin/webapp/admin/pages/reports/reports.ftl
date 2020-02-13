<div class="container-fluid">

    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>Reports</h4>
            </div>
            <div class="col-sm-7">
                <#--<a href="<@ofbizUrl>customers</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">keyboard_backspace</i> <span>Back</span></a>-->
                <#--<a href="#" class="btn btn-primary"><i class="material-icons">&#xE24D;</i> <span>Export to Excel</span></a>-->
            </div>
        </div>
    </div>


    <div class="table-content">
        <ul class="nav nav-tabs" id="reportsTab" role="tablist">
            <#--<li class="nav-item">
                <a class="nav-link active" id="users-report-tab" data-toggle="tab" href="#users_report" role="tab" aria-controls="users_report" aria-selected="true">Users Report</a>
            </li>-->
            <li class="nav-item">
                <a class="nav-link" id="subscriptions-report-tab" data-toggle="tab"
                   href="#subscriptions_report" role="tab" aria-controls="subscriptions_report" aria-selected="false">Subscriptions Report</a>
            </li>
        </ul>

        <div class="tab-content p-4" id="reportsContents">
            <div class="tab-pane fade show active" id="users_report" role="tabpanel" aria-labelledby="users_report-tab">
                ${screens.render("component://admin/widget/AdminReportsScreens.xml#users_report")}
            </div>
            <div class="tab-pane fade" id="subscriptions_report" role="tabpanel" aria-labelledby="subscriptions_report-tab">
                ${screens.render("component://admin/widget/AdminReportsScreens.xml#subscriptions_report")}
            </div>
        </div>

    </div>

</div>
