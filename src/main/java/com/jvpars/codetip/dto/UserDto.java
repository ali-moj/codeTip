package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.UserMessage;
import com.jvpars.codetip.dto.requests.FileRequest;
import lombok.Data;

import javax.persistence.Transient;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String username;
    private String avatar;
    private String token;
    private boolean admin;

    @Transient
    public FileRequest file;

    public UserDto() {
    }

    public UserDto(AppUser user) {
        this.id = user.getId();
        this.name = user.getFullName();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.admin = user.getAdmin();
    }

    public UserDto(UserMessage userMessage) {
        this.id = userMessage.getUser().getId();
        this.name = userMessage.getUser().getFullName();
        this.username = userMessage.getUser().getUsername();
        this.avatar = userMessage.getUser().getAvatar();
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
