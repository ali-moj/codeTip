package com.jvpars.codetip.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jvpars.codetip.domain.enumitem.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Version
    @Column(name = "version")
    private Integer version;


    private MessageType messageType;
    private Long timeStamp;
    @Lob
    private String body;
    private Long visitCount;
    private String caption;
    private boolean seen;
    private Boolean deleted;
    @Column(name = "room_id")
    private Long roomId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "reply_to_id")
    private Long replyToId;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "reply_to_id", updatable = false, insertable = false)
    private Message reply;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "room_id", updatable = false, insertable = false)
    private Room room;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private AppUser user;

    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind
                    .ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
