package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.enumitem.SocketMessageType;
import com.jvpars.codetip.dto.MessageDto;
import com.jvpars.codetip.dto.RoomDto;
import lombok.Data;

@Data
public class SocketMessage {
    SocketMessageType type;
    MessageDto message;
    RoomDto room;
    Long usersCount;

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
