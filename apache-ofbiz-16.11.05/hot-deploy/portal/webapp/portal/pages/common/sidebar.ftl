
<!-- Sidebar -->
<div class="bg-light border-right" id="sidebar-wrapper">
 <!--<div class="sidebar-logo">
     <a href="<@ofbizUrl>home</@ofbizUrl>">
     <img src="../static/logo/AutoPatt_mini.png" class="pl-3 pt-1 pb-0 mb-0" width="36px" align="left"/></a>
     <div class="sidebar-heading pt-2"><a href="<@ofbizUrl>home</@ofbizUrl>" class="sidebar-heading">
     <span class="text-muted">  AutoPatt </span></a></div>
   </div>-->

   <div class="sidebar-logo">
       <a href="<@ofbizUrl>home</@ofbizUrl>">
       <img src="../static/logo/AutoPatt_mini.png" class="pl-3 " width="60px" align="left"/></a>
   </div>
   <div class="sidebar-heading pt-2"><a href="<@ofbizUrl>home</@ofbizUrl>" class="sidebar-heading">
          <span class="text text-muted pl-3"> AutoPatt</span></a>
   </div>

  <div class="list-group list-group-flush">
    <a href="<@ofbizUrl>home</@ofbizUrl>" title="Home" class="list-group-item list-group-item-action <#if currentViewId == 'HOME'>active</#if>">
      <i class="fa fa-home icon-orange sidebar-icons" <#if currentViewId == 'HOME'>icon-color-active</#if>"></i> <span class="text">Home</span>
    </a>

    <a href="<@ofbizUrl>productAPC</@ofbizUrl>" title="APC" class="list-group-item list-group-item-action <#if currentViewId == 'PRODUCT_APC'>active</#if>" >
      <#--  <i class="fa fa-cloud sidebar-icons icon-green <#if currentViewId == 'PRODUCT_APC'>icon-color-active</#if>"></i>   -->
      <img src="../static/images/icon/apc.png" style="width:33px;height:25px;" class="img-icon sidebar-icons <#if currentViewId == 'PRODUCT_APC'>icon-color-active</#if>">
      <span class="text">APC</span></a>

    <a href="<@ofbizUrl>productAPC</@ofbizUrl>" title="EGA" class="list-group-item list-group-item-action <#if currentViewId == 'EGA'></#if> disabled" >
      <img src="../static/images/icon/ega.png" style="width:32px;height:25px;" class="img-icon sidebar-icons <#if currentViewId == 'EGA'>icon-color-active</#if>">
      <span class="text">EGA</span></a>

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
      <img src="../static/images/icon/license.png" style="width:30px;height:25px;" class="img-icon sidebar-icons <#if currentViewId == 'Licence_Billing'>icon-color-active</#if>">
      <span class="text">License & Billing</span></a>

    <a href="<@ofbizUrl></@ofbizUrl>" title="Help" class="list-group-item list-group-item-action <#if currentViewId == 'Help'>active</#if> disabled" >
      <img src="../static/images/icon/help.png" style="width:30px;height:25px;" class="img-icon sidebar-icons <#if currentViewId == 'Help'>icon-color-active</#if>">
      <span class="text">Help & Support</span></a>  
  </div>
</div>
<!-- /#sidebar-wrapper -->

