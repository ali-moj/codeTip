package com.jvpars.codetip.repository;

import com.jvpars.codetip.dto.RoomDto;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class RoomCustomRepositoryImpl implements RoomCustomRepository  {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<RoomDto> getUserRoomList(Long userId) {
        Query q = entityManager.createNativeQuery(
                "call GetUserRoomList(:userId)",
                Tuple.class);
        q.setParameter("userId", userId);
        List<Tuple> recordList = q.getResultList();

        List<RoomDto> dd = recordList.stream().map(
                s -> RoomDto.GetRoomDto(s)).collect(Collectors.toList());
        return dd;
    }

    @Override
    public List<RoomDto> getUserRoomListSimple(Long userId) {

        Query q = entityManager.createNativeQuery(
                "select r.id as roomId , r.name as roomName ,  r.url  as roomUrl , ru.mute as roomMute  from rooms r inner join room_users ru on ru.user_id = :userId and ru.room_id = r.id where ru.user_id = :userId and r.deleted = 0",
                Tuple.class);
        q.setParameter("userId", userId);
        List<Tuple> recordList = q.getResultList();

        List<RoomDto> dd = recordList.stream().map(
                s -> RoomDto.GetSimpleRoomDto(s)).collect(Collectors.toList());
        return dd;
    }
}
