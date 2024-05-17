package com.jvpars.codetip.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jvpars.codetip.dto.requests.FileRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_users")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Version
    @Column(name = "version")
    private Integer version;

    @Column(unique = true)
    private String username;
    private String password;
    private String fullName;
    private String avatar;
    private Boolean admin;
    private Boolean deleted;

    @Transient
    private String token;


    @JsonIgnore
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    private List<RoomUser> roomUsers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Message> messages = new ArrayList<>();

    @Transient
    public FileRequest file;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();


    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    public List<Project> projects;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
