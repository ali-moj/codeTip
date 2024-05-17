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
@Table(name = "login_histories")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    private Long loginTime;

    @Column(name = "user_id" )
    private Long userId;

    @ManyToOne
    @JoinColumn(name="user_id" , updatable = false , insertable = false)
    private AppUser user;
}