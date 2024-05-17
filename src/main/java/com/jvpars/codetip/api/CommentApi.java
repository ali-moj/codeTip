package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.*;
import com.jvpars.codetip.dto.CommentDto;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.service.api.CommentService;
import com.jvpars.codetip.service.api.TaskService;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/comments")
@Slf4j
public class CommentApi {


    private final CommentService service;
    private final TaskService taskService;
    private final AppUserService userService;

    @Autowired
    public CommentApi(CommentService service, TaskService taskService, AppUserService userService) {
        this.service = service;
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping(value = "/get-all" , produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAll() {
        try {
            Iterable<TaskComment> res = service.findAll();
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/paged-list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity list(Pageable pageable) {
        try {

            Page<TaskComment> comments = service.findAll(pageable);
            List<TaskComment> items = new ArrayList<>();
            for (TaskComment comment : comments) {
                items.add(comment);
            }


            long total = comments.getTotalElements();
            int pageCount = comments.getTotalPages();
            return  GenericResponseGenerator.pageable(total , pageCount , items);
        } catch (Exception ex) {
            return GenericResponseGenerator.pageableError(ex.getMessage());
        }
    }

    @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity delete(@RequestBody Long id) {
        try {

            service.delete(id);
            return  GenericResponseGenerator.success(true);

        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity save(@RequestBody CommentDto dto, Principal principal) {
        try {
            ProjectTask task = taskService.findOne(dto.taskId);
            AppUser user = userService.findByUsername(principal.getName());
            TaskComment comment;
            if(dto.id!=null){
                comment = service.findOne(dto.id);
            }
            else {
                 comment = new TaskComment();
            }
            comment.setBody(dto.body);

            comment.setCreateDate(new Date().getTime());
            comment.setTaskId(task.getId());
            comment.setTask(task);
            comment.setUser(user);
            comment.setUserId(user.getId());
            TaskComment res = service.save(comment);
            return  GenericResponseGenerator.success(new CommentDto(res));
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-all-by-user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByUser(@RequestBody AppUser user) {
        try {
            List<TaskComment> res = service.findAllByUsers(user);
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-all-by-task/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByTask(@PathVariable Long id) {
        try {
            ProjectTask task = taskService.findOne(id);
            if(task == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            List<TaskComment> comments = service.findAllByTask(task);
            List<CommentDto> res = comments.stream().map(x -> new CommentDto(x)).collect(Collectors.toList());
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity update(@RequestBody TaskComment comment, Principal principal) {
        try {
            TaskComment old = service.findOne(comment.getId());
            if(old == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            // not authorized
            System.out.println("username " + principal.getName());
            System.out.println("comment username " + old.getUser().getUsername());
            if(!old.getUser().getUsername().equals(principal.getName())) {
                return GenericResponseGenerator.error("Forbidden");
            }

            old.setUser(old.getUser());
            old.setBody(comment.getBody());
            service.save(old);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }
}






