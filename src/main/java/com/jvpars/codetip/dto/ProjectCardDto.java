package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.ProjectTask;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectCardDto {

    public Long id;
    public Long projectId;
    public String title;

    public List<ProjectTaskDto> tasks;


    public ProjectCardDto(ProjectCard card) {
        this.id = card.getId();
        this.projectId = card.getProjectId();
        this.title = card.getTitle();
        this.tasks = card.getTasks().stream().map(x -> new ProjectTaskDto(x)).collect(Collectors.toList());
    }

    public ProjectCardDto() {
    }

    //.................

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

    //.................
}
