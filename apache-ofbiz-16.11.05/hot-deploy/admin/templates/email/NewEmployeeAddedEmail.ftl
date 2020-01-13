<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<p>
<img src="${''+PORTAL_HOST_URL!}/static/logo/AutoPatt_mini.png" width="50px"/>
</p>
<p>
    Hello ${employeePartyName!},
</p>
<p>You have been added to the Organization <b>${organizationName!}</b></p>

<p>
    Please use below details to login to AutoPatt Console.
</p>
<p>
    <a href="${PORTAL_HOST_URL!}">Click here</a> to login to AutoPatt Console<br/>
    Username: ${employeeEmail!}<br/>
    Password: ${employeePassword!}<br/><br/>
    <i>Note: You will be asked to change the password when you login for first time.</i>
</p>

<p>
    Thank You<br/>
    AutoPatt Team
</p>

</body>
</html>