package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.Message;
import com.jvpars.codetip.domain.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageService {
    @Transactional(readOnly = false)
    Message save(Message message);

    @Transactional(readOnly = false)
    void delete(Long id);

    Iterable<Message> findAll();

    Message findOne(Long id);

    long count();


    List<Message> findAllByChatRoomOrderByIdDesc(Room room);

    List<Message> findTop100ByChatRoomOrderByIdDesc(Room room);

    List<Message> findAllByChatRoomAndIdGreaterThanOrderByIdAsc(Room room, Long id);

    List<Message> findTop100ByChatRoomAndIdLessThanOrderByIdDesc(Room room, Long id);

    @Transactional
    @Modifying
    void updateSeen(Long msgId, Long roomId, Long userId);

    Message findFirstByChatRoomOrderByIdDesc(Room room);

    List<Message> getMessageIn(List<Long> ids);

    List<Message> findTop50ByRoomIdAndIdGreaterThanEqual(Long roomId, Long id);

    List<Message> findTop50ByRoomIdAndRoomIdLessThanEqual(Long roomId, Long id);

    List<Message> findAllByRoomIdAndIdBetween(Long roomId, Long startId, Long endId);

    List<Message> findInRangePrev(Long roomId ,Long id , Long take);

    List<Message> findInRangeNext(Long roomId ,Long id , Long take);

    List<Message> findToEnd(Long roomId ,Long id);

    List<Message> findInRange(Long roomId, Long seenId, Long take);

    @Transactional
    @Modifying
    void updatePrivateSeen(Long roomId, Long startId, Long endId, Long userId);

    @Transactional
    @Modifying
    void updateChannelVisitCount(Long roomId, Long startId, Long endId, Long userId);

    @Transactional
    @Modifying
    void UpdateGroupVisit(Long userId, Long visitTime, Long roomId, Long startId, Long endId);

    List<Message> findMediaMessage(Long roomId);
}
