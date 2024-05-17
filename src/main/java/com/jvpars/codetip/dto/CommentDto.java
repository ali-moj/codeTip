package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.TaskComment;

public class CommentDto {
    public Long id;

    public UserDto user;

    public ProjectTaskDto task;

    public Long createDate;

    public String body;

    public Long taskId;

    public Long userId;

    public CommentDto() {
    }

    public CommentDto(TaskComment comment) {
        this.id = comment.getId();
        this.body = comment.getBody();
        this.createDate = comment.getCreateDate();
        this.task = new ProjectTaskDto(comment.getTask());
        this.user = new UserDto(comment.getUser());
        this.taskId = comment.getTaskId();
        this.userId = comment.getUserId();
    }
}
