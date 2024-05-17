package com.jvpars.codetip.domain.enumitem;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum RoomType {
    PRIVATE, CHANNEL, GROUP;

    public static RoomType get(int type) {
        switch (type) {
            case 0:
                return PRIVATE;
            case 1:
                return CHANNEL;
            default:
                return GROUP;
        }

    }
}
