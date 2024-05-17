package com.jvpars.codetip.api;


import com.jvpars.codetip.cache.CacheApi;

import com.jvpars.codetip.service.api.RoomService;
import com.jvpars.codetip.service.api.RoomUserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.messaging.simp.annotation.SubscribeMapping;

import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@Controller
@Slf4j
public class MessageApi {

    private final SimpMessagingTemplate template;
    private CacheApi cacheApi;
    private RoomService roomRepository ;
    private RoomUserService roomUserRepository;

    @Autowired
    public MessageApi(SimpMessagingTemplate template,CacheApi cacheApi,RoomUserService roomUserRepository,RoomService roomRepository) {
        this.template = template;
        this.cacheApi=cacheApi;
        this.roomUserRepository=roomUserRepository;
        this.roomRepository=roomRepository;
    }

    private static final String SENDING_URL = "/queue/reply";


    private AtomicLong counter = new AtomicLong(0);

    private String message = "";

    @SubscribeMapping(SENDING_URL)
    public String onSubscribe() {
        log.info("connecting..");
        return "Connection established .. " ;
    }

    //SEND TO ALL
  /*@Scheduled(fixedRate = 2000)
    public void sendMessage() {
        template.convertAndSend("",SENDING_URL, buildNextMessage());
    }*/

    private String buildNextMessage() {
        message = "Message no " + counter.getAndIncrement();
        System.out.println("Send message " + message);
        return message;
    }
    //...................
}
