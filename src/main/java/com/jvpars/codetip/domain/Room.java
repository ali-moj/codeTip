package com.jvpars.codetip.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jvpars.codetip.domain.enumitem.RoomType;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Version
    @Column(name = "version")
    private Integer version;


    private RoomType type;
    private String name;
    @Lob
    private String description;
    private String url;
    private Long createTime ;
    private Long lastMessageId;
    private boolean userCanLeave;
    private Long firstMessageId;
    private Boolean deleted;
    @Column(name = "owner_id")
    private Long ownerId;


    @ManyToOne
    @JoinColumn(name="owner_id", updatable = false , insertable = false)
    private AppUser owner;
    @JsonIgnore
    @OneToMany(mappedBy = "room" , cascade = CascadeType.ALL)
    private List<RoomUser> roomUsers = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private List<Message> messages = new ArrayList<>();



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
