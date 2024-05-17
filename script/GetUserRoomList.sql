create
    definer = root@localhost procedure GetUserRoomList(IN userId bigint)
BEGIN

SELECT
r.id As roomId  , r.create_time  as roomCreateTime , r.description as roomDescription , r.first_message_id as roomFirstMessageId ,
r.name as roomName , r.owner_id as roomOwnerId , r.type as roomType , r.url as roomUrl , r.user_can_leave roomUserCanLeave ,
au.avatar as ownerAvatar , au.full_name as ownerName , au.username as ownerUserName ,
(select m.body from messages m where m.id = r.last_message_id) as lastMessageBody ,
(select m.caption from messages m where m.id = r.last_message_id) as lastMessageCaption ,
(select m.message_type from messages m where m.id = r.last_message_id) as lastMessageType ,
(select m.id from messages m where m.id = r.last_message_id) as lastMessageId ,
(select m.time_stamp from messages m where m.id = r.last_message_id) as LastMessageTime,
(select au.full_name from messages m inner join app_users au on au.id = m.user_id  where m.id = r.last_message_id ) as LastMessageUserFullName ,
(select mute from room_users ru where ru.room_id = r.id and ru.user_id =  userId ) as roomMute ,
(select last_seen_id from room_users ru where ru.room_id = r.id and ru.user_id =  userId ) as lastSeenId ,
(select count(*) from messages m where  m.room_id = r.id and m.user_id!=userId and m.id > (select  last_seen_id from room_users ru where ru.room_id = r.id and ru.user_id = userId)) as unreadMessageCount
 FROM rooms r
 inner join app_users au on r.owner_id = au.id
where r.id in
 ( select ru.room_id from room_users ru where ru.user_id = userId ) and r.deleted = 0;

END;

