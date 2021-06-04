package com.alexanov.service;

import com.alexanov.entity.*;
import com.alexanov.repos.QuestionRepo;
import com.alexanov.repos.SurveyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.alexanov.repos.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private Map<Question,ArrayList<String>> questAndAnsw = new TreeMap<Question,ArrayList<String>>(new Comparator<Question>() {
        @Override
        public int compare(Question o1, Question o2) {
            return o1.getId()-o2.getId();
        }
    });
    private Survey currentSurvey;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private QuestionRepo questionRepo;
    @Autowired
    private SurveyRepo surveyRepo;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDetails user = userRepo.findByUsername(s);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return userRepo.findByUsername(s);
    }
    public boolean addUser(User user){
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if (userFromDb != null) return false;
        user.setStatus(Status.ACTIVE);
        user.setRole(Role.USER);
        user.setPassword(user.getPassword());
        userRepo.save(user);
        return true;
    }
    public void chooseQType(QType qType, Question question){
        questAndAnsw.remove(question);
        question.setqType(qType);
        question.setAnswer("");
        questAndAnsw.put(question, new ArrayList<String>());
        questionRepo.save(question);
    }
    public void addNewEmptyQuestion(){
        Question question = new Question();
        questionRepo.save(question);
        questAndAnsw.put(question, new ArrayList<String>());
    }
    public void addAnswer(String answer, Question question){
        ArrayList<String> answers = questAndAnsw.get(question);
        if(answers.contains(answer)) return;
        questAndAnsw.remove(question);
        answers.add(answer);
        question.setAnswer(String.join("\n",answers));
        questAndAnsw.put(question, answers);
        questionRepo.save(question);
    }
    public void deleteQuestion(Question question, User user){
        questAndAnsw.remove(question);
        questionRepo.delete(question);
    }
    public void deleteAnswer(String answer, Question question){
        ArrayList<String> answers = questAndAnsw.get(question);
        questAndAnsw.remove(question);
        answers.remove(answer);
        question.setAnswer(String.join("\n",answers));
        questAndAnsw.put(question,answers);
        questionRepo.save(question);
    }
    public void updateAnswer(String answer, String oldAnswer, Question question){
        ArrayList<String> answers = questAndAnsw.get(question);
        answers.set(answer.indexOf(oldAnswer),answer);
        questAndAnsw.remove(question);
        question.setAnswer(String.join("\n", answers));
        questAndAnsw.put(question, answers);
    }
    public void addOrUpdateQuestion(String text, Question question, User user){
        ArrayList<String> answers = questAndAnsw.get(question);
        questAndAnsw.remove(question);
        question.setText(text);
        questAndAnsw.put(question,answers);
        questionRepo.save(question);
    }
    public void addSurvey(String title, String description, User user){
        Calendar calendar = Calendar.getInstance();
        Survey survey = new Survey();
        survey.setStartTime(calendar);
        survey.setTitle(title);
        survey.setDescription(description);
        surveyRepo.save(survey);
        questAndAnsw.keySet().stream().forEach(question -> {
            question.setSurvey(survey);
            question.setUser(user);
            questionRepo.save(question);
        });
        questAndAnsw.clear();
        currentSurvey = null;
    }
    public void updateSurvey(String title, String description, User user){
        currentSurvey.setTitle(title);
        currentSurvey.setDescription(description);
        questAndAnsw.keySet().stream().forEach(question -> {
            if(!user.getQuestions().contains(question)) {
                question.setSurvey(currentSurvey);
                questionRepo.save(question);
                Question question1 = questionRepo.findById(question.getId()).get();
                question1.setUser(user);
                user.getQuestions().add(question1);
                userRepo.save(user);
            }
        });
        questAndAnsw.clear();
        currentSurvey = null;
    }
    public Map<Question, ArrayList<String>> getQuesAndAnsw(){
        return questAndAnsw;
    }


    public Survey getCurrentSurvey() {
        return currentSurvey;
    }

    public void setCurrentSurvey(Survey currentSurvey) {
        this.currentSurvey = currentSurvey;
        currentSurvey.getQuestions().stream().forEach(question -> questAndAnsw.put(question, new ArrayList<String>(Arrays.asList(question.getAnswer().split("\n")))));
    }
}
