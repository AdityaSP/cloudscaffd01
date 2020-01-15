#! /bin/bash

hostname=$1
dbusername=$2
dbpassword=$3
dbport=$4

tenantId=$5
username=$6
password=$7

echo "Creating DB for tenant: : $tenantId"

mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE USER '$username'@'%' IDENTIFIED BY '$password';"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE USER '$username'@'localhost' IDENTIFIED BY '$password';"

mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE DATABASE ofbiz_$tenantId"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbiz_$tenantId -e "GRANT ALL PRIVILEGES ON ofbiz_$tenantId.* TO '$username'@'%' WITH GRANT OPTION;"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbiz_$tenantId -e "GRANT ALL PRIVILEGES ON ofbiz_$tenantId.* TO '$username'@'localhost' WITH GRANT OPTION;"

echo 'Copying schema and data from Template DB'
mysqldump -h$hostname -P$dbport -u$dbusername -p$dbpassword --quick ofbiz_template | mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword ofbiz_$tenantId

mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE DATABASE ofbizolap_$tenantId"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbizolap_$tenantId -e "GRANT ALL PRIVILEGES ON ofbizolap_$tenantId.* TO '$username'@'%' WITH GRANT OPTION;"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbizolap_$tenantId -e "GRANT ALL PRIVILEGES ON ofbizolap_$tenantId.* TO '$username'@'localhost' WITH GRANT OPTION;"

mysqldump -h$hostname -P$dbport -u$dbusername -p$dbpassword --quick ofbizolap_template | mysql -h$hostname -u$dbusername -p$dbpassword ofbizolap_$tenantId

echo "Done cloning and creating db for tenant"

# USAGE: sh clone-tenant-db.sh localhost ofadmin admin123 3306 t1 t1user T1pwd@123
