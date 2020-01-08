
<!-- Sidebar -->
<div class="bg-dark border-right" id="sidebar-wrapper" >

  <div class="sidebar-logo">
    <a href="<@ofbizUrl>home</@ofbizUrl>">
    <img src="../static/logo/AutoPatt_mini.png" width="36px" align="left"/></a>
    <div class="sidebar-heading"><a href="<@ofbizUrl>home</@ofbizUrl>" class="sidebar-heading"><span class="text"> AutoPatt Admin</span></a></div>

  </div>
  <#--<div class="sidebar-heading"><a href="<@ofbizUrl>home</@ofbizUrl>" class="sidebar-heading">AutoPatt Admin</a></div>-->
  <div class="list-group ">
    <a href="<@ofbizUrl>home</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'HOME'>active</#if>">
      <i class="material-icons icon-indianred <#if currentViewId == 'HOME'>icon-color-active</#if>"> dashboard</i><span class="text"> Admin Home</span></a>

    <a href="<@ofbizUrl>customers</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'CUSTOMERS'>active</#if>">
      <i class="material-icons icon-darkmagenta <#if currentViewId == 'CUSTOMERS'>icon-color-active</#if>">business</i><span class="text"> Customers</span></a>

    <a href="<@ofbizUrl>manage_plans</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_PLANS'>active</#if>">
      <i class="material-icons icon-darkgreen <#if currentViewId == 'MANAGE_PLANS'>icon-color-active</#if>">account_tree</i><span class="text"> Manage Plans</span></a>

    <a href="<@ofbizUrl>manage_users</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_USERS'>active</#if>">
      <i class="material-icons icon-orange <#if currentViewId == 'MANAGE_USERS'>icon-color-active</#if>">people</i><span class="text"> Admin Users</span></a>

    <a href="<@ofbizUrl>reports</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'REPORTS'>active</#if>">
      <i class="material-icons icon-chocolate <#if currentViewId == 'REPORTS'>icon-color-active</#if>">assessment</i><span class="text"> Reports</span></a>

    <a href="<@ofbizUrl>system_settings</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'SYSTEM_SETTINGS'>active</#if>">
      <i class="fa fa-cogs sidebar-icons icon-midnightblue <#if currentViewId == 'SYSTEM_SETTINGS'>icon-color-active</#if>" aria-hidden="true"></i><span class="text"> System</span></a>

  </div>
</div>
<!-- /#sidebar-wrapper -->



