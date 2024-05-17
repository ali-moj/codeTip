package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.ProjectTask;
import com.jvpars.codetip.repository.TaskRepository;
import com.jvpars.codetip.service.api.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {
    private TaskRepository repository;

    @Autowired
    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    @Override
    public ProjectTask save(ProjectTask task) {
        return repository.save(task);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Iterable<ProjectTask> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<ProjectTask> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<ProjectTask> findAllByUsers(AppUser user) {
        return repository.findAllByUser(user);
    }

    @Override
    public List<ProjectTask> findAllByUserAndStatus(AppUser user, EntityStatus status) { return repository.findAllByUserAndStatus(user, status); }

    @Override
    public List<ProjectTask> findAllByStatus(EntityStatus status) {
        return repository.findAllByStatus(status);
    }

    @Override
    public List<ProjectTask> findAllByCard(ProjectCard card) {
        return repository.findAllByCard(card);
    }

    @Override
    public List<ProjectTask> findAllByCardOrderByArrange(ProjectCard card) { return repository.findAllByCardOrderByArrange(card); }


    @Override
    public List<ProjectTask> findAllByTitle(String title) {
        return repository.findAllByTitle(title);
    }

    @Override
    public ProjectTask findFirstByTitle(String title) {
        return repository.findFirstByTitle(title);
    }

    @Override
    public ProjectTask findOne(Long id) {
        Optional<ProjectTask> item = repository.findById(id);
        return item.orElse(null);
    }

    @Override
    public long count() {
        return repository.count();
    }
}
