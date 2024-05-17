package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.*;
import com.jvpars.codetip.domain.enumitem.MessageType;
import com.jvpars.codetip.domain.enumitem.RoomType;
import com.jvpars.codetip.domain.enumitem.SocketMessageType;
import com.jvpars.codetip.dto.MessageVisitDto;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.UserRoomDto;
import com.jvpars.codetip.dto.MessageDto;
import com.jvpars.codetip.dto.RoomDto;
import com.jvpars.codetip.dto.UserDto;
import com.jvpars.codetip.dto.requests.*;
import com.jvpars.codetip.dto.SocketMessage;
import com.jvpars.codetip.service.api.*;
import com.jvpars.codetip.utils.DocumentService;
import com.jvpars.codetip.utils.FolderPath;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import com.jvpars.codetip.utils.MyArgUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping(value = "/api/chat")
public class ChatApi {


    private RoomService roomService;
    private AppUserService userService;
    private MessageService messageService;
    private DocumentService documentService;
    private RoomUserService roomUserService;
    private UserMessageService userMessageService;
    private SimpMessagingTemplate template;


    @Autowired
    public ChatApi(RoomService roomService,
                   AppUserService userService,
                   MessageService messageService,
                   DocumentService documentService,
                   RoomUserService roomUserService,
                   UserMessageService userMessageService, SimpMessagingTemplate template) {
        this.roomService = roomService;
        this.userService = userService;
        this.messageService = messageService;
        this.documentService = documentService;
        this.roomUserService = roomUserService;
        this.userMessageService = userMessageService;
        this.template = template;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/edit-room", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editRoom(@RequestBody RoomDto request) {
        try {
            Room room = roomService.findOne(request.getId());
            if (room == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            // private room cannot change to private because of users number
            if(room.getType() != RoomType.PRIVATE && request.getType() == RoomType.PRIVATE) {
                return GenericResponseGenerator.response(OperationResult.Fail);
            }

            room.setName(request.getName());
            room.setType(request.getType());
            if (request.getFile() != null) {
                byte[] data = documentService.Base64ToByte(request.getFile().data);
                String fileName = String.format("%s.%s",
                        new Random()
                                .nextInt(15000000),
                        FilenameUtils.getExtension(request.getFile().name));
                String fileUrl = documentService.saveImageFile(FolderPath.CHAT, fileName, data);
                if (fileUrl.equals("")) {
                    return GenericResponseGenerator.response(OperationResult.Fail);
                }
                room.setUrl(fileUrl);
            }

            roomService.save(room);
            RoomDto roomDto = new RoomDto(room);

            SocketMessage msg = new SocketMessage();
            msg.setRoom(new RoomDto(room));
            msg.setType(SocketMessageType.ROOM_UPDATE);
            template.convertAndSend("/user/queue/room/" + room.getId(), msg);

            return GenericResponseGenerator.success(roomDto);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/create-room", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRoom(@RequestBody RoomDto request, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Room room = new Room();
            room.setName(request.getName());
            byte[] data = documentService.Base64ToByte(request.getFile().data);
            String fileName = String.format("%s.%s",
                    new Random().nextInt(15000000),
                    FilenameUtils.getExtension(request.getFile().name));
            String fileUrl = documentService.saveImageFile(FolderPath.CHAT, fileName, data);
            if (fileUrl.equals("")) {
                return GenericResponseGenerator.response(OperationResult.Fail);
            }
            room.setUrl(fileUrl);
            room.setFirstMessageId(0L);
            room.setLastMessageId(0L);
            room.setCreateTime(MyArgUtils.nowEpoch());
            room.setDeleted(false);
            room.setOwnerId(user.getId());
            room.setType(request.getType());
            roomService.save(room);
            RoomDto roomDto = new RoomDto(room);
            return GenericResponseGenerator.success(roomDto);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/add-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity roomMessage(@RequestBody UserRoomDto request) {
        try {
            Room room = roomService.findOne(request.roomId);
            AppUser user = userService.findOne(request.userId);
            if (room == null && user == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            // private room edit is not allowed
            if (room.getType() == RoomType.PRIVATE) {
                return GenericResponseGenerator.response(OperationResult.Fail);
            }

            List<RoomUser> members = roomUserService.findAllByRoom(room);
            RoomUser roomUser = new RoomUser();
            roomUser.setUser(user);
            roomUser.setRoom(room);
            if(room.getLastMessageId()!=null) {
                roomUser.setLastSeenId(room.getLastMessageId());
            }
            roomUserService.save(roomUser);

            SocketMessage socketMessage = new SocketMessage();
            socketMessage.setType(SocketMessageType.ADD_TO_ROOM);
            RoomDto addedRoom = new RoomDto(room);
            if(room.getLastMessageId()!=null) {
                addedRoom.setLastSeenId(room.getLastMessageId());
            }
            UserDto userDto = new UserDto(user);
            addedRoom.setUser(userDto);
            MessageDto messageDto=null;
            if(room.getLastMessageId()!=null) {
                Message message = messageService.findOne(room.getLastMessageId());
                if(message !=null) {
                      messageDto = new MessageDto(message);
                }
            }
            addedRoom.setLastMessage(messageDto);
            socketMessage.setRoom(addedRoom);

            /*
                two socket message required:
                    1. for the user who added to a room
                    2. for other users in the room
             */
            template.convertAndSend("/user/queue/room/" + room.getId(), socketMessage);
            template.convertAndSend("/user/queue/" + user.getId(), socketMessage);

            return GenericResponseGenerator.success(userDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/remove-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeUser(@RequestBody UserRoomDto request) {
        try {
            Room room = roomService.findOne(request.roomId);
            AppUser user = userService.findOne(request.userId);
            if (room == null && user == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            // private room edit is not allowed
            if (room.getType() == RoomType.PRIVATE) {
                return GenericResponseGenerator.response(OperationResult.Fail);
            }

            List<RoomUser> members = roomUserService.findAllByRoom(room);
            RoomUser roomUser = new RoomUser();
            roomUser.setUser(user);
            roomUser.setRoom(room);
            roomUserService.delete(roomUser);

            if (room != null) {
                System.out.println("request user id = " + user.getId() + " room id = " + room.getId());
            }

            SocketMessage socketMessage = new SocketMessage();
            socketMessage.setType(SocketMessageType.LEAVE_ROOM);
            RoomDto addedRoom = new RoomDto(room);
            UserDto userDto = new UserDto(user);
            addedRoom.setUser(userDto);
            socketMessage.setRoom(addedRoom);
            template.convertAndSend("/user/queue/room/" + room.getId(), socketMessage);

            return GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/room-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity roomList(Pageable pageable) {
        try {
            Page<Room> list = roomService.findAllPageable(pageable);
            List<RoomDto> res = new ArrayList<>();
            list.forEach(x -> res.add(new RoomDto(x)));
            long total = list.getTotalElements();
            int pageCount = list.getTotalPages();
            return GenericResponseGenerator.pageable(total, pageCount, res);

        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/room/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRoom(@PathVariable Long id) {
        try {
            Room room = roomService.findOne(id);
            if (room == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            return GenericResponseGenerator.success(new RoomDto(room));

        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    /*
        fetch user's room list.
        param: simple (boolean)
        simple = true  ===> fetch only rooms general info
        simple = false ===> fetch rooms and users full info (e.g: unread messages count, last message, ...)
    */
    @GetMapping(value = "/get-user-room/{simple}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GetUserRoomList(@PathVariable Boolean simple, Principal principal) {
        log.info("GetUserRoomList callback");
        try {
            AppUser user = userService.findByUsername(principal.getName());
            List<RoomDto> rooms;
            if (simple) {
                rooms = roomService.getUserRoomListSimple(user.getId());
            } else {
                rooms = roomService.getUserRoomList(user.getId());
            }
            //log.info(rooms + "");
            List<RoomDto> privateRooms = rooms.stream().filter(x -> x.getType() == RoomType.PRIVATE).collect(toList());
            UserDto tempUser;
            RoomDto tempRoom;

            /*
               for private rooms (a room has 2 members) image and name of the room change to user's info
               example: a room has users a nd b
                        for user a ---> room name and image change to user b name and avatar
                        for user b ---> room name and image change to user a name and avatar
             */
            for (RoomDto room : privateRooms) {
                tempUser = new UserDto(userService.findPrivateChatOtherSide(room.getId(), user.getId()));
                tempRoom = rooms.stream().filter(x -> x.getId() == room.getId()).findFirst().get();
                tempRoom.setImage(tempUser.getAvatar());
                tempRoom.setName(tempUser.getName());
                tempRoom.setUser(tempUser);
            }

            return GenericResponseGenerator.success(rooms);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }


    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity send(@RequestBody SendMessageDto request, Principal principal) {
        try {
            // create new private chat room
            if (request.getRoomId() < 0) {
                CreateRoomRequest newReq = new CreateRoomRequest();
                newReq.message = request;
                newReq.UserId = request.getUserId();
                return createPrivateRoom(newReq, principal);
            } else {
                AppUser user = userService.findByUsername(principal.getName());
                Message replyToMessage = null;
                if (request.getReplyTo() != null && request.getReplyTo() > 0) {
                    replyToMessage = messageService.findOne(request.getReplyTo());
                }
                Message message = new Message();
                message.setUser(user);
                message.setUserId(user.getId());
                message.setRoomId(request.getRoomId());
                MessageType messageType = request.getType();
                message.setMessageType(request.getType());
                message.setDeleted(false);
                message.setCaption(request.getCaption());
                message.setTimeStamp(MyArgUtils.nowEpoch());
                message.setReplyToId(request.getReplyTo());
                message.setMessageType(request.getType());
                message.setVisitCount(0L);
                message.setSeen(false);

                if (messageType == MessageType.TEXT) {
                    message.setBody(request.getBody());
                } else {
                    String body = documentService.SaveChatFile(request.getFile(), request.getType());
                    message.setBody(body);
                }

                Message res = messageService.save(message);

                Room room = roomService.findOne(message.getRoomId());
                RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

                if (room.getType() == RoomType.PRIVATE) {
                    messageService.updatePrivateSeen(room.getId(), roomUser.getLastSeenId(), message.getId(), user.getId());
                } else if (room.getType() == RoomType.GROUP) {
                    messageService.UpdateGroupVisit(user.getId(), MyArgUtils.nowEpoch(), room.getId(), roomUser.getLastSeenId(), message.getId());
                } else if (room.getType() == RoomType.CHANNEL) {
                    messageService.updateChannelVisitCount(room.getId(), roomUser.getLastSeenId(), message.getId(), user.getId());
                }

                // update user lastSeenId
                roomUser.setLastSeenId(message.getId());
                roomUserService.save(roomUser);

                // update first message id. this occures once.
                if (room.getFirstMessageId() == 0) {
                    room.setFirstMessageId(message.getId());
                    roomService.save(room);
                }

                // update lastMessageId for room
                if (room.getLastMessageId() < message.getId()) {
                    room.setLastMessageId(message.getId());
                    roomService.save(room);
                }

                // broadcast message to members of the room
                SocketMessage socketMessage = new SocketMessage();
                socketMessage.setType(SocketMessageType.NEW_MESSAGE);
                message.setReply(replyToMessage);
                socketMessage.setMessage(new MessageDto(message));
                template.convertAndSend("/user/queue/room/" + room.getId(), socketMessage);

                MessageDto messageResponse = new MessageDto();
                messageResponse.setId(message.getId());
                return GenericResponseGenerator.success(messageResponse);
            }
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    /*
        fetch messages by id between id>lastSeenId and id<=lastMessageId
        param: roomId
    */
    @GetMapping(value = "/get-room-messages/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GetMessagesInRange(@PathVariable Long roomId, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Room room = roomService.findOne(roomId);
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);
            if (roomUser == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            // room is empty
            if (room.getLastMessageId() == 0) {
                return GenericResponseGenerator.success(new ArrayList<Message>());
            }

            // fetch 100 messages based on lastSeenId: <-- 50 message before --> lastSeenId <-- 50 message after -->
            // FIXME: change number of messages in production mode
            // TODO: remove body of deleted message. only id is required
            List<Message> m = messageService.findInRange(roomId, roomUser.getLastSeenId(), 20L);
            List<MessageDto> messages = m.stream().map(MessageDto::new).collect(toList());
            //log.info(messages.toString());
            return GenericResponseGenerator.success(messages);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }


    //get last seen message 50 previous message for room
    @GetMapping(value = "/get-room-previous-messages/{roomId}/{lastMessageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GetPreviousMessages(@PathVariable Long lastMessageId, @PathVariable Long roomId, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Room room = roomService.findOne(roomId);
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

            // if user is not in room or lastMessageId is invalid
            if (roomUser == null || lastMessageId <= room.getFirstMessageId()) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            List<Message> m = messageService.findInRangePrev(roomId, lastMessageId, 5L);
            List<MessageDto> messages = m.stream().map(MessageDto::new).collect(toList());
            return GenericResponseGenerator.success(messages);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    //get last seen message 50 next message for room
    @GetMapping(value = "/get-room-next-messages/{roomId}/{lastMessageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GetNextMessages(@PathVariable Long lastMessageId, @PathVariable Long roomId, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Room room = roomService.findOne(roomId);
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

            // if user is not in room or lastMessageId is invalid
            if (roomUser == null || lastMessageId >= room.getLastMessageId()) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            List<Message> m = messageService.findInRangeNext(roomId, lastMessageId, 5L);
            List<MessageDto> messages = m.stream().map(MessageDto::new).collect(toList());
            return GenericResponseGenerator.success(messages);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    /*
        set seen for messages <= seenId
        param: seenId
    */
    @GetMapping(value = "/seen-message/{seenId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity seen(@PathVariable Long seenId, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Message message = messageService.findOne(seenId);
            if (message == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            // seen message is for others message not user's self message
            if (message.getUserId().equals(user.getId())) {
                return GenericResponseGenerator.response(OperationResult.Fail);
            }

            Room room = roomService.findOne(message.getRoomId());
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

            Long oldSeenId = roomUser.getLastSeenId();

            // if room type = private
            if (room.getType() == RoomType.PRIVATE) {
                // update message list that seen
                messageService.updatePrivateSeen(room.getId(), roomUser.getLastSeenId(), seenId, user.getId());
            } else if (room.getType() == RoomType.GROUP) {
                messageService.UpdateGroupVisit(user.getId(), MyArgUtils.nowEpoch(), room.getId(), roomUser.getLastSeenId(), seenId);
            } else if (room.getType() == RoomType.CHANNEL) {
                messageService.updateChannelVisitCount(room.getId(), roomUser.getLastSeenId(), seenId, user.getId());
            }
            roomUser.setLastSeenId(seenId);
            roomUserService.save(roomUser);

            // broadcast message to members of the room
            SocketMessage socketMessage = new SocketMessage();
            socketMessage.setType(SocketMessageType.SEEN);
            MessageDto seenMessage = new MessageDto();
            UserDto seenUser = new UserDto();
            RoomDto seenRoom = new RoomDto();
            seenRoom.setLastSeenId(oldSeenId);
            seenRoom.setId(room.getId());
            seenUser.setId(message.getUserId());
            seenMessage.setId(message.getId());
            seenMessage.setRoomId(message.getRoomId());
            seenMessage.setUser(seenUser);
            seenMessage.setRoom(seenRoom);
            socketMessage.setMessage(seenMessage);
            template.convertAndSend("/user/queue/room/" + room.getId(), socketMessage);

            return GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            //log.info("seen error");
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    /*
        get all messages > messageId
        used for scrollToEnd in chat
        param: messageId
    */
    @GetMapping(value = "/get-room-messages-end/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllMessagesToEnd(@PathVariable Long messageId, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Message message = messageService.findOne(messageId);
            if (message == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            Room room = roomService.findOne(message.getRoomId());
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

            List<Message> m = messageService.findToEnd(room.getId(), messageId);
            List<MessageDto> messages = m.stream().map(MessageDto::new).collect(toList());

            // if room type = private
            if (room.getType() == RoomType.PRIVATE) {
                // update message list that seen
                messageService.updatePrivateSeen(room.getId(), roomUser.getLastSeenId(), room.getLastMessageId(), user.getId());
            } else if (room.getType() == RoomType.GROUP) {
                messageService.UpdateGroupVisit(user.getId(), MyArgUtils.nowEpoch(), room.getId(), roomUser.getLastSeenId(), room.getLastMessageId());
            } else if (room.getType() == RoomType.CHANNEL) {
                messageService.updateChannelVisitCount(room.getId(), roomUser.getLastSeenId(), room.getLastMessageId(), user.getId());
            }

            roomUser.setLastSeenId(room.getLastMessageId());
            roomUserService.save(roomUser);
            return GenericResponseGenerator.success(messages);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/visitors/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getVisitors(@PathVariable Long messageId, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Message message = messageService.findOne(messageId);
            if (message == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            Room room = roomService.findOne(message.getRoomId());
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

            // illegal access
            if (roomUser == null) {
                return GenericResponseGenerator.response(OperationResult.Fail);
            }

            List<UserMessage> messages = userMessageService.findVisitors(messageId);
            List<MessageVisitDto> users = messages.stream().map(MessageVisitDto::new).collect(toList());
            return GenericResponseGenerator.success(users);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PostMapping(value = "/send-file-message", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendFileMessage(@RequestBody SendMessageDto request, Principal principal) {
        try {
            log.info("message body " + request.getBody());
            AppUser user = userService.findByUsername(principal.getName());
            Message message = new Message();
            message.setUserId(user.getId());
            message.setRoomId(request.getRoomId());
            message.setMessageType(request.getType());
            message.setDeleted(false);
            message.setCaption(request.getCaption());
            message.setTimeStamp(MyArgUtils.nowEpoch());
            message.setReplyToId(request.getReplyTo());
            message.setMessageType(request.getType());
            message.setVisitCount(0L);
            message.setSeen(false);
            message.setBody(request.getBody());
            Message res = messageService.save(message);

            Room room = roomService.findOne(message.getRoomId());
            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(room, user);

            if (room.getType() == RoomType.PRIVATE) {
                messageService.updatePrivateSeen(room.getId(), roomUser.getLastSeenId(), message.getId(), user.getId());
            } else if (room.getType() == RoomType.GROUP) {
                messageService.UpdateGroupVisit(user.getId(), MyArgUtils.nowEpoch(), room.getId(), roomUser.getLastSeenId(), message.getId());
            } else if (room.getType() == RoomType.CHANNEL) {
                messageService.updateChannelVisitCount(room.getId(), roomUser.getLastSeenId(), message.getId(), user.getId());
            }

            // update user lastSeenId
            roomUser.setLastSeenId(message.getId());
            roomUserService.save(roomUser);

            // update lastMessageId for room
            if (room.getLastMessageId() < message.getId()) {
                room.setLastMessageId(message.getId());
                roomService.save(room);
            }
            MessageDto messageResponse = new MessageDto();
            messageResponse.setId(message.getId());
            return GenericResponseGenerator.success(messageResponse);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-user-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserList(Principal principal) {
        try {
            Iterable<AppUser> items = userService.findAll();
            List<UserDto> users = new ArrayList<>();
            log.info(users.toString());
            items.forEach(x -> users.add(new UserDto(x)));
            return GenericResponseGenerator.success(users);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PostMapping(value = "/create-private-room", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createPrivateRoom(@RequestBody CreateRoomRequest request, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            AppUser sideUser = userService.findOne(request.UserId);

            Room room = new Room();
            room.setType(RoomType.PRIVATE);
            room.setLastMessageId(0L);
            room.setFirstMessageId(0L);
            room.setUrl(sideUser.getAvatar());
            room.setName(user.getUsername() + "-" + sideUser.getUsername());
            room.setCreateTime(MyArgUtils.nowEpoch());
            room.setDescription("private room");
            room.setDeleted(false);
            room.setOwnerId(user.getId());
            room.setUserCanLeave(false);
            Room createdRoom = roomService.save(room);

            if (createdRoom == null)
                return GenericResponseGenerator.error("error in create room");


            //add owner private user
            RoomUser ownerRoomUser = new RoomUser();
            ownerRoomUser.setUser(user);
            ownerRoomUser.setRoom(createdRoom);
            ownerRoomUser.setLastSeenId(0L);
            roomUserService.save(ownerRoomUser);


            //add owner private user
            RoomUser sideRoomUser = new RoomUser();
            sideRoomUser.setUser(sideUser);
            sideRoomUser.setRoom(createdRoom);
            sideRoomUser.setLastSeenId(0L);
            roomUserService.save(sideRoomUser);


            Message message = new Message();
            message.setUserId(user.getId());
            message.setUser(user);
            message.setRoomId(createdRoom.getId());
            message.setDeleted(false);
            message.setCaption(request.message.getCaption());
            message.setTimeStamp(MyArgUtils.nowEpoch());
            message.setReplyToId(request.message.getReplyTo());
            message.setMessageType(request.message.getType());
            message.setVisitCount(0L);
            message.setSeen(false);
            message.setBody(request.message.getBody());
            Message savedMessage = messageService.save(message);

            RoomUser roomUser = roomUserService.findFirstByRoomAndUser(createdRoom, user);
            messageService.updatePrivateSeen(room.getId(),
                    roomUser.getLastSeenId(),
                    message.getId(),
                    user.getId());


            // update user lastSeenId
            roomUser.setLastSeenId(message.getId());
            roomUserService.save(roomUser);

            // update lastMessageId for room
            if (room.getLastMessageId() < message.getId()) {
                room.setLastMessageId(message.getId());
                roomService.save(room);
            }

            RoomDto roomDto = new RoomDto();
            roomDto.setImage(createdRoom.getUrl());
            roomDto.setName(sideUser.getFullName());
            roomDto.setId(createdRoom.getId());
            roomDto.setType(createdRoom.getType());
            roomDto.setLastMessage(new MessageDto(savedMessage));
            roomDto.setMute(false);
            roomDto.setUser(new UserDto(sideUser));
            roomDto.setOwnerId(user.getId());
            roomDto.setCreateTime(createdRoom.getCreateTime());
            roomDto.setFirstMessageId(savedMessage.getId());
            roomDto.setLastMessageId(savedMessage.getId());
            roomDto.setDescription(createdRoom.getDescription());
            roomDto.setLastSeenId(0L);
            roomDto.setUnreadMessageCount(0L);
            roomDto.setUserCanLeave(false);

            // prepare socket message
            SocketMessage socketMessage = new SocketMessage();
            socketMessage.setType(SocketMessageType.ADD_TO_ROOM);
            socketMessage.setRoom(roomDto);

            // notify user
            template.convertAndSend("/user/queue/" + user.getId(), socketMessage);

            // notify side user
            roomDto.setImage(user.getAvatar());
            roomDto.setName(user.getFullName());
            roomDto.setId(createdRoom.getId());
            roomDto.setType(createdRoom.getType());
            roomDto.setLastMessage(new MessageDto(savedMessage));
            roomDto.setMute(false);
            roomDto.setUser(new UserDto(user));
            roomDto.setOwnerId(user.getId());
            roomDto.setCreateTime(createdRoom.getCreateTime());
            roomDto.setFirstMessageId(savedMessage.getId());
            roomDto.setLastMessageId(savedMessage.getId());
            roomDto.setDescription(createdRoom.getDescription());
            roomDto.setLastSeenId(0L);
            roomDto.setUnreadMessageCount(1L);
            roomDto.setUserCanLeave(false);

            socketMessage.setRoom(roomDto);

            template.convertAndSend("/user/queue/" + sideUser.getId(), socketMessage);

            return GenericResponseGenerator.success(roomDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }


    @GetMapping(value = "/room-members/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createPrivateRoom(@PathVariable Long roomId){
        try {

            Room room = roomService.findOne(roomId);
            if(room == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            List<RoomUser> roomUsers = room.getRoomUsers();
            List<UserDto> users = roomUsers.stream()
                    .map(x -> new UserDto(x.getUser()))
                    .collect(toList());

            return GenericResponseGenerator.success(users);

        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PostMapping(value = "/change-room-mute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity changeRoomMute(@RequestBody UserRoomDto request, Principal principal){
        try {
            Room room = roomService.findOne(request.roomId);
            AppUser user = userService.findByUsername(principal.getName());
            if(room == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            RoomUser userRoom = roomUserService.findFirstByRoomAndUser(room, user);
            if(userRoom == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            userRoom.setMute(request.mute);
            RoomUser newState = roomUserService.save(userRoom);
            return GenericResponseGenerator.success(newState.isMute());

        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PostMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRoomInfo(@RequestBody UserRoomDto request, Principal principal){
        try {
            if (request.room != null) {
                Room room = roomService.findOne(request.room.getId());
                AppUser user = userService.findByUsername(principal.getName());
                if (room == null)
                    return GenericResponseGenerator.response(OperationResult.NotFound);
                RoomUser userRoom = roomUserService.findFirstByRoomAndUser(room, user);
                if (userRoom == null || user.getAdmin() == false)
                    return GenericResponseGenerator.response(OperationResult.NotFound);

                if (request.room.getFile() != null) {
                    byte[] data = documentService.Base64ToByte(request.room.getFile().data);
                    String fileName = String.format("%s.%s",
                            new Random()
                                    .nextInt(15000000),
                            FilenameUtils.getExtension(request.room.getFile().name));
                    String fileUrl = documentService.saveImageFile(FolderPath.USER_PROFILE, fileName, data);
                    if (fileUrl.equals("")) {
                        return GenericResponseGenerator.response(OperationResult.Fail);
                    }
                    room.setUrl(fileUrl);
                }
                room.setName(request.room.getName());
                room.setDescription(request.room.getDescription());
                roomService.save(room);
                return GenericResponseGenerator.success(new UserRoomDto(new RoomDto(room)));
            } else if (request.user != null) {
                AppUser user = userService.findByUsername(principal.getName());
                AppUser editingUser = userService.findOne(request.user.getId());
                if (user != editingUser) {
                    return GenericResponseGenerator.response(OperationResult.NotFound);
                }

                if (request.user.getFile() != null) {
                    byte[] data = documentService.Base64ToByte(request.user.getFile().data);
                    String fileName = String.format("%s.%s",
                            new Random()
                                    .nextInt(15000000),
                            FilenameUtils.getExtension(request.user.getFile().name));
                    String fileUrl = documentService.saveImageFile(FolderPath.USER_PROFILE, fileName, data);
                    if (fileUrl.equals("")) {
                        return GenericResponseGenerator.response(OperationResult.Fail);
                    }
                    editingUser.setAvatar(fileUrl);
                }
                editingUser.setFullName(request.user.getName());
                userService.save(editingUser, false);
                return GenericResponseGenerator.success(new UserRoomDto(new UserDto(editingUser)));
            }

            return GenericResponseGenerator.error("Request has not valid data");

        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/room-media-messages/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity roomMediaMessages(@PathVariable Long roomId){
        try {
            Room room = roomService.findOne(roomId);
            if(room == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            List<Message> items = messageService.findMediaMessage(roomId);
            List<MessageDto> messages = items.stream()
                    .map(MessageDto::new)
                    .collect(toList());

            return GenericResponseGenerator.success(messages);

        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }
}
