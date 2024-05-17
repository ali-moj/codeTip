package com.jvpars.codetip.api;


import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ReminderEvent;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.requests.ReminderRequest;
import com.jvpars.codetip.dto.responses.ReminderResponse;
import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.service.api.ReminderService;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@RestController
@RequestMapping(value = "/api/reminder")
public class ReminderApi {
    private final Logger log = LoggerFactory.getLogger(ReminderApi.class);

    private ReminderService service;
    private AppUserService userService;

    @Autowired
    public ReminderApi(ReminderService repository,
                       AppUserService userService) {
        this.service = repository;
        this.userService = userService;
    }



    @GetMapping(value ="/get-reminders", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity list(@RequestParam String from,
                        @RequestParam String to, Principal principal) {
        try {

            AppUser user = userService.findByUsername(principal.getName());

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date formDate = df.parse(from);
            long epochFrom = formDate.getTime();


            Date toDate = df.parse(to);
            long epochTo = toDate.getTime();

            List<ReminderEvent> list = service
                    .findAllByUserAndDateBetween(user, epochFrom, epochTo);

            List<ReminderResponse> items = new ArrayList<>();
            for (ReminderEvent item : list) {
                items.add(new ReminderResponse(item));
            }
            return GenericResponseGenerator.success(items);
        } catch (Exception ex) {

            return GenericResponseGenerator.error(ex.getMessage());

        }
    }


    @PostMapping(value ="/save", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity save(@RequestBody ReminderRequest request, Principal principal) {
        try {

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date formDate = df.parse(request.date);
            long date = formDate.getTime();

            AppUser user = userService.findByUsername(principal.getName());
            ReminderEvent event = new ReminderEvent();
            event.setTitle(request.title);
            event.setUserId(user.getId());
            event.setDate(date);
            event.setType(request.type);
            event.setDescription(request.description);
            service.save(event);
            return GenericResponseGenerator.success(1);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());

        }
    }


    @PostMapping(value ="/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity edit(@RequestBody ReminderRequest request, Principal principal) {
        try {

            ReminderEvent opt = service.findOne(request.id);
            if (opt==null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            AppUser user = userService.findByUsername(principal.getName());
            if(opt.getUserId() != user.getId()) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date formDate = df.parse(request.date);
            long date = formDate.getTime();

            ReminderEvent event = opt;
            event.setTitle(request.title);
            event.setDescription(request.description);
            event.setType(request.type);
            event.setDate(date);
            service.save(event);
            return GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());

        }
    }


    @PostMapping(value ="/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity delete(@RequestBody ReminderRequest request, Principal principal) {
        try {
            ReminderEvent opt = service.findOne(request.id);
            if (opt==null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            AppUser user = userService.findByUsername(principal.getName());
            if(opt.getUserId() != user.getId()) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            service.delete(request.id);
            return GenericResponseGenerator.success(1);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());

        }
    }
}
