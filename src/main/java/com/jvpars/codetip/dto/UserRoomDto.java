package com.jvpars.codetip.dto;

public class UserRoomDto {
    public Long userId;
    public Long roomId;
    public boolean mute;
    public UserDto user;
    public RoomDto room;

    public UserRoomDto() {}

    public UserRoomDto(UserDto user) {
        this.user = user;
    }

    public UserRoomDto(RoomDto room) {
        this.room = room;
    }
}
