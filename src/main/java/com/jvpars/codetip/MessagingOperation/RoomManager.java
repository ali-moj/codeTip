package com.jvpars.codetip.MessagingOperation;

import com.jvpars.codetip.cache.CacheApi;
import com.jvpars.codetip.cache.CacheMem;
import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.RoomUser;
import com.jvpars.codetip.repository.AppUserRepository;
import com.jvpars.codetip.repository.RoomRepository;
import com.jvpars.codetip.repository.RoomUserRepository;
import com.jvpars.codetip.utils.MyArgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;


@Component
@Slf4j
public class RoomManager {

    @Autowired
    CacheApi cacheApi;

    @Autowired
    RoomUserRepository roomUserRepository;

    @Autowired
    AppUserRepository appUserRepository;


    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userName = event.getUser().getName();
        String ssid = headerAccessor.getSessionAttributes().get("sessionId").toString();
        log.warn(headerAccessor.getDestination());
   //     registerSSid(ssid,userName);

    }

    //..............................................

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String ssid = headerAccessor.getSessionAttributes().get("sessionId").toString();
        CacheMem cacheMem = cacheApi.findBySsid(ssid);
        log.warn("DiSCONNECT SSID :{}",ssid);
        cacheApi.delete(cacheMem);
        log.warn(" ROOM MANAGER CACHE COUNT:{}",cacheApi.count());
    }

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userName = event.getUser().getName();
        String ssid = headerAccessor.getSessionAttributes().get("sessionId").toString();
        log.info("handleConnectEvent: username="+userName+", event="+event+" ,headerAccessor "+ssid);
        registerSSid(ssid,userName);
    }


   public void registerSSid(String ssid,String userName){
        CacheMem cacheMem;
        cacheMem =cacheApi.findBySsid(ssid);
        if(cacheMem==null)
        cacheMem=new CacheMem();
        cacheMem.setName(userName);
        cacheMem.setSsid(ssid);
       AppUser user = appUserRepository.findFirstByUsername(userName);
       List<RoomUser> roomsOfUser = roomUserRepository.findAllByUser(user);
       for(RoomUser room:roomsOfUser){
           cacheMem.setRoomId(room.getRoom().getId());
           cacheApi.save(cacheMem);
       }

        log.warn(" ROOM MANAGER CACHE COUNT:{} , user :{} , ssid :{}",cacheApi.count(),userName,ssid );
    }
}