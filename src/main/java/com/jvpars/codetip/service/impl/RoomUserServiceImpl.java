package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.domain.RoomUser;
import com.jvpars.codetip.repository.RoomUserRepository;
import com.jvpars.codetip.service.api.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomUserServiceImpl implements RoomUserService {
    private RoomUserRepository repository;


    @Autowired
    public RoomUserServiceImpl(RoomUserRepository repository ) {
        this.repository = repository;

    }
    @Transactional(readOnly = false)
    @Override
    public RoomUser save(RoomUser user) {
        return repository.save(user);
    }
    @Transactional(readOnly = false)
    @Override
    public void delete(RoomUser user) {
      repository.delete(user);
    }

    @Override
    public Iterable<RoomUser> findAll() {
        return repository.findAll();
    }

    @Override
    public RoomUser findOne(Long id) {
        return findOne(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<RoomUser> findAllByUser(AppUser user) {
        return repository.findAllByUser(user);
    }

    @Override
    public List<RoomUser> findAllByRoom(Room room) {
        return repository.findAllByRoom(room);
    }

    @Override
    public RoomUser findFirstByRoomAndUser(Room room, AppUser user) {
        return repository.findFirstByRoomAndUser(room,user);
    }
}
