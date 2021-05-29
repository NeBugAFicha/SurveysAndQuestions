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
    <!--Добавление нового вопроса-->
    <a href="/addNewEmptyQuestion">Add New Empty Question to the Survey</a><br>
    <!--Перечисление всех вопросов этого опроса-->
    <#list questions as question>
        <!--Форма каждого вопроса, которую можно заполнить и добавить новый вопрос либо же изменить существующий-->
        <form action="/addOrUpdateQuestion" method="post">
            <div ><label> Text of question : <input type="text" name="title" <#if question.text??>value="${question.text}"</#if>/> </label></div>
            <!--Выбор типа вопроса-->
            <div>Type of question:
                <ul>
                    <li><a href="/sort/${qTypes[0]}/${question.id}">${qTypes[0]}</a></li>
                    <li><a href="/sort/${qTypes[1]}/${question.id}">${qTypes[1]}</a></li>
                    <li><a href="/sort/${qTypes[2]}/${question.id}">${qTypes[2]}</a></li>
                </ul>
            </div>
                <#if question.qType.toString()!=text>
                    <a href="/addAnswer/${question.id}">Add another answer</a></br>
                        <#list questAndAnsw.get(question) as answer>
                            <form action="/addOrUpdateAnswer" method="post">
                                <input type="text" name="answer" value="${answer}">
                                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                                <button type="submit">Add answer</button>
                            </form></br>
                        </#list>
                </#if>
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <button type="submit">Add/Update question</button>
            <a href="deleteQuestion/${question.id}">Delete question</a>
        </form></br>
    </#list>
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button type="submit">Add survey</button>
</form>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-primary" type="submit">Выйти</button>
</form>
</body>
</html>