package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.AdminMessage;

import java.util.ArrayList;
import java.util.List;

public class AdminMessageDto {
    public Long id;

    public String body;

    public
    Long time;

    public AdminMessageDto(AdminMessage message) {
        this.id = message.getId();
        this.body = message.getBody();
        this.time = message.getTime();
    }

    public AdminMessageDto() {
    }

    public static List<AdminMessageDto> list(Iterable<AdminMessage> items) {
        List<AdminMessageDto> list = new ArrayList<>();
        for (AdminMessage item : items)
            list.add(new AdminMessageDto(item));
        return list;
    }
}
