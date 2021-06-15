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
    private Survey currentSurvey = null;
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
    public void addAnswer(String answer, Question questionTemp){
        ArrayList<String> answers = questAndAnsw.get(questionTemp);
        if(answers.contains(answer)) return;
        Question question = questAndAnsw.keySet().stream().filter(question1 -> question1.getId()==questionTemp.getId()).collect(Collectors.toList()).get(0);
        questAndAnsw.remove(question);
        answers.add(answer);
        question.setAnswer(String.join("\n",answers));
        questAndAnsw.put(question, answers);
    }
    public void deleteQuestion(Question question){
        questAndAnsw.remove(question);
        if(currentSurvey!=null&&question.getSurvey()!=null){
            question.setSurvey(null);
            question.setUser(null);
        }
        questionRepo.delete(question);
    }
    public void deleteAnswer(String answer, Question questionTemp){
        ArrayList<String> answers = questAndAnsw.get(questionTemp);
        Question question = questAndAnsw.keySet().stream().filter(question1 -> question1.getId()==questionTemp.getId()).collect(Collectors.toList()).get(0);
        questAndAnsw.remove(question);
        answers.remove(answer);
        question.setAnswer(String.join("\n",answers));
        questAndAnsw.put(question,answers);
    }
    public void updateAnswer(String answer, String oldAnswer, Question questionTemp){
        ArrayList<String> answers = questAndAnsw.get(questionTemp);
        answers.set(answers.indexOf(oldAnswer),answer);
        Question question = questAndAnsw.keySet().stream().filter(question1 -> question1.getId()==questionTemp.getId()).collect(Collectors.toList()).get(0);
        questAndAnsw.remove(question);
        question.setAnswer(String.join("\n", answers));
        questAndAnsw.put(question, answers);
    }
    public void addOrUpdateQuestion(String text, Question questionTemp){
        ArrayList<String> answers = questAndAnsw.get(questionTemp);
        Question question = questAndAnsw.keySet().stream().filter(question1 -> question1.getId()==questionTemp.getId()).collect(Collectors.toList()).get(0);
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
        questionRepo.deleteAll(questionRepo.findAllBySurvey(currentSurvey).stream()
                .filter(question -> question.getUser().getId()!=user.getId())
                .map(question -> {
                    question.setSurvey(null);
                    question.setUser(null);
                    return question;
                })
                .collect(Collectors.toList()));
        questAndAnsw.clear();
        currentSurvey = null;
    }
    public void deleteSurvey(Survey survey, User user){
        List<Question> questions = questionRepo.findAllBySurvey(survey);
        questions.stream().forEach(question -> {
            question.setSurvey(null);
            question.setUser(null);
            questionRepo.delete(question);
        });
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
        currentSurvey.getQuestions().stream().forEach(question -> {
            if(question.getUser().getRole().toString()=="ADMIN") questAndAnsw.put(question, new ArrayList<String>(Arrays.asList(question.getAnswer().split("\n"))));
        });
    }

    public void answerToQues(Map<String, String> form ,Question question, User user){
        Question question1 = new Question();
        question1.setqType(question.getqType());
        question1.setText(question.getText());
        question1.setUser(user);
        question1.setSurvey(surveyRepo.findById(question.getSurvey().getId()).get());
        if(question.getqType().equals(QType.TEXT)) {
            if(form.get("text").isEmpty()||form.get("text")==null) return;
            question1.setAnswer(form.get("text"));
        }
        else if(question.getqType().equals(QType.SINGLEANSWER)) {
            if(form.get("singleAnswer")==null) return;
            question1.setAnswer(form.get("singleAnswer"));
        }
        else {
            for (String key : form.keySet()) {
                if (!key.equals("text")
                        && !key.equals("singleAnswer")
                        && !key.equals("_csrf")
                        && !key.equals("question")
                        && !key.equals("user")) question1.setAnswer(question1.getAnswer()+"\n"+key);
            }
            question1.setAnswer(question1.getAnswer().substring((1)));
        }
        questionRepo.save(question1);
        questAndAnsw.remove(question);
        questAndAnswUsr.add(question1);
    }
}
