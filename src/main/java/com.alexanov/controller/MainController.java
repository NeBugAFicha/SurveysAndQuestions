package com.alexanov.controller;

import com.alexanov.entity.*;
import com.alexanov.repos.QuestionRepo;
import com.alexanov.repos.SurveyRepo;
import com.alexanov.repos.UserRepo;
import com.alexanov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.attribute.SupportedValuesAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    @Autowired
    SurveyRepo surveyRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    QuestionRepo questionRepo;
    List<Survey> completedSurveys;
    @Autowired
    UserService userService;
    @GetMapping
    public String neutralPage(Model model, @AuthenticationPrincipal User user){
        List<Survey> allSurveys = surveyRepo.findAll();
        Map<Survey, Boolean> completedSurveysMap = new HashMap<Survey, Boolean>();
        completedSurveys = questionRepo.findQuestionByUser(user).stream().map(question -> question.getSurvey()).collect(Collectors.toList());
        allSurveys.stream().forEach(survey -> {
            if(completedSurveys.contains(survey)) completedSurveysMap.put(survey,true);
            else completedSurveysMap.put(survey,false);
        });
        model.addAttribute("surveys", completedSurveysMap);
        return "allSurveys";
    }

    @GetMapping("/completeSurvey/{survey}")
    public String completeSurvey(@PathVariable Survey survey, Model model){
        if(userService.getQuesAndAnsw().isEmpty()&&userService.getQuestAndAnswUsr().isEmpty()) userService.setCurrentSurvey(survey);
        model.addAttribute("questAndAnswUsr",userService.getQuestAndAnswUsr());
        model.addAttribute("currentSurvey",userService.getCurrentSurvey());
        model.addAttribute("questAndAnsw",userService.getQuesAndAnsw());
        return "completeSurvey";
    }
    @PostMapping("/answerToQues")
    public String answerToQues(@RequestParam Map<String, String> form, @RequestParam Question question, @RequestParam User user) {
        Survey survey = question.getSurvey();
        userService.answerToQues(form,question,user);
        if(userService.getQuesAndAnsw().isEmpty()) {
            completedSurveys.add(survey);
            userService.getQuestAndAnswUsr().clear();
            return "redirect:/";
        }
        return "redirect:/completeSurvey/"+survey.getId();
    }
    @GetMapping("/showAllCompletedSurveys/{userId}")
    public String showAllCompletedSurveys(@PathVariable int userId, Model model){
        Map<Survey, List<Question>> completedSurveysMap2 = new HashMap<Survey, List<Question>>();
        User user = userRepo.findById(userId).get();
        completedSurveys = questionRepo.findQuestionByUser(user).stream().map(question -> question.getSurvey()).collect(Collectors.toList());
        completedSurveys.stream().forEach(survey -> completedSurveysMap2.put(survey, questionRepo.findAllBySurveyAndUser(survey,user)));
        model.addAttribute("completedSurveysMap",completedSurveysMap2);
        return "completedSurveys";
    }

}
