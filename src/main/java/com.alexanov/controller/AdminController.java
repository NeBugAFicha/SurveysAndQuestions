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


@Controller
@PreAuthorize("hasAuthority('isAdmin')")
public class AdminController {
    private String updateTogglerForAnswers = "";
    private int updateTogglerForAnswersQId = -1;
    @Autowired
    UserService userService;
    @GetMapping("/main")
    public String mainPage(Model model){
        model.addAttribute("qTypes", QType.values());
        model.addAttribute("questAndAnsw",userService.getQuesAndAnsw());
        model.addAttribute("text",QType.TEXT);
        model.addAttribute("updateTogglerForAnswers",updateTogglerForAnswers);
        model.addAttribute("updateTogglerForAnswersQId",updateTogglerForAnswersQId);
        return "main";
    }


    @GetMapping("/chooseQType/{qType}/{question}")
    public String chooseQType(@PathVariable QType qType, @PathVariable Question question){
        userService.chooseQType(qType,question);
        return "redirect:/main";
    }
    @GetMapping("/addNewEmptyQuestion")
    public String addNewEmptyQuestion(){
        userService.addNewEmptyQuestion();
        return "redirect:/main";
    }
    @PostMapping("/addAnswer")
    public String addAnswer(@RequestParam String answer,@RequestParam Question question){
        userService.addAnswer(answer,question);
        return "redirect:/main";
    }
    @GetMapping("/deleteQuestion/{question}")
    public String deleteQuestion(@PathVariable Question question){
        userService.deleteQuestion(question);
        return "redirect:/main";
    }
    @PostMapping("/deleteAnswer")
    public String deleteAnswer(@RequestParam String answer, @RequestParam int questionId){
        userService.deleteAnswer(answer,questionId);
        return "redirect:/main";
    }
    @GetMapping("/updateAnswer/{questionId}/{answer}")
    public String getUpdateToggleForAnswers(@PathVariable String answer, @PathVariable int questionId){
        updateTogglerForAnswers = answer;
        updateTogglerForAnswersQId = questionId;
        return "redirect:/main";
    }
    @PostMapping("/updateAnswer")
    public String updateAnswer(@RequestParam String answer, @RequestParam String oldAnswer, @RequestParam Question question){
        updateTogglerForAnswers = "";
        updateTogglerForAnswersQId = -1;
        userService.updateAnswer(answer,oldAnswer,question);
        return "redirect:/main";
    }
    @PostMapping("/addOrUpdateQuestion")
    public String addOrUpdateAnswer(@RequestParam String text, @RequestParam Question question){
        userService.addOrUpdateAnswer(text,question);
        return "redirect:/main";
    }
    @PostMapping("/addSurvey")
    public String addSurvey(@RequestParam String title, @RequestParam String description, @AuthenticationPrincipal User user){
        userService.addSurvey(title,description,user);
        return "redirect:/";
    }
}