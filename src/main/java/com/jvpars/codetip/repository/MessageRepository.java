package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.Message;
import com.jvpars.codetip.domain.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {


    List<Message> findAllByRoomOrderByIdDesc(Room room);

    List<Message> findTop100ByRoomOrderByIdDesc(Room room);

    List<Message> findAllByRoomAndIdGreaterThanOrderByIdAsc(Room room, Long id);

    List<Message> findTop100ByRoomAndIdLessThanOrderByIdDesc(Room room, Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE messages msg SET seen_count=1 WHERE msg.id <=:msgId AND msg.room_id =:roomId AND msg.user_id =:userId AND msg.seen_count < 1",
            nativeQuery = true)
    void updateSeen(@Param("msgId") Long msgId, @Param("roomId") Long roomId, @Param("userId") Long userId);

    Message findFirstByRoomOrderByIdDesc(Room room);

    @Query(value = "select  * from messages where id in ( :ids  ) ; ", nativeQuery = true)
    List<Message> getMessageIn(@Param("ids") List<Long> ids);

    List<Message> findTop50ByRoomIdAndIdGreaterThanEqual(Long roomId, Long id);

    List<Message> findTop50ByRoomIdAndRoomIdLessThanEqual(Long roomId, Long id);

    List<Message> findAllByRoomIdAndIdBetween(Long roomId, Long startId, Long endId);

    @Query(value = "select * from messages where room_id=:roomId and id<:id order by id desc limit :take" , nativeQuery = true)
    List<Message> findInRangePrev(@Param("roomId") Long roomId, @Param("id") Long id, @Param("take") Long take);

    @Query(value = "(select * from messages\n" +
            "where room_id=:roomId and id>:id\n" +
            "limit :take)\n", nativeQuery = true)
    List<Message> findInRangeNext(@Param("roomId") Long roomId, @Param("id") Long id, @Param("take") Long take);

    @Query(value = "(select * from messages\n" +
            "where room_id=:roomId and id>:id)\n", nativeQuery = true)
    List<Message> findToEnd(@Param("roomId") Long roomId, @Param("id") Long id);

    @Query(value = "(select * from messages\n" +
                    "where room_id=:roomId and id<=:seenId\n" +
                    "order by id desc\n" +
                    "limit :take)\n" +
                    "union\n" +
                    "(select * from messages\n" +
                    "where room_id=:roomId and id>:seenId\n" +
                    "limit :take)\n" +
                    "order by id", nativeQuery = true)
    List<Message> findInRange(@Param("roomId") Long roomId, @Param("seenId") Long seenId, @Param("take") Long take);

    //for private message seen
    @Modifying
    @Query(value = "UPDATE messages msg SET seen = true WHERE msg.room_id =:roomId AND msg.user_id !=:userId AND (msg.id >= :startId and msg.id <= :endId)", nativeQuery = true)
    void updatePrivateSeen(@Param("roomId") Long roomId, @Param("startId") Long startId, @Param("endId") Long endId, @Param("userId") Long userId);


    //for channel increase message view count
    @Modifying
    @Query(value = "update messages msg set msg.visit_count =msg.visit_count + 1 WHERE msg.room_id =:roomId AND msg.user_id !=:userId AND (msg.id > :startId and msg.id <= :endId)", nativeQuery = true)
    void updateChannelVisitCount(@Param("roomId") Long roomId, @Param("startId") Long startId, @Param("endId") Long endId, @Param("userId") Long userId);


    //for update group visit count and insert user_message table
    @Modifying
    @Query(value = "call UpdateGroupVisit(:userId , :visitTime , :roomId , :startId,  :endId)", nativeQuery = true)
    void UpdateGroupVisit(@Param("userId") Long userId,
                          @Param("visitTime") Long visitTime,
                          @Param("roomId") Long roomId,
                          @Param("startId") Long startId,
                          @Param("endId") Long endId);


    @Query("select m  from Message m  where m.messageType > 0 and m.roomId = :roomId" )
    List<Message> findMediaMessage(@Param("roomId") Long roomId);



}

