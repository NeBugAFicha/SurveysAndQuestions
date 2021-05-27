<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>Surveys and Questions</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Now its a main page</h1>
<form action="/addSurvey" method="post">
    <div ><label> Survey title : <input type="text" name="title"/> </label></div>
    <div><label> Survey description: <input type="title" name="description"/> </label></div>
    <a href="/addNewEmptyQuestion">Add New Empty Question to the Survey</a><br>
    <#list questions as question>
        <form action="/addOrUpdateQuestion" method="post">
            <div ><label> Text of question : <input type="text" name="title" <#if question.text??>value="${question.text}"</#if>/> </label></div>
            <div>Type of question:
                <form method="get" action="/sort">
                    <select name="qType">
                        <#list qTypes as qType>
                            <option value=${qType} <#if selectedQType??&&selectedQType=qType>selected</#if>>${qType}</option>
                        </#list>
                    </select>
                    <button type="submit">Выбрать тип</button>
                </form>
            </div>
            <#if choosedQType==1>
                <input type="text" name="answer">
            <#elseif choosedQType==2>
                <a href="/addAnswer/${question.id}">Add another answer</a>
                <#list Arrays.asList(question.answers.split("\n")) as answer>
                    <input type="text" value="${answer}">
                </#list>
            </#if>
        </form>
    </#list>
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <div><input type="submit" value="add survey"/></div>
</form>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-primary" type="submit">Выйти</button>
</form>
</body>
</html>