package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.repository.ProjectRepository;
import com.jvpars.codetip.service.api.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private ProjectRepository repository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    @Override
    public Project save(Project project) {
        return  repository.save(project);
    }
    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {

        repository.deleteById(id);

    }

    @Override
    public Project findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Project> findAll() {
       return repository.findAll();
    }

    @Override
    public Page<Project> findAll(Pageable pageable) {
        return  repository.findAll(pageable);
    }

    @Override
    public List<Project> findAllByUsers(AppUser user) {
        return repository.findAllByUsers(user);
    }

    @Override
    public List<Project> findAllByTitle(String title) {
        return repository.findAllByTitle(title);
    }

    @Override
    public Project findFirstByTitle(String title) {
        return repository.findFirstByTitle(title);
    }

    @Override
    public List<Project> findAllByStatus(EntityStatus status) {
        return repository.findAllByStatus(status);
    }

    @Override
    public long count() {
        return repository.count();
    }
}
