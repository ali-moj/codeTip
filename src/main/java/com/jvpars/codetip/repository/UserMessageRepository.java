package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.UserMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends CrudRepository<UserMessage, Long> {
    @Query(value = "(select * from user_messages where message_id=:messageId)", nativeQuery = true)
    List<UserMessage> findVisitors(@Param("messageId") Long messageId);
}
