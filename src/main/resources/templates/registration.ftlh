<!DOCTYPE HTML>
<html>
<head>
    <title>Surveys and Questions</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
Registration!</br>
<#if message??>
    ${message}
</#if>
<form method="post">
    <div ><label> User name : <input type="text" name="username"/> </label></div>
    <div><label> Password: <input type="password" name="password"/> </label></div>
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <div><input type="submit" value="Sign up"/></div>
</form>
</body>
</html>