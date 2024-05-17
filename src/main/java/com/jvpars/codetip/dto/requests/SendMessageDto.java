package com.jvpars.codetip.dto.requests;

import com.jvpars.codetip.domain.enumitem.MessageType;
import lombok.Data;

@Data
public class SendMessageDto {
    private long roomId;
    private long userId;
    private String body;
    private Long id;
    private String caption;
    private MessageType type;
    private Long replyTo;
    private FileRequest file;
}
