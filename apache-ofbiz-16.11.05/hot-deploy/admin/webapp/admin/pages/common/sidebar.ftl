
<!-- Sidebar -->
<div class="bg-dark border-right" id="sidebar-wrapper" >

  <div class="sidebar-logo">
    <a href="<@ofbizUrl>home</@ofbizUrl>">
    <img src="../static/logo/AutoPatt_mini.png" width="36px" align="left"/></a>
    <div class="sidebar-heading"><a href="<@ofbizUrl>home</@ofbizUrl>" class="sidebar-heading"><span class="text"> AutoPatt Support</span></a></div>

  </div>
  <#--<div class="sidebar-heading"><a href="<@ofbizUrl>home</@ofbizUrl>" class="sidebar-heading">AutoPatt Support</a></div>-->
  <div class="list-group ">
    <a href="<@ofbizUrl>home</@ofbizUrl>" title="Home"  class="list-group-item list-group-item-action <#if currentViewId == 'HOME'>active</#if>">
      <i class="material-icons icon-indianred <#if currentViewId == 'HOME'>icon-color-active</#if>"> dashboard</i><span class="text"> Support Home</span></a>

    <a href="<@ofbizUrl>customers</@ofbizUrl>" title="Customers" class="list-group-item list-group-item-action <#if currentViewId == 'CUSTOMERS'>active</#if>">
      <i class="material-icons icon-darkmagenta <#if currentViewId == 'CUSTOMERS'>icon-color-active</#if>">business</i><span class="text"> Client Organisation</span></a>

    <a href="<@ofbizUrl>manage_plans</@ofbizUrl>" title="Manage Plans" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_PLANS'>active</#if>">
      <i class="material-icons icon-darkgreen <#if currentViewId == 'MANAGE_PLANS'>icon-color-active</#if>">account_tree</i><span class="text"> Manage Plans</span></a>

    <a href="<@ofbizUrl>manage_users</@ofbizUrl>" title="Admin Users" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_USERS'>active</#if>">
      <i class="material-icons icon-orange <#if currentViewId == 'MANAGE_USERS'>icon-color-active</#if>">people</i><span class="text">User Management</span></a>

    <a href="<@ofbizUrl>reports</@ofbizUrl>" title="Reports"  class="list-group-item list-group-item-action <#if currentViewId == 'REPORTS'>active</#if>">
      <i class="material-icons icon-chocolate <#if currentViewId == 'REPORTS'>icon-color-active</#if>">assessment</i><span class="text"> Reports</span></a>

    <a href="<@ofbizUrl></@ofbizUrl>" title="Licence_Billing" class="list-group-item list-group-item-action <#if currentViewId == 'Licence_Billing'>active</#if> disabled" >
      <img src="../static/images/icon/license.png" style="width:30px;height:25px;" class="img-icon sidebar-icons <#if currentViewId == 'Licence_Billing'>icon-color-active</#if>">
      <span class="text">License & Billing</span></a>

    <a href="<@ofbizUrl></@ofbizUrl>" title="Help" class="list-group-item list-group-item-action <#if currentViewId == 'Help'>active</#if> disabled" >
      <img src="../static/images/icon/help.png" style="width:30px;height:25px;" class="img-icon sidebar-icons <#if currentViewId == 'Help'>icon-color-active</#if>">
      <span class="text">Help & Support</span></a> 
    <#--<a href="<@ofbizUrl>system_settings</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId == 'SYSTEM_SETTINGS'>active</#if>">
      <i title="System" class="fa fa-cogs sidebar-icons icon-midnightblue <#if currentViewId == 'SYSTEM_SETTINGS'>icon-color-active</#if>" aria-hidden="true"></i><span class="text"> System</span></a>-->

  </div>
</div>
<!-- /#sidebar-wrapper -->



