<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
<head>
    <title>Surveys and Questions</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<#include "security.ftlh">
<h1>Now its a main page</h1>
<form action="/addSurvey" method="post" id="addSurvey">
    <div ><label> Survey title : <input type="text" name="title" <#if currentSurvey??>value="${currentSurvey.title}"</#if>/> </label></div>
    <div><label> Survey description: <input type="text" name="description" <#if currentSurvey??>value="${currentSurvey.description}"</#if>/> </label></div>
    <!--Добавление нового вопроса-->
    <a href="/addNewEmptyQuestion">Add New Empty Question to the Survey</a><br>
    <!--Перечисление всех вопросов этого опроса-->
    <input type="hidden" name="user" value="${user.id}"/>
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
</form>
<#list questAndAnsw as question,answers>
    <!--Форма каждого вопроса, которую можно заполнить и добавить новый вопрос либо же изменить существующий-->
    <form action="/addOrUpdateQuestion" method="post" id="addQuestion${question.id}">
        Text of question : <input type="text" name="text" <#if question.text??>value="${question.text}"</#if>/>
        <input type="hidden" name="question" value="${question.id}" />
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
    </form></br>
    <div>Type of question:
        <ul>
            <li><a href="/chooseQType/${qTypes[0]}/${question.id}">${qTypes[0]}</a></li>
            <li><a href="/chooseQType/${qTypes[1]}/${question.id}">${qTypes[1]}</a></li>
            <li><a href="/chooseQType/${qTypes[2]}/${question.id}">${qTypes[2]}</a></li>
        </ul>
    </div>
    <#if question.qType!=text>
        <#list answers as answer>
            ${answer}
            <form style="display:inline;" action="/deleteAnswer" method="post" id="${question.id}deleteAnswer${answer}">
                <input type="hidden" name="answer" value="${answer}">
                <input type="hidden" name="question" value="${question.id}" />
                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                <button form="${question.id}deleteAnswer${answer}" type="submit">Delete answer</button>
            </form>
            <a href="/updateAnswer/${question.id}/${answer}">UpdateAnswer</a>
                <form action="/updateAnswer" method="post" id="${question.id}updateAnswer${answer}" <#if answer!=updateTogglerForAnswers || updateTogglerForAnswersQId!=question.id> hidden <#else>style="display: inline;"</#if>>
                    <input type="text" name="answer" value="${answer}"/>
                    <input type="hidden" name="oldAnswer" value="${answer}"/>
                    <input type="hidden" name="question" value="${question.id}" />
                    <input type="hidden" name="_csrf" value="${_csrf.token}" />
                    <button form="${question.id}updateAnswer${answer}" type="submit">update answer</button>
                </form>
            </br>
        </#list>
        <form method="post" action="/addAnswer" id="addAnswer${question.id}" >
                <input type="text" name="answer" placeholder="new answer to add"/>
                <input type="hidden" name="question" value="${question.id}" />
                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                <button form="addAnswer${question.id}" type="submit">Add answer to question</button>
        </form>
    </#if>
    <button form="addQuestion${question.id}" type="submit" >Add/Update question</button>
    <a href="deleteQuestion/${question.id}">Delete question</a>

</#list>
<#if currentSurvey??>
 <button form="addSurvey" type="submit">Update survey</button>
<#else>
 <button form="addSurvey" type="submit">Add survey</button>
</#if>

<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-primary" type="submit">Выйти</button>
</form>
</body>
</html>