package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectDto {
    private Long id;
    private Integer version;
    private String title;
    private String description;
    private List<UserDto> users;
    private List<Long> usersRequest;
    private Long startDate;
    private Long endDate;
    private EntityStatus status;

    public ProjectDto() {
    }

    public ProjectDto(Project project) {
        id = project.getId();
        title = project.getTitle();
        description = project.getDescription();
        users = project.getUsers().stream().map(x -> new UserDto(x)).collect(Collectors.toList());
        startDate = project.getStartDate();
        endDate = project.getEndDate();
        status = project.getStatus();
    }
}
