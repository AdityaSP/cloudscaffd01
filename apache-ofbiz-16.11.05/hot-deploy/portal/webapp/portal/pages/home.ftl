<div class="dashboard-widget">
<div class="container-fluid">
    <div class="row">
        <div class="col-sm-3">
            <div class="card  mb-3 widget-body">
                <div class="card-body card-padding">
                    <h6 class="card-title text-center">Users</h6
                    <img src="../static/images/icon/1626113.png" class="float-right card-user-icon">
                    <h5 class="card-text" align="center">Total: ${users!?size}</h5>
                    <div class="progress">
                        <div class="progress-bar bg-success progress-bar-style" role="progressbar" style="width:${users!?size*45}px">
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="col-sm-3">
            <div class="card  mb-3 widget-body">
                <div class="card-body card-padding" >
                    <h6 class="card-text" align="center">
                    <span class="createdSolutionDesigns">available designs:</span></br>
                    <span class="approvedBasePattern">approved patterns : </span></br>
                    <span class="createdBasePattern">created patterns : </span></br>
                    <span class="approvedSolutionDesign">approved solution design : </span></br>
                    <span class="ProblemStatement">problem statement : </span></br>
                    </h6>
                </div>
            </div>
        </div>


        <div class="col-sm-4">
            <div class="card mb-4 widget-body">
                <div class="card-body card-padding">
                    <h6 class="card-title text-center">Active Subscription</h6>
                    <img src="../static/images/icon/1334767.png" class="float-right card-subscription-icon">
                        <#if activeSubscription??>
                            <#assign subscribedProduct= (delegator.findOne("Product", {"productId" : activeSubscription.productId}, false))/>
                            <#if subscribedProduct??>
                                <h5 class="card-text" align="center">${subscribedProduct.productName!} (${subscribedProduct.productId!}) </h5>
                                <#if activeSubscription.thruDate??>
                                    <small class="p-1 text-muted">valid till ${activeSubscription.thruDate?date} ${activeSubscription.thruDate?time}</small>
                                <#else>
                                    <small class="p-1 text-muted">valid forever</small>
                                </#if>
                                <br/>
                            </#if>
                        <#else>
                            <h6 class="card-text">No Active Subscriptions found</h6>
                        </#if>
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

        <div class="row">
            <#if security.hasEntityPermission("PORTAL", "_VIEW_USERS", session)>
            <div class="col-sm-6 ">
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
            </div></#if>

            <div class="col-sm-6 " >
            <table class="table table-sm table-style">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Count</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Icon</td>
                    <td>approved Patterns</td>
                    <td class="approvedBasePattern"></td>
                </tr>
                 <tr>
                    <td>Icon</td>
                    <td>created solution designs</td>
                    <td class="createdSolutionDesigns"></td>
                 </tr>
                 <tr>
                    <td><i class="fa fa-exclamation-triangle" aria-hidden="true"></i></td>
                    <td>problem statement</td>
                    <td class="ProblemStatement"></td>
                 </tr>
                 <tr>
                     <td>Icon</td>
                     <td>custom patterns</td>
                     <td class="createdBasePattern"></td>
                 </tr>
                 <tr>
                     <td>Icon</td>
                     <td>approved solution designs</td>
                     <td class="approvedSolutionDesign"></td>
                 </tr>
              </tbody>
            </table>
            </div>
        </div>
</div>



<script
  src="https://code.jquery.com/jquery-3.4.1.min.js"
  integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
  crossorigin="anonymous"></script>


<script>
 (function apcDetailInCount(){
 $.ajax({
  url:"getAPCDetailsInCount",
  type:"POST",
  success : function(res){
  console.log(res);

  $(".createdSolutionDesigns").append(res.data.createdSolutionDesignCount);
  $(".approvedBasePattern").append(res.data.approvedBasePatternCount);
  $(".ProblemStatement").append(res.data.ProblemStatementCount);
  $(".createdBasePattern").append(res.data.createdBasePatternCount);
  $(".approvedSolutionDesign").append(res.data.approvedSolutionDesignCount);
  },
  error:function(res){
    console.log(res);
 }
 });
 })();
</script>