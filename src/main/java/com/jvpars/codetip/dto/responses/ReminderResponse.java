package com.jvpars.codetip.dto.responses;

import com.jvpars.codetip.domain.ReminderEvent;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ReminderResponse {

    public ReminderResponse() {
    }

    public ReminderResponse(ReminderEvent item) {
        id = item.getId();
        userId = item.getUser().getId();
        title = item.getTitle();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        date = formatter.format( new Date(item.getDate()));
        type = item.getType();
        description = item.getDescription();
    }

    public Long id;
    public Long userId;
    public String title;
    public String date;
    public String description;
    public int type;
}
