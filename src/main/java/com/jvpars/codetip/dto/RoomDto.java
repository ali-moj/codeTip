package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.domain.enumitem.MessageType;
import com.jvpars.codetip.domain.enumitem.RoomType;
import com.jvpars.codetip.dto.requests.FileRequest;
import com.jvpars.codetip.utils.TupleConverter;
import lombok.Data;

import javax.persistence.Tuple;

@Data
public class RoomDto {
    private Long id;
    private Long createTime;
    private String description;
    private Long firstMessageId;
    private Long lastMessageId;
    private String name;
    private UserDto user;
    private RoomType type;
    private String image;
    private Boolean userCanLeave;
    private MessageDto lastMessage;
    private Boolean mute;
    private Long unreadMessageCount;
    private Long lastSeenId;
    private Long ownerId;
    private FileRequest file;

    public RoomDto() {

    }

    public RoomDto(Room room) {
        this.id = room.getId();
        this.createTime = room.getCreateTime();
        this.description = room.getDescription();
        this.firstMessageId = room.getFirstMessageId();
        this.name = room.getName();
        this.type = room.getType();
        this.image = room.getUrl();
        this.userCanLeave = room.isUserCanLeave();
        this.ownerId = room.getOwnerId();
    }

    public static RoomDto GetRoomDto(Tuple tuple) {

        RoomDto item = new RoomDto();
        item.id = TupleConverter.getLong(tuple, "roomId");
        item.createTime = TupleConverter.getLong(tuple, "roomCreateTime");
        item.description = TupleConverter.getString(tuple, "roomDescription");
        item.firstMessageId = TupleConverter.getLong(tuple, "roomFirstMessageId");
        item.name = TupleConverter.getString(tuple, "roomName");
        Integer roomType = TupleConverter.getInteger(tuple, "roomType");
        item.type = RoomType.get(roomType);
        item.image  = TupleConverter.getString(tuple, "roomUrl");
        item.userCanLeave  = TupleConverter.getBoolean(tuple, "roomUserCanLeave");
        item.mute  = TupleConverter.getBoolean(tuple, "roomMute");
        item.unreadMessageCount  = TupleConverter.getLong(tuple, "unreadMessageCount");
        item.lastSeenId  = TupleConverter.getLong(tuple, "lastSeenId");

        //add owner user info
        UserDto owner = new UserDto();
        owner.setId(TupleConverter.getLong(tuple, "roomOwnerId"));
        owner.setAvatar(TupleConverter.getString(tuple, "ownerAvatar"));
        owner.setName(TupleConverter.getString(tuple, "ownerName"));
        owner.setUsername(TupleConverter.getString(tuple, "ownerUserName"));
        item.setUser(owner);

        //set last message
        item.lastMessageId = TupleConverter.getLong(tuple, "lastMessageId");
        MessageDto message = new MessageDto();
        message.setBody(TupleConverter.getString(tuple, "lastMessageBody"));
        message.setCaption(TupleConverter.getString(tuple, "lastMessageCaption"));
        message.setTimestamp(TupleConverter.getLong(tuple, "LastMessageTime"));
        message.setId(TupleConverter.getLong(tuple, "lastMessageId"));
        Integer typeNum = TupleConverter.getInteger(tuple, "lastMessageType");
        if(typeNum != null) {
            message.setType(MessageType.get(typeNum));
        }
        UserDto user = new UserDto();
        user.setName(TupleConverter.getString(tuple, "LastMessageUserFullName"));
        message.setUser(user);
        item.setLastMessage(message);
        return item;
    }

    public static RoomDto GetSimpleRoomDto(Tuple tuple) {

        RoomDto item = new RoomDto();
        item.setId(TupleConverter.getLong(tuple, "roomId"));
        item.setName(TupleConverter.getString(tuple, "roomName"));
        item.setImage(TupleConverter.getString(tuple, "roomUrl"));
        item.setMute(TupleConverter.getBoolean(tuple, "roomMute"));
        return item;
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
