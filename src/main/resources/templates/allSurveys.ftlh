<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
<head>
    <title>Surveys and Questions</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB" crossorigin="anonymous">
</head>
<body>
<#include "security.ftlh">
<h1>Hello</h1>
<#if user.role.toString()=='ADMIN'>
  <a href="/editSurvey">ADD NEW SURVEY</a>
</#if>
<div class="card-columns">
    <#list surveys as survey, isCompleted>
        <div class="card my-3">
            Title: ${survey.title}</br>
            Description: ${survey.description}</br>
            Questions:
            <#list survey.getQuestions() as question>
                <div class="m-2" >
                    <#if question.getUser().getRole().toString()=="ADMIN"> ${question.getText()}: ${question.getAnswer()}</#if>
                </div>
            </#list>
            <#if user.role.toString()=='ADMIN'>
                <#if survey.endTime??==false>
                    <a href="/updateSurvey/${survey.id}">Update Survey</a>
                    <a href="/finishSurvey/${survey.id}">Finish Survey</a>
                </#if>
                <a href="/deleteSurvey/${survey.id}">Delete Survey</a>
            </#if>
            <#if survey.endTime??> The survey is finished</#if>
            <#if isCompleted==false && !survey.endTime??>
                    <a href="/completeSurvey/${survey.id}">Complete Survey</a>
            </#if>
        </div>
    </#list>
</div>
<a href="/showAllCompletedSurveys/${user.id}">Show All Completed Surveys</a>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-primary" type="submit">Выйти</button>
</form>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js" integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T" crossorigin="anonymous"></script>
</body>
</html>