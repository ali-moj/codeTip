package com.jvpars.codetip.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_cards")
public class ProjectCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "project_id")
    private Long projectId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="project_id" , updatable = false , insertable = false)
    private Project project;

    @JsonIgnore
    @OneToMany(mappedBy = "card", cascade = CascadeType.REMOVE)
    private List<ProjectTask> tasks = new ArrayList<>();

    private String title;

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
