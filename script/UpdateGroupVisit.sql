create
    definer = root@localhost procedure UpdateGroupVisit(IN userId bigint, IN visitTime bigint, IN roomId bigint,
                                                        IN startId bigint, IN endId bigint)
BEGIN

update messages msg set msg.visit_count =msg.visit_count + 1 WHERE msg.room_id = roomId AND msg.user_id!=userId AND (msg.id > startId and msg.id <= endId);
insert into user_messages(user_messages.message_id , user_messages.room_id , user_messages.timestamp , user_messages.user_id)
select msg.id , roomId , visitTime , userId from  messages msg where msg.room_id = roomId AND msg.user_id!=userId AND (msg.id > startId and msg.id <= endId);

END;

