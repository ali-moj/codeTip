package com.jvpars.codetip.repository;


import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.domain.RoomUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RoomUserRepository extends CrudRepository<RoomUser, Long> {

    List<RoomUser> findAllByUser(AppUser user);

    List<RoomUser> findAllByRoom(Room room);

    RoomUser findFirstByRoomAndUser(Room room, AppUser user);
}
