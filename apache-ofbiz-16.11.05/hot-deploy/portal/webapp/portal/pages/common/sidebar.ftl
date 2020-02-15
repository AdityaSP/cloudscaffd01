
<!-- Sidebar -->
<div class="bg-light border-right" id="sidebar-wrapper">
  <div class="list-group-flush">
    <a href="<@ofbizUrl>home</@ofbizUrl>" title="Users" class="p-3 pl-4 list-group-item list-group-item-action" >
      <i class="fa fa-home icon-orange sidebar-icons"></i> <span class="text">Home</span>
    </a>
  </div>

  <div class="list-group list-group-flush">
    <a href="<@ofbizUrl>home</@ofbizUrl>" title="Dashboard" class="list-group-item list-group-item-action <#if currentViewId == 'HOME'>active</#if>">
      <i class="fa fa-dashboard sidebar-icons icon-indianred <#if currentViewId == 'HOME'>icon-color-active</#if>"></i> <span class="text">Dashboard</span>
    </a>

    <a href="<@ofbizUrl>productAPC</@ofbizUrl>" title="APC" class="list-group-item list-group-item-action <#if currentViewId == 'PRODUCT_APC'>active</#if>" >
      <i class="fa fa-cloud sidebar-icons icon-green <#if currentViewId == 'PRODUCT_APC'>icon-color-active</#if>"></i> <span class="text">APC</span></a>

    <#if security.hasEntityPermission("PORTAL", "_VIEW_USERS", session)>
      <a href="<@ofbizUrl>manage_users</@ofbizUrl>" title="Users" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_USERS'>active</#if>" >
        <i class="fa fa-group icon-chocolate sidebar-icons <#if currentViewId == 'MANAGE_USERS'>icon-color-active</#if>"></i> <span class="text">IAM</span>
      </a>
    </#if>

    <#--<#if security.hasEntityPermission("PORTAL", "_ADMIN", session)>
      <a href="<@ofbizUrl>settings</@ofbizUrl>" title="Settings" class="list-group-item list-group-item-action <#if currentViewId == 'MANAGE_SETTINGS'>active</#if>" >
        <i class="fa fa-cog sidebar-icons icon-midnightblue <#if currentViewId == 'MANAGE_SETTINGS'>icon-color-active</#if>"></i> <span class="text">Settings</span>
      </a>
    </#if>-->

    <a href="<@ofbizUrl></@ofbizUrl>" title="Licence_Billing" class="list-group-item list-group-item-action <#if currentViewId == 'Licence_Billing'>active</#if> disabled" >
      <i class="fa fa-id-card-o sidebar-icons <#if currentViewId == 'Licence_Billing'>icon-color-active</#if>"></i> <span class="text">License & Billing</span></a>

    <a href="<@ofbizUrl></@ofbizUrl>" title="Help" class="list-group-item list-group-item-action <#if currentViewId == 'Help'>active</#if> disabled" >
      <i class="fa fa-question-circle sidebar-icons <#if currentViewId == 'Help'>icon-color-active</#if>"></i> <span class="text">Help & Support</span></a>  
  </div>
</div>
<!-- /#sidebar-wrapper -->

