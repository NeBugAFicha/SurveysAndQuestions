package com.alexanov.repos;

import com.alexanov.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepo extends JpaRepository<Survey, Integer> {

}
