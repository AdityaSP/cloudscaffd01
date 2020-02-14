
<!-- Sidebar -->
<div class="bg-light border-right" id="sidebar-wrapper">
  <div class="sidebar-heading">
    <a href="<@ofbizUrl>home</@ofbizUrl>" ><img src="../static/logo/AutoPatt_mini.png" width="30px" align="left"/><span class="text">&nbsp AutoPatt Console</span></a>
  </div>
  <div class="list-group">
    <a href="<@ofbizUrl>home</@ofbizUrl>" title="Dashboard" class="list-group-item list-group-item-action <#if currentViewId == 'HOME'>active</#if>">
      <i class="fa fa-dashboard sidebar-icons icon-indianred <#if currentViewId == 'HOME'>icon-color-active</#if>"></i> <span class="text">Dashboard</span></a>

    <a href="<@ofbizUrl>productAPC</@ofbizUrl>" title="APC" class="list-group-item list-group-item-action <#if currentViewId == 'PRODUCT_APC'>active</#if>" >
      <i class="fa fa-cloud sidebar-icons icon-green <#if currentViewId == 'PRODUCT_APC'>icon-color-active</#if>"></i> <span class="text">APC</span></a>

    <#if security.hasEntityPermission("PORTAL", "_VIEW_USERS", session)>
      <a href="<@ofbizUrl>manage_users</@ofbizUrl>" title="Users" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_USERS'>active</#if>" >
        <i class="fa fa-group icon-orange sidebar-icons <#if currentViewId == 'MANAGE_USERS'>icon-color-active</#if>"></i> <span class="text">Users</span>
      </a>
    </#if>

    <#if security.hasEntityPermission("PORTAL", "_ADMIN", session)>
      <a href="<@ofbizUrl>settings</@ofbizUrl>" title="Settings" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_SETTINGS'>active</#if>" >
        <i class="fa fa-cog sidebar-icons icon-midnightblue <#if currentViewId == 'MANAGE_SETTINGS'>icon-color-active</#if>"></i> <span class="text">Settings</span>
      </a>
    </#if>

    <a href="<@ofbizUrl></@ofbizUrl>" title="Billing" class="list-group-item list-group-item-action <#if currentViewId == 'Billing'>active</#if> disabled" >
      <i class="fa fa-id-card-o sidebar-icons <#if currentViewId == 'Billing'>icon-color-active</#if>"></i> <span class="text">Billing</span></a>

    <a href="<@ofbizUrl></@ofbizUrl>" title="License" class="list-group-item list-group-item-action <#if currentViewId == 'License'>active</#if> disabled" >
      <i class="fa fa-file-text-o sidebar-icons <#if currentViewId == 'License'>icon-color-active</#if>"></i> <span class="text">License</span></a>
  
  </div>
</div>
<!-- /#sidebar-wrapper -->

