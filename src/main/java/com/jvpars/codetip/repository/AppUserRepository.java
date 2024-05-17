package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    AppUser findFirstByUsername(String username);

//    List<AppUser> findAllByRooms(ChatRoom room);

    AppUser findByUsername(String username);

    @Query(value = "select * from app_users users\n" +
            "join room_users ru on users.id = ru.user_id\n" +
            "where ru.room_id=:roomId and ru.user_id!=:userId", nativeQuery = true)
    // shiiiitttt. semicolon in the end of line caused a fucking stupid error
    AppUser findPrivateChatOtherSide(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query(value = "select u from AppUser u where u.username like %:name%")
    List<AppUser> searchByUsername(@Param("name") String name);

    Page<AppUser> findAll(Pageable pageable);
}
