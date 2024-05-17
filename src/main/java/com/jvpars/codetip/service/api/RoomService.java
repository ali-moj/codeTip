package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.dto.RoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomService {
    @Transactional(readOnly = false)
    Room save(Room room);

    @Transactional(readOnly = false)
    void delete(Long id);

    Iterable<Room> findAll();

    public abstract Page<Room> findAllPageable(Pageable pageable);

    Room findOne(Long id);

    long count();

    Room findTopById(Long id);

    List<RoomDto> getUserRoomList(Long userId);
     List<RoomDto> getUserRoomListSimple(Long userId);
}
