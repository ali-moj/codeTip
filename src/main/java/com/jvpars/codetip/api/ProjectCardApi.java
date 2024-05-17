package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Project;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.ProjectTask;
import com.jvpars.codetip.dto.ProjectCardDto;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.ProjectTaskDto;
import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.service.api.ProjectCardService;
import com.jvpars.codetip.service.api.ProjectService;
import com.jvpars.codetip.service.api.TaskService;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/project-cards")

public class ProjectCardApi {

    private final Logger log = LoggerFactory.getLogger(ProjectCardApi.class);
    ProjectCardService service;
    ProjectService projectService;
    TaskService taskService;
    AppUserService userService;


    
    @Autowired
    public ProjectCardApi(ProjectCardService service , ProjectService projectService, TaskService taskService, AppUserService userService) {
        this.service = service;
        this.projectService = projectService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping(value = "/get-all",  produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAll() {
        try {
            Iterable<ProjectCard> res = service.findAll();
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-by-project/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getByProject(@PathVariable Long id, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            Project proj = projectService.findOne(id);
            if(proj == null || (!user.getAdmin() && !proj.getUsers().contains(user))) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            List<ProjectCard> cards = service.findAllByProject(proj);
            List<ProjectCardDto> res = cards.stream().map(x -> new ProjectCardDto(x)).collect(Collectors.toList());

            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity save(@RequestBody ProjectCard request) {
        try {

            Project project = projectService.findOne(request.getProjectId());
            if(project == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            ProjectCard card = new ProjectCard();
            card.setTitle(request.getTitle());
            card.setProjectId(project.getId());
            ProjectCard res = service.save(card);
            return  GenericResponseGenerator.success(new ProjectCardDto(res));
        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/update",  produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity update(@RequestBody ProjectCard card) {
        try {
            ProjectCard old = service.findOne(card.getId());
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            old.setTitle(card.getTitle());

             service.save(card);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity delete(@PathVariable Long id) {
        try {
            ProjectCard old = service.findOne(id);
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            service.delete(id);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

}
