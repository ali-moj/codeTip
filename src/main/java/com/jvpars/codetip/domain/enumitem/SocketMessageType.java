package com.jvpars.codetip.domain.enumitem;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum  SocketMessageType {
    NEW_MESSAGE, MESSAGE_DELETE, SEEN, ADD_TO_ROOM, LEAVE_ROOM, ROOM_UPDATE, ONLINE_USERS_COUNT, ADMIN_MESSAGE
}
