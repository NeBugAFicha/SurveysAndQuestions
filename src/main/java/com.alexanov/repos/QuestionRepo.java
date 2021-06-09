package com.alexanov.repos;

import com.alexanov.entity.Question;
import com.alexanov.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepo extends JpaRepository<Question, Integer> {
    List<Question> findAllBySurvey(Survey survey);
}
