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
@Table(name = "task_comments")
public class TaskComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;


    @Column(name = "user_id")
    private Long userId;
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne
    @JoinColumn(name="user_id", updatable = false , insertable = false)
    private AppUser user;


    @ManyToOne
    @JoinColumn(name="task_id", updatable = false , insertable = false)
    private ProjectTask task;

    @Column(name = "create_date")
    private Long createDate;

    private String body;

}
