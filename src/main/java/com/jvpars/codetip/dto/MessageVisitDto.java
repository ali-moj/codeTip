package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.UserMessage;
import lombok.Data;

@Data
public class MessageVisitDto {
    private Long userId;
    private Long messageId ;
    private Long time;
    private String userName;
    private String userAvatar;

    public MessageVisitDto(){
    }

    public MessageVisitDto(UserMessage message){
        userId = message.getUser().getId();
        messageId = message.getMessage().getId();
        time = message.getTimestamp();
        userName = message.getUser().getFullName();
        userAvatar = message.getUser().getAvatar();
    }
}
