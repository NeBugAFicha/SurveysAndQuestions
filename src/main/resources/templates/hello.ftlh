<!DOCTYPE HTML>
<html>
<head>
    <title>Surveys and Questions</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Hello</h1>
<a href="/main">Helo Page</a>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-primary" type="submit">Выйти</button>
</form>
</body>
</html>