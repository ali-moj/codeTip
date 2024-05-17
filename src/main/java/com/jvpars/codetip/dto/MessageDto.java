package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.Message;
import com.jvpars.codetip.domain.enumitem.MessageType;
import lombok.Data;

@Data
public class MessageDto {

    private Long id;
    private String body;
    private String caption;
    private boolean deleted;
    private MessageType type;
    private MessageDto replyTo;
    private Long roomId;
    private RoomDto room;
    private Long timestamp;
    private UserDto user;
    private Long visitCount;
    private boolean seen;

    public MessageDto() {
    }


    public MessageDto(Message message) {
        this.id = message.getId();
        this.deleted = message.getDeleted();
        this.type = message.getMessageType();
        this.roomId = message.getRoomId();
        this.timestamp = message.getTimeStamp();
        this.user = new UserDto(message.getUser());

        // if message is deleted don't populate content
        if(!this.deleted) {
            this.body = message.getBody();
            this.caption = message.getCaption();
            if (message.getReplyToId() != null)
                this.replyTo = new MessageDto(message.getReply());
            this.visitCount = message.getVisitCount();
            this.seen = message.isSeen();
        }
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
