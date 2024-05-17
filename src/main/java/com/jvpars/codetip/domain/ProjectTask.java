package com.jvpars.codetip.domain;

import com.jvpars.codetip.domain.enumitem.EntityStatus;
import javafx.concurrent.Task;
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
@Table(name = "project_tasks")
public class ProjectTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    private String title;

    @Lob
    private String description;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "card_id")
    private Long cardId;

    @ManyToOne
    @JoinColumn(name="card_id" , updatable = false , insertable = false)
    private ProjectCard card;

    @ManyToOne
    @JoinColumn(name="user_id" , updatable = false , insertable = false)
    private AppUser user;

    private Long startDate;

    private Long endDate;

    private EntityStatus status;

    private Long arrange;
}
