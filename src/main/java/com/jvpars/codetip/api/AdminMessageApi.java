package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.AdminMessage;
import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.enumitem.SocketMessageType;
import com.jvpars.codetip.dto.AdminMessageDto;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.MessageDto;
import com.jvpars.codetip.dto.SocketMessage;
import com.jvpars.codetip.service.api.AdminMessageService;
import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/admin-message")
public class AdminMessageApi {

    private final AdminMessageService service;
    private AppUserService userService;
    private SimpMessagingTemplate template;

    @Autowired
    public AdminMessageApi(AdminMessageService service, AppUserService userService, SimpMessagingTemplate template) {
        this.service = service;
        this.userService = userService;
        this.template = template;
    }

    @GetMapping(value = "/latest-message", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity latestMessage() {
        try {
            List<AdminMessage> res = service.findTop5();
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/message-list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity messageList(Pageable pageable) {
        try {
            Page<AdminMessage> res = service.findAllPageable(pageable);
            long total = res.getTotalElements();
            int pageCount = res.getTotalPages();
            return GenericResponseGenerator.pageable(total, pageCount, AdminMessageDto.list(res.getContent()));
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getById(@PathVariable Long id) {
        try {
            AdminMessage res = new AdminMessage(service.findOne(id));
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/save" , produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity post(@RequestBody AdminMessage request, Principal principal) {
        try {
            AppUser user = userService.findByUsername(principal.getName());
            request.setUser(user);
            request.setTime(DateTime.now().getMillis());
            AdminMessage message = service.save(request);
            AdminMessage res = new AdminMessage(message);
            res.setUser(null);

            SocketMessage msg = new SocketMessage();
            MessageDto response = new MessageDto();
            response.setBody(message.getBody());
            msg.setMessage(response);
            msg.setType(SocketMessageType.ADMIN_MESSAGE);
            template.convertAndSend("/user/queue/notify", msg);


            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity delete(@PathVariable Long id) {
        try {
            AdminMessage temp = service.findOne(id);
            if (temp == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            service.delete(id);
            return GenericResponseGenerator.success(true);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity update(@RequestBody AdminMessage request) {
        try {
            AdminMessage temp = service.findOne(request.getId());
            if (temp == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }
            temp.setBody(request.getBody());
            AdminMessage message = service.save(temp);
            AdminMessage res = new AdminMessage(message);
            res.setUser(null);
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/send/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity sendMessage(@PathVariable Long id) {
        try {
            AdminMessage res = new AdminMessage(service.findOne(id));
            if (res == null) {
                return GenericResponseGenerator.response(OperationResult.NotFound);
            }

            SocketMessage msg = new SocketMessage();
            MessageDto response = new MessageDto();
            response.setBody(res.getBody());
            msg.setMessage(response);
            msg.setType(SocketMessageType.ADMIN_MESSAGE);
            template.convertAndSend("/user/queue/notify", msg);
            return GenericResponseGenerator.success(res);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }
}
