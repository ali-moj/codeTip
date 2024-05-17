package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.TaskComment;
import com.jvpars.codetip.domain.ProjectTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository  extends CrudRepository<TaskComment,Long>
{
    Page<TaskComment> findAll(Pageable pageable);
    List<TaskComment> findAllByUser(AppUser user);
    List<TaskComment>findAllByTask(ProjectTask task);
    TaskComment findFirstById(Long id);
}
