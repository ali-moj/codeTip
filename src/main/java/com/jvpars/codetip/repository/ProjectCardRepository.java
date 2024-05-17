package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.ProjectCard;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectCardRepository  extends CrudRepository<ProjectCard, Long> {
    List<ProjectCard> findAllByProject(Project project);
}
