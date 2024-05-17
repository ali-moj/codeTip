package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ProjectCard;
import com.jvpars.codetip.domain.enumitem.EntityStatus;
import com.jvpars.codetip.domain.ProjectTask;
import com.jvpars.codetip.dto.GenericResponse;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.ProjectTaskDto;
import com.jvpars.codetip.dto.TaskArrangeDto;
import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.service.api.ProjectCardService;
import com.jvpars.codetip.service.api.TaskService;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/project-tasks")
@Slf4j
public class ProjectTaskApi {

    private TaskService service;
    private ProjectCardService cardService;
    private AppUserService userService;

    //    @Secured("ROLE_ADMIN")
    //    @PostMapping(value = "/create-new-user" , produces = MediaType.APPLICATION_JSON_VALUE)
    
    @Autowired
    public ProjectTaskApi(TaskService service, ProjectCardService cardService , AppUserService userService) {
        this.service = service;
        this.cardService = cardService;
        this.userService =  userService;
    }

    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAll() {
        try {
            Iterable<ProjectTask> res = service.findAll();
            return GenericResponseGenerator.success(res);

        } catch (Exception ex) {
            GenericResponse<Boolean> response = new GenericResponse<>();
            response.operationResult = OperationResult.Fail;
            response.message = ex.getMessage();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/paged-list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity list(Pageable pageable) {
        try {

            Page<ProjectTask> tasks = service.findAll(pageable);
            List<ProjectTask> items = new ArrayList<>();
            for (ProjectTask Task : tasks) {
                items.add(Task);
            }
            long total = tasks.getTotalElements();
            int pageCount = tasks.getTotalPages();
            return GenericResponseGenerator.pageable(total, pageCount, items);
        } catch (Exception ex) {
            return GenericResponseGenerator.pageableError(ex.getMessage());
        }
    }

    @PostMapping(value = "/get-by-card/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllBayCard(@RequestBody Long id) {
        try {

            ProjectCard card = cardService.findOne(id);
            List<ProjectTask> tasks = service.findAllByCard(card);
            return GenericResponseGenerator.success(tasks);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity save(@RequestBody ProjectTaskDto request) {
        try {
            ProjectTask task = new ProjectTask();
            AppUser user = userService.findOne(request.userId);
            ProjectCard card = cardService.findOne(request.cardId);
            int lastId = card.getTasks().size() - 1;
            Long order = lastId <= 0 ? 1:card.getTasks().get(lastId).getId();
            task.setCardId(card.getId());
            task.setUserId(user.getId());
            task.setUser(user);
            task.setDescription(request.description);
            task.setEndDate(request.endDate);
            task.setStartDate(request.startDate);
            task.setStatus(request.status);
            task.setTitle(request.title);
            task.setArrange(order);
            ProjectTask res = service.save(task);
            return GenericResponseGenerator.success(new ProjectTaskDto(res));
        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-all-by-user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByUser(@RequestBody AppUser user) {
        try {
            List<ProjectTask> res = service.findAllByUsers(user);
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-all-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByTitle(@RequestBody String title) {
        try {
            List<ProjectTask> res = service.findAllByTitle(title);
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getByTitle(@RequestBody String title) {
        try {
            ProjectTask res = service.findFirstByTitle(title);
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @GetMapping(value = "/get-all-by-Status", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByStatus(@RequestBody EntityStatus status) {
        try {


            List<ProjectTask> res = service.findAllByStatus(status);
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity delete(@PathVariable Long id) {
        try {
            ProjectTask old = service.findOne(id);
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            service.delete(id);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity update(@RequestBody ProjectTaskDto task) {
        try {
            ProjectTask old = service.findOne(task.id);
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            AppUser user = userService.findOne(task.userId);
            if(user == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            ProjectCard card = cardService.findOne(task.cardId);
            if(card == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            old.setTitle(task.title);
            old.setDescription(task.description);
            old.setEndDate(task.endDate);
            old.setStartDate(task.startDate);
            old.setStatus(task.status);
            old.setUserId(user.getId());
            service.save(old);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PutMapping(value = "/update-card-task", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity updateCardTask(@RequestBody ProjectTaskDto task) {

        try {
            log.warn(task.toString());
            ProjectTask old = service.findOne(task.id);
            if(old == null) {
                log.warn("old task no found");
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            ProjectCard card = cardService.findOne(task.cardId);
            if(card == null) {
                log.warn("card not found");
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            old.setCard(card);
            if(task.arrange!=null)
            old.setArrange(task.arrange);
            service.save(old);
            log.warn("update-card-task success");
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @PutMapping(value = "/update-task-arrange", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity updateTaskArrange(@RequestBody TaskArrangeDto task) {
        try {
            ProjectTask old = service.findOne(task.id);
            if(old == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            if(task.newCardId != null) {
                ProjectCard card = cardService.findOne(task.newCardId);
                if(card == null)
                    return GenericResponseGenerator.response(OperationResult.NotFound);
                old.setCard(card);
                old.setCardId(card.getId());

                List<ProjectTask> tasks = service.findAllByCardOrderByArrange(card);
                tasks.add(old);
                int index = tasks.indexOf(old);
                Long counter = 0L;
                for(int i=0; i< tasks.size(); i++) {
                    if(i == task.position) {
                        tasks.get(index).setArrange(counter);
                        counter++;
                        tasks.get(i).setArrange(counter);
                        counter++;
                    } else if(i == index) {
                        if(tasks.get(i).getArrange() == task.position) {
                            continue;
                        } else {
                            tasks.get(i).setArrange(task.position);
                        }
                    } else if(task.position > index && i > task.position) {
                        counter = task.position + 1;
                        tasks.get(i).setArrange(counter);
                        counter++;
                    } else {
                        tasks.get(i).setArrange(counter);
                        counter++;
                    }
                }
            } else {
                ProjectCard card = cardService.findOne(task.cardId);
                if(card == null)
                    return GenericResponseGenerator.response(OperationResult.NotFound);

                List<ProjectTask> tasks = service.findAllByCardOrderByArrange(card);
                int index = tasks.indexOf(old);
                Long counter = 0L;
                for(int i=0; i< tasks.size(); i++) {
                    if(i == task.position) {
                        tasks.get(index).setArrange(counter);
                        counter++;
                        tasks.get(i).setArrange(counter);
                        counter++;
                    } else if(i == index) {
                        if(tasks.get(i).getArrange() == task.position) {
                            continue;
                        } else {
                            tasks.get(i).setArrange(task.position);
                        }
                    } else if(task.position > index && i > task.position) {
                        counter = task.position + 1;
                        tasks.get(i).setArrange(counter);
                        counter++;
                    } else {
                        tasks.get(i).setArrange(counter);
                        counter++;
                    }
                }
            }
            service.save(old);
            return  GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    // get tasks of users that expired
    @GetMapping(value = "/get-all-by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllByDate(Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            List<ProjectTask> res = service.findAllByUserAndStatus(user, EntityStatus.WORK);
            List<ProjectTask> expired = new ArrayList<>();

            // check for project status is work(is active) and end date is greater than now
            LocalDate now = LocalDate.now();
            LocalDate temp;
            for (ProjectTask task:res) {
                temp = Instant.ofEpochMilli(task.getEndDate()).atZone(ZoneId.systemDefault()).toLocalDate();
                if(now.compareTo(temp) > 0) {
                    expired.add(task);
                }
            }
            return GenericResponseGenerator.success(expired);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    // get tasks of users that expired
    @GetMapping(value = "/get-all-today", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getAllToday(Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            List<ProjectTask> tasks = service.findAllByUserAndStatus(user, EntityStatus.WORK);
            List<ProjectTask> todayTasks = new ArrayList<>();

            LocalDate today = LocalDate.now();
            LocalDate temp;

            for (ProjectTask task:tasks) {
                temp = Instant.ofEpochMilli(task.getEndDate()).atZone(ZoneId.systemDefault()).toLocalDate();
                if(today.compareTo(temp) == 0) {
                    todayTasks.add(task);
                }
            }
            List<ProjectTaskDto> res = todayTasks.stream().map(x -> new ProjectTaskDto(x)).collect(Collectors.toList());
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }
}