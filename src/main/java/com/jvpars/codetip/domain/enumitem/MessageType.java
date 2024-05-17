package com.jvpars.codetip.domain.enumitem;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum MessageType {
    TEXT, VOICE, VIDEO, FILE, PICTURE;

    public static MessageType get(int type) {
        switch (type) {
            case 1:
                return VOICE;
            case 2:
                return VIDEO;
            case 3:
                return FILE;
            case 4:
                return PICTURE;
            default:
                return TEXT;

        }
    }
}
