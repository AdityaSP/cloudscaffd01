#! /bin/bash

hostname=$1
dbusername=$2
dbpassword=$3
dbport=$4

echo "creating template database on $hostname"


mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE USER 'template_user'@'%' IDENTIFIED BY 'Template@321';"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE USER 'template_user'@'localhost' IDENTIFIED BY 'Template@321';"

mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE DATABASE ofbiz_template"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbiz_template -e "GRANT ALL PRIVILEGES ON ofbiz_template.* TO 'template_user'@'%' WITH GRANT OPTION;"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbiz_template -e "GRANT ALL PRIVILEGES ON ofbiz_template.* TO 'template_user'@'localhost' WITH GRANT OPTION;"

mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -e "CREATE DATABASE ofbizolap_template"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbizolap_template -e "GRANT ALL PRIVILEGES ON ofbizolap_template.* TO 'template_user'@'%' WITH GRANT OPTION;"
mysql -h$hostname -P$dbport -u$dbusername -p$dbpassword -Dofbizolap_template -e "GRANT ALL PRIVILEGES ON ofbizolap_template.* TO 'template_user'@'localhost' WITH GRANT OPTION;"


echo "Done creating template database."
