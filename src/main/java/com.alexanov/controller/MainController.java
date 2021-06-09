package com.alexanov.controller;

import com.alexanov.entity.QType;
import com.alexanov.entity.Question;
import com.alexanov.entity.Survey;
import com.alexanov.entity.User;
import com.alexanov.repos.SurveyRepo;
import com.alexanov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class MainController {

    @Autowired
    SurveyRepo surveyRepo;
    @Autowired
    UserService userService;
    @GetMapping
    public String neutralPage(Model model){
        model.addAttribute("surveys", surveyRepo.findAll());
        return "allSurveys";
    }
    @GetMapping("/registration")
    public String addUser(){
        return "registration";
    }
    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        if (!userService.addUser(user)) {
            model.addAttribute("message", "User already exists!");
            return "registration";
        }
        return "redirect:/login";
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
        userService.answerToQues(form,question,user);
        if(userService.getQuesAndAnsw().isEmpty()) {
            userService.getQuestAndAnswUsr().clear();
            return "redirect:/";
        }
        return "redirect:/completeSurvey/"+question.getSurvey().getId();
    }
}
