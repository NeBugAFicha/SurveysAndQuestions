package com.alexanov.controller;

import com.alexanov.entity.*;
import com.alexanov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


@Controller
@PreAuthorize("hasAuthority('isAdmin')")
public class AdminController {
    private String updateTogglerForAnswers = "";
    private int updateTogglerForAnswersQId = -1;
    @Autowired
    UserService userService;
    @GetMapping("/editSurvey")
    public String editSurvey(Model model){
        if(userService.getCurrentSurvey()!=null) model.addAttribute("currentSurvey",userService.getCurrentSurvey());
        model.addAttribute("qTypes", QType.values());
        model.addAttribute("questAndAnsw",userService.getQuesAndAnsw());
        model.addAttribute("text",QType.TEXT);
        model.addAttribute("updateTogglerForAnswers",updateTogglerForAnswers);
        model.addAttribute("updateTogglerForAnswersQId",updateTogglerForAnswersQId);
        return "editSurvey";
    }


    @GetMapping("/chooseQType/{qType}/{question}")
    public String chooseQType(@PathVariable QType qType, @PathVariable Question question){
        userService.chooseQType(qType,question);
        return "redirect:/editSurvey";
    }
    @GetMapping("/addNewEmptyQuestion")
    public String addNewEmptyQuestion(){
        userService.addNewEmptyQuestion();
        return "redirect:/editSurvey";
    }
    @PostMapping("/addAnswer")
    public String addAnswer(@RequestParam String answer,@RequestParam Question question){
        if(!answer.trim().isEmpty()) userService.addAnswer(answer.trim(),question);
        return "redirect:/editSurvey";
    }
    @GetMapping("/deleteQuestion/{question}")
    public String deleteQuestion(@PathVariable Question question, @AuthenticationPrincipal User user){
        userService.deleteQuestion(question);
        return "redirect:/editSurvey";
    }
    @PostMapping("/deleteAnswer")
    public String deleteAnswer(@RequestParam String answer, @RequestParam Question question){
        userService.deleteAnswer(answer,question);
        return "redirect:/editSurvey";
    }
    @GetMapping("/updateAnswer/{questionId}/{answer}")
    public String getUpdateToggleForAnswers(@PathVariable String answer, @PathVariable int questionId){
        updateTogglerForAnswers = answer;
        updateTogglerForAnswersQId = questionId;
        return "redirect:/editSurvey";
    }
    @PostMapping("/updateAnswer")
    public String updateAnswer(@RequestParam String answer, @RequestParam String oldAnswer, @RequestParam Question question){
        System.out.println(answer +" "+ oldAnswer);
        if(answer==null||answer.trim().isEmpty()) return "redirect:/editSurvey";
        userService.updateAnswer(answer.trim(),oldAnswer,question);
        updateTogglerForAnswers = "";
        updateTogglerForAnswersQId = -1;
        return "redirect:/editSurvey";
    }
    @PostMapping("/addOrUpdateQuestion")
    public String addOrUpdateQuestion(@RequestParam String text, @RequestParam Question question){
        if(!text.trim().isEmpty()) userService.addOrUpdateQuestion(text.trim(),question);
        return "redirect:/editSurvey";
    }
    @PostMapping("/addSurvey")
    public String addSurvey(@RequestParam String title, @RequestParam String description, @RequestParam User user){
        if(title.trim().isEmpty()||description.trim().isEmpty()||userService.getQuesAndAnsw().size()==0) return "redirect:/editSurvey";
        for(Map.Entry<Question, ArrayList<String>> entry: userService.getQuesAndAnsw().entrySet()){
            if(entry.getKey().getText()==null) return "redirect:/editSurvey";
        }
        if(userService.getCurrentSurvey()==null) userService.addSurvey(title,description,user);
        else userService.updateSurvey(title,description,user);
        return "redirect:/";
    }
    @GetMapping("/updateSurvey/{survey}")
    public String updateSurvey(@PathVariable Survey survey){
        userService.setCurrentSurvey(survey);
        return "redirect:/editSurvey";
    }
    @GetMapping("/deleteSurvey/{survey}")
    public String deleteSurvey(@PathVariable Survey survey, @AuthenticationPrincipal User user){
        userService.deleteSurvey(survey, user);
        return "redirect:/";
    }
    @GetMapping("/finishSurvey/{survey}")
    public String finishSurvey(@PathVariable Survey survey){
        survey.setEndTime(Calendar.getInstance());
        userService.finishSurvey(survey);
        return "redirect:/";
    }
}
