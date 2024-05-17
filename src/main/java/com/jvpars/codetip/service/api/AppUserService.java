package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AppUserService {

    @Transactional(readOnly = false)
    AppUser save(AppUser user, boolean changePassword);

    @Transactional(readOnly = false)
    AppUser update(AppUser user);

    @Transactional(readOnly = false)
    void delete(Long id);

    Iterable<AppUser> findAll();

    AppUser findOne(Long id);

    long count();

    AppUser Login(String username, String password);

    AppUser findByUsername(String username);

    List<AppUser> finAllByRoom(Room room);

    AppUser findPrivateChatOtherSide(Long roomId,Long userId);

    List<AppUser> searchByUsername(String name);

    Page<AppUser> findAllPageable(Pageable pageable);
}
