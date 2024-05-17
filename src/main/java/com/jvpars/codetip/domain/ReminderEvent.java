package com.jvpars.codetip.domain;

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
@Table(name = "reminder_events")
public class ReminderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "user_id")
    private Long userId;


    @ManyToOne
    @JoinColumn(name = "user_id" , updatable = false , insertable = false)
    private AppUser user;


    private String title;

    private Long date;

    private String description;

    private int type;

}
