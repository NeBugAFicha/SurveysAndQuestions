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
    private List<Question> questAndAnswUsr = new ArrayList<Question>();
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
    public void deleteQuestion(Question question){
        questAndAnsw.remove(question);
        int i = question.getId();
        if(currentSurvey!=null&&question.getSurvey()!=null) {
            question.setUser(null);
            question.setSurvey(null);
            questionRepo.save(question);
        }
        questionRepo.deleteById(i);
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
        answers.set(answers.indexOf(oldAnswer),answer);
        questAndAnsw.remove(question);
        question.setAnswer(String.join("\n", answers));
        questAndAnsw.put(question, answers);
        questionRepo.save(question);
    }
    public void addOrUpdateQuestion(String text, Question question){
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
                question.setUser(user);
                questionRepo.save(question);
            }
        });
        questAndAnsw.clear();
        currentSurvey = null;
    }
    public void deleteSurvey(Survey survey, User user){
        List<Question> questions = questionRepo.findAllBySurvey(survey);
        questions.stream().forEach(question -> {
            question.setSurvey(null);
            question.setUser(null);
            questionRepo.save(question);
        });
        questionRepo.deleteAll(questions);
        surveyRepo.delete(survey);
    }
    public void finishSurvey(Survey survey){
        surveyRepo.save(survey);
    }
    public Map<Question, ArrayList<String>> getQuesAndAnsw(){
        return questAndAnsw;
    }
    public List<Question> getQuestAndAnswUsr() { return questAndAnswUsr; }

    public Survey getCurrentSurvey() {
        return currentSurvey;
    }

    public void setCurrentSurvey(Survey currentSurvey) {
        this.currentSurvey = currentSurvey;
        currentSurvey.getQuestions().stream().forEach(question -> questAndAnsw.put(question, new ArrayList<String>(Arrays.asList(question.getAnswer().split("\n")))));
    }

    public void answerToQues(Map<String, String> form ,Question question, User user){
        Question question1 = new Question();
        question1.setqType(question.getqType());
        question1.setText(question.getText());
        question1.setUser(user);
        question1.setSurvey(surveyRepo.findById(question.getSurvey().getId()).get());
        if(question.getqType().equals(QType.TEXT)) question1.setAnswer(form.get("text"));
        else if(question.getqType().equals(QType.SINGLEANSWER)) question1.setAnswer(form.get("singleAnswer"));
        else {
            for (String key : form.keySet()) {
                if (!key.equals("text")
                        && !key.equals("singleAnswer")
                        && !key.equals("_csrf")
                        && !key.equals("question")
                        && !key.equals("user")) question1.setAnswer(question.getAnswer()+" "+key);
            }
            question1.setAnswer(String.join("\n",question1.getAnswer().split(" ")));
        }
        questionRepo.save(question1);
        questAndAnsw.remove(question);
        questAndAnswUsr.add(question1);
        System.out.println(questAndAnsw.size());
    }
}
