/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.autopatt.admin.services;

import com.autopatt.admin.constants.SecurityGroupConstants;
import com.autopatt.admin.utils.UserLoginUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.transaction.TransactionUtil;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.service.*;
import org.codehaus.plexus.util.FastMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;

/**
 * Services for Customer Onboarding
 * Tenant Creation
 * Org Setup
 */
public class CustomerOnboardingServices {

    public static final String module = CustomerOnboardingServices.class.getName();
    private static Properties ONBOARDING_PROPERTIES = UtilProperties.getProperties("onboarding.properties");

    /**
     * Onboard a new Customer.
     * Creates a new tenant DB.
     * Creates Org Party record
     * Creates First User (admin) for tenant
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> onboardNewCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String tenantId = (String) context.get("tenantId");
        String organizationName = (String) context.get("organizationName");

        String contactFirstName = (String) context.get("contactFirstName");
        String contactLastName = (String) context.get("contactLastName");
        String contactEmail = (String) context.get("contactEmail");
        String contactPassword = (String) context.get("contactPassword");
        String sendNotificationToContact = (String) context.get("sendNotificationToContact");

        // TODO: Validation - check for duplicate tenantId

        if(UtilValidate.isEmpty(tenantId)) {
            // Generate tenantId from organizationName
        }

        // 1. Initiate Tenant DB Creation
        String dbHostIp = ONBOARDING_PROPERTIES.getProperty("onboarding.database.mysql.hostname", "127.0.0.1");
        String dbHostPort = ONBOARDING_PROPERTIES.getProperty("onboarding.database.mysql.port", "3306");
        String dbUsername = ONBOARDING_PROPERTIES.getProperty("onboarding.database.mysql.username");
        String dbPassword = ONBOARDING_PROPERTIES.getProperty("onboarding.database.mysql.password");

        String tenantDbPrefix = tenantId;
        // Remove special chars from tenantDatabaseName
        tenantDbPrefix = tenantDbPrefix.replaceAll("[^\\w]","");
        String tenantDbUsername = "user" + tenantDbPrefix;
        String tenantDbPassword = "P@" + RandomStringUtils.random(15, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwyz1234567890@".toCharArray());

        createTenantDatabase(dbHostIp, dbHostPort, dbUsername, dbPassword, tenantDbPrefix, tenantDbUsername, tenantDbPassword);

        // Create Tenant entries
        Debug.logInfo("Creating Tenant and TenantDataSource entries ", module);
        try {
            Map<String,Object> createTenantResp = dispatcher.runSync("createTenantEntries", UtilMisc.<String, Object> toMap("tenantId", tenantId,
                    "organizationName", organizationName,
                    "dbHostIp",dbHostIp,
                    "dbHostPort",dbHostPort,
                    "tenantDbPrefix",tenantDbPrefix,
                    "tenantDbUsername",tenantDbUsername,
                    "tenantDbPassword",tenantDbPassword,
                    "userLogin", userLogin));
            if(ServiceUtil.isSuccess(createTenantResp)) {
                Debug.logError("Unable to create Tenant and TenantDataSource entries Main DB", module);
                Debug.logError("Response: " + createTenantResp, module);
            }
        } catch (GenericServiceException e) {
            Debug.logError("Unable to create Tenant and TenantDataSource entries Main DB", module);
            e.printStackTrace();
        }

        try {
            GenericValue tenant = delegator.findOne("Tenant", UtilMisc.toMap("tenantId", tenantId), false);
            System.out.println(tenant);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        // 2. Create OrgEntry in Main DB (along with status)
        Debug.logInfo("Creating OrgEntry in Main Db................", module);
        String mainDbOrgPartyId = createOrgPartyInMainDB(dispatcher, userLogin, tenantId, organizationName);
        Debug.log("Organization Party Id in Main DB is: " + mainDbOrgPartyId, module);
        if(UtilValidate.isEmpty(mainDbOrgPartyId)) {
            Debug.logError("Unable to create Organization party in Main DB", module);
        }

        // 3. Create Org Entry in TenantDB,
        Debug.logInfo("Creating Org Entry in Tenant DB, for tenant " + tenantId, module);
        String tenantDbOrgPartyId = createOrgPartyInTenantDB(tenantId, organizationName);
        Debug.logInfo("Organization Party Id in Tenant DB is: " + tenantDbOrgPartyId, module);

        // 4. create user login for given contact in TennatDB, and build relationship
        String adminUserLoginPartyId = createAdminUserLoginInTenantDb(tenantId, tenantDbOrgPartyId, contactFirstName, contactLastName, contactEmail, contactPassword );

        // TODO: 5. Send EMail notification if marked YES

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put("tenantId", tenantId);
        return result;
    }

    /** Service to create Tenant and TenantDataSource entries
     *
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> createTenantEntries(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String tenantId = (String) context.get("tenantId");
        String organizationName = (String) context.get("organizationName");
        String dbHostIp = (String) context.get("dbHostIp");
        String dbHostPort = (String) context.get("dbHostPort");
        String tenantDbPrefix = (String) context.get("tenantDbPrefix");
        String tenantDbUsername = (String) context.get("tenantDbUsername");
        String tenantDbPassword = (String) context.get("tenantDbPassword");

        try {
            GenericValue newTenant = delegator.makeValue("Tenant");
            newTenant.setString("tenantId", tenantId);
            newTenant.setString("tenantName", organizationName);
            delegator.create(newTenant);

            //TODO: handle TenantDomainName in future

            GenericValue newTenantDataSourceOfbiz = delegator.makeValue("TenantDataSource");
            newTenantDataSourceOfbiz.setString("tenantId", tenantId);
            newTenantDataSourceOfbiz.setString("entityGroupName", "org.apache.ofbiz");
            newTenantDataSourceOfbiz.setString("jdbcUri", "jdbc:mysql://" + dbHostIp + ":" + dbHostPort + "/ofbiz_" + tenantDbPrefix);
            newTenantDataSourceOfbiz.setString("jdbcUsername", tenantDbUsername);
            newTenantDataSourceOfbiz.setString("jdbcPassword", tenantDbPassword);
            delegator.create(newTenantDataSourceOfbiz);

            GenericValue newTenantDataSourceOfbizOlap = delegator.makeValue("TenantDataSource");
            newTenantDataSourceOfbizOlap.setString("tenantId", tenantId);
            newTenantDataSourceOfbizOlap.setString("entityGroupName", "org.apache.ofbiz.olap");
            newTenantDataSourceOfbizOlap.setString("jdbcUri", "jdbc:mysql://" + dbHostIp + ":" + dbHostPort + "/ofbizolap_" + tenantDbPrefix);
            newTenantDataSourceOfbizOlap.setString("jdbcUsername", tenantDbUsername);
            newTenantDataSourceOfbizOlap.setString("jdbcPassword", tenantDbPassword);
            delegator.create(newTenantDataSourceOfbizOlap);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String createAdminUserLoginInTenantDb(String tenantId, String orgPartyId, String contactFirstName, String contactLastName, String contactEmail, String contactPassword) {
        GenericDelegator tenantDelegator = null;
        LocalDispatcher tenantDispatcher = null;
        String adminUserLoginPartyId = null;

        if (UtilValidate.isNotEmpty(tenantId)) {
            tenantDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default#" + tenantId);
            if (UtilValidate.isEmpty(tenantDelegator)) {
                Debug.logError("Invalid tenantId " + tenantId, module);
                return null;
            }
            tenantDispatcher = new GenericDispatcherFactory().createLocalDispatcher("default#" + tenantId, tenantDelegator);
        }
        if(UtilValidate.isEmpty(tenantDispatcher)) {
            // Unable to load tenant dispatcher
            Debug.logError("Unable to load tenant dispatcher/delegator for tenant " + tenantId, module);
            return null;
        }

        try {
            // Create Party & Person
            Map<String, Object> createPersonResp = tenantDispatcher.runSync("createPerson", UtilMisc.<String, Object> toMap("firstName", contactFirstName,
                    "lastName", contactLastName,
                    "userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator)));
            if(!ServiceUtil.isSuccess(createPersonResp)) {
                Debug.logError("Error creating new user for " + contactEmail + " in tenant " + tenantId, module);
                return null;
            }
            adminUserLoginPartyId = (String) createPersonResp.get("partyId");

            // Create UserLogin
            Map<String,Object> userLoginCtx = UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator));
            userLoginCtx.put("userLoginId", contactEmail);
            userLoginCtx.put("currentPassword", contactPassword);
            userLoginCtx.put("currentPasswordVerify", contactPassword);
            userLoginCtx.put("requirePasswordChange", "Y"); // enforce password change for new user
            userLoginCtx.put("partyId", adminUserLoginPartyId);

            Map<String, Object> createUserLoginResp = tenantDispatcher.runSync("createUserLogin", userLoginCtx);
            if(!ServiceUtil.isSuccess(createUserLoginResp)) {
                Debug.logError("Error creating new UserLogin for " + contactEmail + " in tenant " + tenantId, module);
                return adminUserLoginPartyId;
            }

            // All Org Users should have EMPLOYEE role (so that we can fetch all org users)
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", adminUserLoginPartyId,
                    "roleTypeId", "EMPLOYEE",
                    "userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator)
            );
            Map<String,Object> createPartyRoleResp = tenantDispatcher.runSync("createPartyRole", partyRole);
            if(!ServiceUtil.isSuccess(createPartyRoleResp)) {
                Debug.logError("Error creating new Party Role for " + contactEmail + " in tenant " + tenantId, module);
                return adminUserLoginPartyId;
            }
            // Add partyRelationship with ORG Party (orgPartyId)
            Map<String, Object> partyRelationship = UtilMisc.toMap(
                    "partyIdFrom", orgPartyId,
                    "partyIdTo", adminUserLoginPartyId,
                    "roleTypeIdFrom", "ORGANIZATION_ROLE",
                    "roleTypeIdTo", "EMPLOYEE",
                    "partyRelationshipTypeId", "EMPLOYMENT",
                    "userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator)
            );
            Map<String,Object> createPartyRelationResp = tenantDispatcher.runSync("createPartyRelationship", partyRelationship);
            if(!ServiceUtil.isSuccess(createPartyRelationResp)) {
                Debug.logError("Error creating new Party Relationship between " + orgPartyId + " and " +adminUserLoginPartyId+" in tenant " + tenantId, module);
                return adminUserLoginPartyId;
            }

            // Assign SecurityGroup to user
            GenericValue userLoginSecurityGroup = tenantDelegator.makeValue("UserLoginSecurityGroup",
                    UtilMisc.toMap("userLoginId", contactEmail,
                            "groupId", SecurityGroupConstants.AP_FULLADMIN,
                            "fromDate", UtilDateTime.nowTimestamp()));
            try {
                userLoginSecurityGroup.create();
            } catch (GenericEntityException e) {
                Debug.logError("Error creating new Party Role for " + contactEmail + " in tenant " + tenantId, module);
                return adminUserLoginPartyId;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return null;
        }
        return adminUserLoginPartyId;
    }

    /** Create an PartyGroup entry for the customer organization in Tenant DB
     *
     * @param tenantId
     * @param organizationName
     * @return
     */
    private static String createOrgPartyInTenantDB(String tenantId, String organizationName) {
        String tenantOrgPartyId = null;
        GenericDelegator tenantDelegator = null;
        LocalDispatcher tenantDispatcher = null;

        if (UtilValidate.isNotEmpty(tenantId)) {
            tenantDelegator = (GenericDelegator) DelegatorFactory.getDelegator("default#" + tenantId);
            if (UtilValidate.isEmpty(tenantDelegator)) {
                Debug.logError("Invalid tenantId " + tenantId, module);
                return null;
            }
            tenantDispatcher = new GenericDispatcherFactory().createLocalDispatcher("default#" + tenantId, tenantDelegator);
        }
        if(UtilValidate.isEmpty(tenantDispatcher)) {
            // Unable to load tenant dispatcher
            Debug.logError("Unable to load tenant dispatcher/delegator for tenant " + tenantId, module);
            return null;
        }

        Map createPartyGroupCtx = UtilMisc.toMap(
                "userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator),
                "groupName", organizationName);
        Map<String, Object> createPartyGroupResponse = null;
        try {
            createPartyGroupResponse = tenantDispatcher.runSync("createPartyGroup", createPartyGroupCtx);
            Debug.logInfo("createPartyGroupResponse is: " + createPartyGroupResponse, module);
            tenantOrgPartyId = (String) createPartyGroupResponse.get("partyId");

        } catch (GenericServiceException e) {
            Debug.logError("Error creating an Org party in tenant DB for for tenant " + tenantId, module);
            e.printStackTrace();
        }

        Map<String, Object> createPartyGroupRoleCtx = UtilMisc.toMap("partyId", tenantOrgPartyId,
                "roleTypeId", "ORGANIZATION_ROLE",
                "userLogin", UserLoginUtils.getSystemUserLogin(tenantDelegator));
        try {
            Map createPartyGroupRoleResponse = tenantDispatcher.runSync("createPartyRole", createPartyGroupRoleCtx);
        } catch (GenericServiceException e) {
            Debug.logError("Error creating an Org party role in tenant DB for for tenant "
                    + tenantId + " and org party id: "  + tenantOrgPartyId, module);
            e.printStackTrace();
        }

        // Set Tenant Org Party Id in SystemProperty for easy lookup
        String organizationPartyKey = UtilProperties.getPropertyValue("admin.properties","customer.organization.party.key", "ORGANIZATION_PARTY_ID");
        GenericValue systemPropertyGv = tenantDelegator.makeValue("SystemProperty",
                UtilMisc.toMap("systemResourceId", "general",
                        "systemPropertyId", organizationPartyKey,
                        "systemPropertyValue", tenantOrgPartyId, "description", null));
        try {
            tenantDelegator.createOrStore(systemPropertyGv);
        } catch (GenericEntityException e) {
            Debug.logError("Error create a SystemProperty for tenants key: " + organizationPartyKey, module);
            e.printStackTrace();
        }
        //EntityUtilProperties.setPropertyValue(tenantDelegator, "general", organizationPartyKey, tenantOrgPartyId);

        return tenantOrgPartyId;
    }

    /**
     * Create an PartyGroup entry for the customer organization
     * And associate with tenantId - for easy lookup
     * @param dispatcher
     * @param userLogin
     * @param tenantId
     * @param organizationName
     * @return
     */
    private static String createOrgPartyInMainDB(LocalDispatcher dispatcher, GenericValue userLogin, String tenantId, String organizationName) {
        String orgPartyId = null;
        Delegator delegator = dispatcher.getDelegator();

        try {
            Map<String,Object> createPartyGroupCtx = UtilMisc.toMap(
                    "userLogin", userLogin,
                    "groupName", organizationName
                    );
            Map<String, Object> createPartyGroupResponse = dispatcher.runSync("createPartyGroup", createPartyGroupCtx);
            Debug.logInfo("createPartyGroupResponse is: " + createPartyGroupResponse, module);
            orgPartyId = (String) createPartyGroupResponse.get("partyId");

            if(UtilValidate.isEmpty(orgPartyId)) {
                // Unable to create;
                return null;
            }

            // Create AP_CUSTOMER Role to this org party
            Map<String, Object> createPartyGroupRoleCtx = UtilMisc.toMap("partyId", orgPartyId,
                    "roleTypeId", "AP_CUSTOMER",
                    "userLogin", userLogin);
            Map createPartyGroupRoleResponse = dispatcher.runSync("createPartyRole", createPartyGroupRoleCtx);


            //Add Entry in Main DB: TenantOrgParty (tenantId - orgPartyId) for easy lookup
            GenericValue tenantOrgParty = delegator.makeValue("TenantOrgParty");
            tenantOrgParty.setString("orgPartyId", orgPartyId);
            tenantOrgParty.setString("tenantId", tenantId);

            delegator.create(tenantOrgParty);
        } catch (GenericEntityException | GenericServiceException e) {
            e.printStackTrace();
        }
        return orgPartyId;
    }


    private static void createTenantDatabase(String dbHostname, String dbHostPort, String dbUsername, String dbPassword,
                                      String tenantId, String tenantDbUsername, String tenantDbPassword) {
        String cloneScriptFile = getCloneScriptFilePath();
        Debug.logInfo("Cloning Database from template for new tenant: " + tenantId, module);
        Debug.logInfo("Running Script: " + cloneScriptFile, module);
        try {
            Debug.logInfo(cloneScriptFile + " " + dbHostname + " " + dbHostPort + " "
                    + dbUsername + " *******"
                    + " " + tenantId + " " + tenantDbUsername + " ******" , module);

            ProcessBuilder pb = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                pb = new ProcessBuilder(cloneScriptFile, dbHostname, dbUsername, dbPassword, dbHostPort,
                        tenantId, tenantDbUsername, tenantDbPassword);
            } else {
                pb = new ProcessBuilder("sh", cloneScriptFile, dbHostname, dbUsername, dbPassword, dbHostPort,
                        tenantId, tenantDbUsername, tenantDbPassword);
            }
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                Debug.logInfo(">>>" + s, module);
            }
            p.waitFor();
            Debug.logInfo("Tenant Database creation completed for tenant: " + tenantId, module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return the script filename based on OS
    private static String getCloneScriptFilePath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "tools/scripts/windows/clone-tenant-db.cmd";
        } else {
            return "tools/scripts/linux/clone-tenant-db.sh";
        }
    }




}
