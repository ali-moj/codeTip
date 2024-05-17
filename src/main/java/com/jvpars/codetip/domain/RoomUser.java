package com.jvpars.codetip.domain;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "room_users")
@IdClass(RoomUserId.class)
public class RoomUser  {


    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Id
    @ManyToOne
    @JoinColumn(name = "room_id" )
    private Room room;

    private Long lastSeenId;
    private boolean mute;

}


