<div class="dashboard-widget">
<div class="container-fluid">
    <div class="row">
        <div class="col-sm-3">
            <div class="card  mb-3 widget-body">
                <div class="card-body card-padding">
                    <h6 class="card-title text-center">Users</h6>

                    <img src="../static/images/icon/1626113.png" class="float-right card-user-icon">
                    <h5 class="card-text" align="center">Active ${users!?size}</h5>
                    <div class="progress">
                        <div class="progress-bar bg-success progress-bar-style" role="progressbar" style="width:${users!?size*45}px">
                        </div>
                    </div>
                </div>
            </div>
        </div>
            <div class="col-sm-3">
                <div class="card  mb-3 widget-body">
                    <div class="card-body card-padding">
                    <h6 class="card-title text-center">Active Subscription</h6>
                    <img src="../static/images/icon/1334767.png" class="float-right card-subscription-icon">
                    <h5 class="card-text" align="center">Advanced Planner</h5>
                </div>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="card  mb-3 widget-body">
                <div class="card-body card-padding">
                    <h6 class="card-title text-center">Products</h6>
                    <img src="../static/images/icon/1474713.png" class="float-right card-product-icon">
                    <h5 class="card-text" align="center">APC</h5>
                    <h6 align="center"></h6>
                </div>
            </div>
        </div>
    </div>

    <#if security.hasEntityPermission("PORTAL", "_VIEW_USERS", session)>
        <table class="table  table-sm table-style">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Status</th>
            <th>Last Logged-in</th>
        </tr>
        </thead>
        <tbody>
        <#if users??>
        <#list users as user>
        <tr>
            <th><img id="table-user-icon" src="../static/images/icon/uicon3.png" alt="user"></th>
            <td>${user.partyName!} </td>
            <td>
            <#if user.userStatus?? && user.userStatus == "ACTIVE">
                <span class="status text-success">&#8226;</span> Active
            <#elseif user.userStatus?? && user.userStatus == "INACTIVE">
                <span class="status text-info">&bull;</span> In-Active
            <#elseif user.userStatus?? && user.userStatus == "LOCKED">
                <span class="status text-warning" style="size:40px">&bull;</span> Locked
            <#else>
                <span class="status text-warning">&bull;</span> Suspended
            </#if>
            </td>
            <td width="30%">${user.lastLoggedInPrettyTime!}</td>
        </tr>
        </#list>
        </#if>
        </tbody>
    </table>
    </#if>

</div>

