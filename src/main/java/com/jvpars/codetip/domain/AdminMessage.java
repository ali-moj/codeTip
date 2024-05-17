package com.jvpars.codetip.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "admin_messages")
public class AdminMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    private String body;

    private Long time;

    @Column(name = "user_id")
    private Long userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id"  , updatable = false , insertable = false)
    private AppUser user;

    public AdminMessage(AdminMessage message) {
        body = message.body;
        time = message.time;
        user = message.user;
        id = message.id;
    }
}
