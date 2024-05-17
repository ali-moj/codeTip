package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectService {
    @Transactional(readOnly = false)
    Project save(Project project);


    @Transactional(readOnly = false)
    void delete(Long id);

    Project findOne(Long id);

    Iterable<Project> findAll();

    Page<Project> findAll(Pageable pageable);

   List<Project> findAllByUsers(AppUser user);

   List<Project> findAllByTitle(String title);

   Project findFirstByTitle(String title);

   List<Project> findAllByStatus(EntityStatus status);

    long count();
}
