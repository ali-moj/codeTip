package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    @Transactional(readOnly = false)
    TaskComment save(TaskComment comment);


    @Transactional(readOnly = false)
    void delete(Long id);

    Iterable<TaskComment> findAll();

    Page<TaskComment> findAll(Pageable pageable);

    List<TaskComment> findAllByUsers(AppUser user);

    List<TaskComment>findAllByTask(ProjectTask task);

    TaskComment findOne(Long id);


    long count();
}
