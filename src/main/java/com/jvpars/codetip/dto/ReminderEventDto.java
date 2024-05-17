package com.jvpars.codetip.dto;

import com.jvpars.codetip.domain.ReminderEvent;

public class ReminderEventDto {
    private Long id;
    private Long userId;
    private Long date;
    private String description;
    private int type;

    public ReminderEventDto() {}

    public ReminderEventDto(ReminderEvent event) {
        id = event.getId();
        userId = event.getUserId();
        date = event.getDate();
        description = event.getDescription();
        type = event.getType();
    }
}
