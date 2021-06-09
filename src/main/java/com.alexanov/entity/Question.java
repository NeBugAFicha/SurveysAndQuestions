package com.alexanov.entity;

import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Enumerated(EnumType.STRING)
    private QType qType;
    private String text;
    private String answer;
    public Question() {
        this.qType =  QType.TEXT;
        this.answer = "";
    }
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user ;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QType getqType() {
        return qType;
    }

    public void setqType(QType qType) {
        this.qType = qType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object obj) {
        return this.id==((Question) obj).getId();
    }

    @Override
    public int hashCode() {
        return 1;
    }


}
