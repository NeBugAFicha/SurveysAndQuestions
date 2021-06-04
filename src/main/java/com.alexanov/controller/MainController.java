package com.alexanov.controller;

import com.alexanov.entity.User;
import com.alexanov.repos.SurveyRepo;
import com.alexanov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

}
