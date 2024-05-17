package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.ProjectTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository  extends CrudRepository<ProjectTask,Long> {
    Page<ProjectTask> findAll(Pageable pageable);
    List<ProjectTask> findAllByUser(AppUser user);
    List<ProjectTask>findAllByStatus(EntityStatus status);
    List<ProjectTask>findAllByTitle(String title);
    ProjectTask findFirstByTitle(String title);
    List<ProjectTask> findAllByCard(ProjectCard card);
    List<ProjectTask> findAllByCardOrderByArrange(ProjectCard card);
    List<ProjectTask> findAllByUserAndStatus(AppUser user, EntityStatus status);
}
