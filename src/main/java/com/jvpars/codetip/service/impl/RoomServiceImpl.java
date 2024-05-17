package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.dto.RoomDto;
import com.jvpars.codetip.repository.RoomRepository;
import com.jvpars.codetip.service.api.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private RoomRepository repository;


    @Autowired
    public RoomServiceImpl(RoomRepository repository) {
        this.repository = repository;

    }

    @Transactional(readOnly = false)
    @Override
    public Room save(Room room) {

        try {

            return repository.save(room);
        } catch (Exception ex) {
            return null;
        }

    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Iterable<Room> findAll() {
        return repository.findAll();
    }


    @Override
    public Room findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Room findTopById(Long id) {
        return repository.findTopById(id);
    }

    @Override
    public List<RoomDto> getUserRoomList(Long userId) {
        return repository.getUserRoomList(userId);
    }

    public List<RoomDto> getUserRoomListSimple(Long userId) {
        return repository.getUserRoomListSimple(userId);
    }

    @Override
    public Page<Room> findAllPageable(Pageable pageable){
        return repository.findAll(pageable);
    }
}
