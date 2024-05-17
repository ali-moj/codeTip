package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.domain.RoomUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomUserService {

    @Transactional(readOnly = false)
    RoomUser save(RoomUser user);

    @Transactional(readOnly = false)
    void delete(RoomUser user);

    Iterable<RoomUser> findAll();

    RoomUser findOne(Long id);

    long count();

    List<RoomUser> findAllByUser(AppUser user);

    List<RoomUser> findAllByRoom(Room room);

    RoomUser findFirstByRoomAndUser(Room room, AppUser user);
}
