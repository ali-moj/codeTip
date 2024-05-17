package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.Message;
import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.repository.MessageRepository;
import com.jvpars.codetip.service.api.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    private MessageRepository repository;


    @Autowired
    public MessageServiceImpl(MessageRepository repository ) {
        this.repository = repository;

    }
    @Transactional(readOnly = false)
    @Override
    public Message save(Message message) {
        return repository.save(message);
    }
    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
      repository.deleteById(id);
    }

    @Override
    public Iterable<Message> findAll() {
        return repository.findAll();
    }

    @Override
    public Message findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<Message> findAllByChatRoomOrderByIdDesc(Room room) {
        return null;
    }

    @Override
    public List<Message> findTop100ByChatRoomOrderByIdDesc(Room room) {
        return repository.findTop100ByRoomOrderByIdDesc(room);
    }

    @Override
    public List<Message> findAllByChatRoomAndIdGreaterThanOrderByIdAsc(Room room, Long id) {
        return repository.findAllByRoomAndIdGreaterThanOrderByIdAsc(room,id);
    }

    @Override
    public List<Message> findTop100ByChatRoomAndIdLessThanOrderByIdDesc(Room room, Long id) {
        return repository.findTop100ByRoomAndIdLessThanOrderByIdDesc(room,id);
    }

    @Override
    public void updateSeen(@Param("msgId") Long msgId, @Param("roomId") Long roomId, @Param("userId") Long userId) {
        repository.updateSeen(msgId,roomId,userId);
    }

    @Override
    public Message findFirstByChatRoomOrderByIdDesc(Room room) {
        return repository.findFirstByRoomOrderByIdDesc(room);
    }

    public List<Message> getMessageIn(List<Long> ids){
        return repository.getMessageIn(ids);

    }

    public List<Message> findTop50ByRoomIdAndIdGreaterThanEqual(Long roomId ,Long id){
        return repository.findTop50ByRoomIdAndIdGreaterThanEqual(roomId , id);
    }

    public List<Message> findTop50ByRoomIdAndRoomIdLessThanEqual(Long roomId ,Long id){
        return repository.findTop50ByRoomIdAndRoomIdLessThanEqual(roomId , id);
    }

    public List<Message> findAllByRoomIdAndIdBetween(Long roomId ,Long startId , Long endId){
        return repository.findAllByRoomIdAndIdBetween(roomId , startId , endId);
    }

    public List<Message> findInRange(Long roomId ,Long seenId , Long take){
        return repository.findInRange(roomId , seenId , take);
    }

    public List<Message> findInRangePrev(Long roomId ,Long id , Long take){
        return repository.findInRangePrev(roomId , id , take);
    }

    public List<Message> findInRangeNext(Long roomId ,Long id , Long take){
        return repository.findInRangeNext(roomId , id , take);
    }

    public List<Message> findToEnd(Long roomId ,Long id){
        return repository.findToEnd(roomId , id);
    }

    public void updatePrivateSeen(Long roomId, Long startId, Long endId, Long userId){
        repository.updatePrivateSeen(roomId , startId , endId, userId);
    }

    public void updateChannelVisitCount(Long roomId, Long startId, Long endId, Long userId){
        repository.updateChannelVisitCount(roomId , startId , endId, userId);
    }

    public void UpdateGroupVisit(Long userId, Long visitTime, Long roomId, Long startId, Long endId){
        repository.UpdateGroupVisit(userId , visitTime , roomId , startId , endId);
    }

    @Override
    public List<Message> findMediaMessage(Long roomId) {
        return repository.findMediaMessage(roomId);
    }
}
