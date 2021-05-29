package com.alexanov.controller;

import com.alexanov.entity.*;
import com.alexanov.repos.QuestionRepo;
import com.alexanov.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private HashMap<Question,List<String>> questAndAnsw = new HashMap<Question,List<String>>();
    @Autowired
    UserRepo userRepo;
    @Autowired
    QuestionRepo questionRepo;
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
        HashMap<Question,List<String>> questAndAnswTemp = new HashMap<Question,List<String>>();
        questAndAnswTemp.putAll(questAndAnsw);
        model.addAttribute("qTypes", QType.values());
        model.addAttribute("questAndAnsw",questAndAnswTemp);
        model.addAttribute("text",QType.TEXT);
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
    @GetMapping("/sort/{qType}/{question}")
    public String sortType(@PathVariable QType qType, @PathVariable Question question){
        question.setqType(qType);
        questAndAnsw.remove(question);
        if(qType.equals(QType.TEXT)||question.getAnswer()==null) question.setAnswer("");
        questAndAnsw.put(question, Arrays.asList(question.getAnswer().split(" ")));
        questionRepo.save(question);
        return "redirect:/main";
    }
    @GetMapping("/addNewEmptyQuestion")
    public String addNewEmptyQuestion(){
        Question question = new Question();
        questAndAnsw.put(question, new ArrayList<String>());
        questionRepo.save(question);
        return "redirect:/main";
    }
    @GetMapping("/addNewAnswer/{question}")
    public String addNewAnswer(@PathVariable Question question, Model model){
        question.setAnswer(question.getAnswer()+" NewAnswer");
        questAndAnsw.put(question, Arrays.asList(question.getAnswer().split(" ")));
        questionRepo.save(question);
        return "redirect:/main";
    }
    @GetMapping("/addAnswer")
    public String addAnswer(@RequestParam String answer,@RequestParam Question question){
        question.setAnswer(question.getAnswer()+" "+answer);
        questAndAnsw.remove(question);
        questAndAnsw.put(question, Arrays.asList(question.getAnswer().split(" ")));
        questionRepo.save(question);
        return "redirect:/main";
    }
    @GetMapping("/deleteQuestion/{question}")
    public String deleteQuestion(@PathVariable Question question){
        questAndAnsw.remove(question);
        questionRepo.delete(question);
        return "redirect:/main";
    }
    @GetMapping("/deleteAnswer")
    public String deleteAnswer(@RequestParam String answer,@RequestParam Question question){
        List<String> answers = questAndAnsw.get(question);
        answers.remove(answer);
        question.setAnswer(String.join(" ",answers));
        questAndAnsw.remove(question);
        questAndAnsw.put(question,answers);
        questionRepo.delete(question);

        return "redirect:/main";
    }

}
