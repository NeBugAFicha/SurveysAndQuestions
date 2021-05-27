package com.alexanov.controller;

import com.alexanov.entity.Question;
import com.alexanov.entity.Role;
import com.alexanov.entity.Status;
import com.alexanov.entity.User;
import com.alexanov.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private List<Question> questions = new ArrayList<Question>();
    private List<String> answers = new ArrayList<String>();
    @Autowired
    UserRepo userRepo;
    @GetMapping("/")
    public String neutralPage(Model model){
        return "hello";
    }
    @GetMapping("/login")
    public String loginPage(Model model){
        return "login";
    }
    @GetMapping("/main")
    public String mainPage(Model model){
        model.addAttribute("questions",questions);
        return "main";
    }
    @GetMapping("/registration")
    public String addUser(){
        return "registration";
    }
    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.addAttribute("message", "Пользователь уже существует!");
            return "registration";
        }
        user.setStatus(Status.ACTIVE);
        user.setRole(Role.USER);
        user.setPassword(user.getPassword());
        userRepo.save(user);

        return "redirect:/login";
    }
}
