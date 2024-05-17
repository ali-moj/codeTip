package com.jvpars.codetip.repository;
import com.jvpars.codetip.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long>  , RoomCustomRepository{
    Room findTopById(Long id);
    Page<Room> findAll(Pageable pageable);
}
