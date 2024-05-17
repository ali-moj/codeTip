package com.jvpars.codetip.repository;

import com.jvpars.codetip.dto.RoomDto;

import java.util.List;

public interface RoomCustomRepository {
    List<RoomDto> getUserRoomList(Long UserId);
    List<RoomDto> getUserRoomListSimple(Long userId);

}