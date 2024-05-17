package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.ProjectTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskService {
    @Transactional(readOnly = false)
    ProjectTask save(ProjectTask task);


    @Transactional(readOnly = false)
    void delete(Long id);

    Iterable<ProjectTask> findAll();

    Page<ProjectTask> findAll(Pageable pageable);

    List<ProjectTask> findAllByUsers(AppUser user);
    List<ProjectTask> findAllByUserAndStatus(AppUser user, EntityStatus status);

    List<ProjectTask> findAllByStatus(EntityStatus status);
    List<ProjectTask> findAllByCard(ProjectCard card);
    List<ProjectTask> findAllByCardOrderByArrange(ProjectCard card);

    List<ProjectTask> findAllByTitle(String title);
    ProjectTask findFirstByTitle(String title);
    ProjectTask findOne(Long id);


    long count();
}
