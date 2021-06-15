package com.alexanov.repos;

import com.alexanov.entity.Question;
import com.alexanov.entity.Survey;
import com.alexanov.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepo extends JpaRepository<Question, Integer> {
    List<Question> findAllBySurvey(Survey survey);
    @Query("Select q from Question q where user_id = ?1 group by survey_id")
    List<Question> findQuestionByUser(User user);
    List<Question> findAllBySurveyAndUser(Survey survey, User user);
    List<Question> findQuestionByUserAndSurvey(User user ,Survey survey);
}
