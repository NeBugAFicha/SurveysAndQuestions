package com.alexanov.entity;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String description;
    private Calendar startTime;
    private Calendar endTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "survey", cascade = {CascadeType.PERSIST} )
    private List<Question> questions;
    public Survey(){
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
