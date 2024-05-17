package com.jvpars.codetip.service.api;
import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.ProjectCard;

import java.util.List;


public interface ProjectCardService {

    void delete(Long id);
    Iterable<ProjectCard> findAll();
    ProjectCard findOne(Long id);
    ProjectCard save(ProjectCard item);
    long count();
    List<ProjectCard> findAllByProject(Project project);
}
