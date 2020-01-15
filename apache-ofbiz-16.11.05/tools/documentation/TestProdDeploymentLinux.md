# Test/Prod Deployment on Linux


### Add a new user "ofbiz"

Refer: https://levelup.gitconnected.com/add-new-users-to-amazon-ec2-ubuntu-instance-da075949ca4b

Create your ssh-key and add your public key to the file /home/ofbiz/.ssh/authorized_keys
And use the private key to login to server as user "ofbiz"

---

### Install Mysql client

sudo apt-get install mysql-client

Test Connection to db:
λ mysql -h3.9.134.27 -uofbizdbuser -P8681 -p


##### MySQL set sql_mode (on DB server)
Open /etc/mysql/mysql.conf.d/mysqld.cnf

Add below line at end of file:
~~~
set global sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
~~~
Restart MySQL server

----

### Code Setup:

Clone code repo

Change gradlew file execution mode
~~~
chmod 700 gradlew 
~~~

#### Build
~~~
./gradlew cleanAll build
~~~

#### Configuration
Make a copy of the below config files from *-test file

* entityengine.xml
* onboarding.properties

----
### Template Setup

~~~
sh create-template-db 3.9.134.27 ofadmin oAdmin@123 8681
~~~
~~~
./gradlew createTenant -PtenantId=template -PtenantReaders=seed,seed-initial -PdbPlatform=M -PdbIp=3.9.134.27:8681 -PdbUser=template_user -PdbPassword=Template@321
~~~

----

Start server (as background process):
~~~
./gradlew ofbizBackground
~~~

Stop server:
~~~
./gradlew terminateOfbiz
~~~

---

Test Server URLs

https://3.8.1.169:8443/admin


https://3.8.1.169:8443/portal

