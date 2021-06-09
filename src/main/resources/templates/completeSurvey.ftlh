<!DOCTYPE HTML>
<html>
<head>
    <title>Surveys and Questions</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<#include "security.ftlh">
<h1>Complete Survey</h1>
${currentSurvey.getTitle()}</br>
${currentSurvey.getDescription()}</br>
<#list questAndAnswUsr as question>
    ${question.text}: ${question.answer}
</#list>
<#list questAndAnsw as question, answers>
    <form action="/answerToQues" method="post" id="${question.id}answerToQues">
        ${question.text}:
        <input <#if question.qType.toString()=="TEXT">type="text"<#else>type="hidden"</#if> name="text" placeholder="Your answer">
        <div <#if question.qType.toString()!="SINGLEANSWER">hidden</#if>>
            <#list answers as answer>
                <input type="radio" name="singleAnswer" value="${answer}">${answer}</input>
            </#list>
        </div>
        <div <#if question.qType.toString()!="MULTIANSWERS">hidden</#if>>
            <#list answers as answer>
                <input type="radio" name="${answer}">${answer}</input>
            </#list>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <input type="hidden" name="question" value="${question.id}" />
        <input type="hidden" name="user" value="${user.id}" />
        <button form="${question.id}answerToQues" type="submit" >Answer to Question</button>
    </form>
</#list>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-primary" type="submit">Выйти</button>
</form>
</body>
</html>