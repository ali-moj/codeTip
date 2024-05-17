package com.jvpars.codetip.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_messages")
@IdClass(UserMessageId.class)
public class UserMessage {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Id
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    private Long timestamp;

    private Long roomId;
}
