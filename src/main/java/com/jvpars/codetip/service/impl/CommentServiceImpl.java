package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.TaskComment;
import com.jvpars.codetip.domain.ProjectTask;
import com.jvpars.codetip.repository.CommentRepository;
import com.jvpars.codetip.service.api.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private CommentRepository repository;

    @Autowired
    public CommentServiceImpl(CommentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    @Override
    public TaskComment save(TaskComment comment) {
        return repository.save(comment);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
repository.deleteById(id);
    }

    @Override
    public Iterable<TaskComment> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<TaskComment> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<TaskComment> findAllByUsers(AppUser user) {
        return repository.findAllByUser(user);
    }

    @Override
    public List<TaskComment> findAllByTask(ProjectTask task) {
        return repository.findAllByTask(task);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public TaskComment findOne(Long id) { return repository.findFirstById(id); }
}
