package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.ProjectTask;
import com.jvpars.codetip.domain.enumitem.EntityStatus;

public class ProjectTaskDto {

    public Long id;
    public String title;
    public String description;
    public Long cardId;
    public Long userId;
    public Long startDate;
    public Long endDate;
    public EntityStatus status;
    public UserDto user;
    public Long arrange;

    public ProjectTaskDto() {
    }

    public ProjectTaskDto(ProjectTask task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.cardId = task.getCardId();
        this.userId = task.getUserId();
        this.startDate = task.getStartDate();
        this.endDate = task.getEndDate();
        this.status = task.getStatus();
        this.user = new UserDto(task.getUser());
        this.arrange = task.getArrange();
    }

    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind
                    .ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
