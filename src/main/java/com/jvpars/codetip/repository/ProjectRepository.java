package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project,Long> {
    Page<Project> findAll(Pageable pageable);
    List<Project> findAllByUsers(AppUser user);
    List<Project> findAllByTitle(String title);
    Project findFirstByTitle(String title);
    List<Project> findAllByStatus(EntityStatus status);



}
