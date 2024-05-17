package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.*;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.dto.GenericPageableResponse;
import com.jvpars.codetip.dto.GenericResponse;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.ProjectDto;
import com.jvpars.codetip.service.api.*;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import com.jvpars.codetip.utils.MyArgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;
import sun.util.resources.cldr.ga.LocaleNames_ga;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/projects")
@Slf4j
public class ProjectApi {
    private final ProjectService service;
    private final AppUserService userService;

    @Autowired
    public ProjectApi(ProjectService service, AppUserService userService) {
        this.service = service;
        this.userService = userService;
    }


    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAll() {
        try {


            Iterable<Project> res = service.findAll();
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/paged-list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity list(Pageable pageable) {
        try {

            Page<Project> Projects = service.findAll(pageable);
            List<Project> items = new ArrayList<>();
            for (Project Project : Projects) {
                items.add(Project);
            }

            long total = Projects.getTotalElements();
            int pageCount = Projects.getTotalPages();
            return  GenericResponseGenerator.pageable(total , pageCount , items);
        } catch (Exception ex) {
            return GenericResponseGenerator.pageableError(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity delete(@PathVariable Long id) {
        try {
            Project old = service.findOne(id);
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            service.delete(id);
            return  GenericResponseGenerator.success(true);

        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/save",produces = "application/json")
    ResponseEntity save(@RequestBody ProjectDto request) {
        try {
            Project proj = new Project();
            for(Long userId: request.getUsersRequest()) {
                proj.getUsers().add(userService.findOne(userId));
            }
            proj.setTitle(request.getTitle());
            proj.setDescription(request.getDescription());
            proj.setEndDate(request.getEndDate());
            proj.setStartDate(request.getStartDate());
            proj.setStatus(request.getStatus());
            Project res = service.save(proj);
            return  GenericResponseGenerator.success(new ProjectDto(res));
        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }


    @GetMapping(value = "/get-all-by-user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByUser(@PathVariable Long id) {
        try {
            AppUser user = userService.findOne(id);
            if(user == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            List<Project> res = service.findAllByUsers(user);
            log.warn(res.size()+"<<<<<<<<<<<<");
            //log.info(res.stream().map(Object::toString).collect(Collectors.joining(", ")));
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/user-projects", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getUserProjects(Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            if(user == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            List<ProjectDto> res = new ArrayList<>();
            if (user.getAdmin()) {
                Iterable<Project> temp = service.findAll();
                List<Project> projects = new ArrayList<>();
                service.findAll().forEach(x -> projects.add(x));
                res = projects.stream().map(x -> new ProjectDto(x)).collect(Collectors.toList());
            } else {
                List<Project> temp = service.findAllByUsers(user);
                res = temp.stream().map(x -> new ProjectDto(x)).collect(Collectors.toList());
            }

            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }


    @GetMapping(value = "/get-all-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByTitle(@RequestBody String title) {
        try {

            List<Project> res = service.findAllByTitle(title);
            return  GenericResponseGenerator.success(1);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getByTitle(@RequestBody String title) {
        try {

            Project res = service.findFirstByTitle(title);
            return  GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-all-by-Status", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByStatus(@RequestBody EntityStatus status) {
        try {

            List<Project> res = service.findAllByStatus(status);
            return  GenericResponseGenerator.success(1);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity update(@RequestBody ProjectDto project) {
        try {
            Project old = service.findOne(project.getId());
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            old.getUsers().clear();
            for(Long userId: project.getUsersRequest()) {
                old.getUsers().add(userService.findOne(userId));
            }
            old.setTitle(project.getTitle());
            old.setDescription(project.getDescription());
            old.setEndDate(project.getEndDate());
            old.setStartDate(project.getStartDate());
            old.setStatus(project.getStatus());
            service.save(old);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }
}

