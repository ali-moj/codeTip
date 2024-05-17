package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.repository.ProjectCardRepository;
import com.jvpars.codetip.service.api.ProjectCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProjectCardServiceImpl implements ProjectCardService {

    @Autowired
    ProjectCardRepository repository;

    @Override
    @Transactional(readOnly = false)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Iterable<ProjectCard> findAll() {
        return repository.findAll();
    }

    @Override
    public ProjectCard findOne(Long id) {
        Optional<ProjectCard> item = repository.findById(id);
        return item.orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public ProjectCard save(ProjectCard item) {
        return repository.save(item);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<ProjectCard> findAllByProject(Project project) {
        return repository.findAllByProject(project);
    }
}
