<div class="dashboard-widget" style="padding-top: 15px;">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6">
                <div class="card mb-3" style="height: 19rem;background-color: #fbfbfb;">
                    <div class="card-body card-padding">
                        <h6 class="card-title text-center">Customers
                            <img src="../static/images/icon/customers.png" class="float-right customers-icon"
                                 style="width: 45px; margin-left: 20px;"></h6>
                        <h4 class="text-center">
                            Total
                            ${totalCustomerCount!}
                        </h4>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="card mb-3" style="height: 19rem;background-color: #fbfbfb;">
                    <div class="card-body">
                        <h6 class="card-title text-center">TBD</h6>
                        <div class="table-responsive-sm">
                            to be done...
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="dashboard-widget" style="padding-top: 0px;">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6">
                <div class="card  mb-3 widget-body " style="height: 19rem; background-color: #fbfbfb;">
                    <div class="card-body card-padding">
                        <h6 class="card-title text-center">Plans <img src="../static/images/icon/plan.png"
                                                                      class="float-right "
                                                                      style="width: 45px; margin-left: 20px;">
                        </h6>
                        <#if plans?? && plans?size &gt; 0>
                            <#list plans as plan>
                                <h6 style="padding-top: 15px;">${plan.productName} ${plans!?size}</h6>
                                <div class="progress">
                                    <div class="progress-bar bg-secondary progress-bar-style" role="progressbar"
                                         style="width:${plans!?size*150}px;"></div> <#--// TODO: need to get the accurate number of perticular plans-->
                                </div>
                            </#list>
                        </#if>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="card  mb-3 widget-body" style="background-color: #fbfbfb;height:19rem;">
                    <div class="card-body">
                        <div class="table-responsive-sm">
                            <table class="table  table-sm" style="width:100%;">
                                <thead>
                                <h6 class="card-title text-center">Admin Users</h6>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Status</th>
                                    <th>Last Logged-in</th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list adminUsers as admin>
                                    <tr>
                                        <td><img src="../static/images/icon/admin.png" alt="user"
                                                 style="width:30px; height:auto;"></td>
                                        <td>${admin.fullName!}</td>
                                        <td>
                                            <#if admin.userStatus?? && admin.userStatus == "ACTIVE">
                                                <span class="status text-success">&#8226;</span> Active
                                            <#elseif admin.userStatus?? && admin.userStatus == "INACTIVE">
                                                <span class="status text-info">&bull;</span> In-Active
                                            <#elseif admin.userStatus?? && admin.userStatus == "LOCKED">
                                                <span class="status text-warning">&bull;</span> Locked
                                            <#else>
                                                <span class="status text-warning">&bull;</span> Suspended
                                            </#if>
                                        </td>
                                        <td width="25%">5 mins ago</td>
                                    </tr>
                                </#list>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>