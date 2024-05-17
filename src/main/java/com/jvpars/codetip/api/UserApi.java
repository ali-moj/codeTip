package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.*;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.UserDto;
import com.jvpars.codetip.service.api.*;
import com.jvpars.codetip.utils.DocumentService;
import com.jvpars.codetip.utils.FolderPath;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserApi {


    private DocumentService documentService;
    private AppUserService service;
    private RoomService roomRepository;
    private RoomUserService roomUserRepository;
    private LoginHistoryService loginHistoryService;
    private ProjectService projectService;
    private final Logger log = LoggerFactory.getLogger(UserApi.class);

    @Autowired
    public UserApi(AppUserService service,
                   RoomService roomRepository,
                   DocumentService documentService,
                   RoomUserService roomUserRepository,
                   LoginHistoryService loginHistoryService,
                   ProjectService projectService) {
        this.service = service;
        this.roomRepository = roomRepository;
        this.documentService = documentService;
        this.roomUserRepository = roomUserRepository;
        this.loginHistoryService = loginHistoryService;
        this.projectService = projectService;
    }


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity userList() {
        try {
            Iterable<AppUser> list = service.findAll();
            List<UserDto> users = new ArrayList<>();
            list.forEach(x -> users.add(new UserDto(x)));
            return GenericResponseGenerator.success(users);
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/list-page", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity userList(Pageable pageable) {
        try {
            Page<AppUser> list = service.findAllPageable(pageable);
            List<UserDto> users = new ArrayList<>();
            list.forEach(x -> users.add(new UserDto(x)));
            long total = list.getTotalElements();
            int pageCount = list.getTotalPages();
            return GenericResponseGenerator.pageable(total, pageCount, users);
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/project-member/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity userList(@PathVariable Long id) {
        try {
            Project project = projectService.findOne(id);
            if (project == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            List<AppUser> users = project.getUsers();
            List<UserDto> res = users.stream().map(x -> new UserDto(x)).collect(Collectors.toList());
            return GenericResponseGenerator.success(res);
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity save(@RequestBody AppUser request) {
        try {
            AppUser savedUser = new AppUser();
            savedUser.setAdmin(request.getAdmin());
            savedUser.setFullName(request.getFullName());
            savedUser.setUsername(request.getUsername());
            savedUser.setPassword(request.getPassword());
            AppUser user = service.save(savedUser, true);

            if (request.getFile() != null) {
                byte[] data = documentService.Base64ToByte(request.getFile().data);
                String fileName = String.format("%s.%s",
                        new Random()
                                .nextInt(15000000),
                        FilenameUtils.getExtension(request.getFile().name));
                String fileUrl = documentService.saveImageFile(FolderPath.USER_PROFILE, fileName, data);
                if (fileUrl.equals("")) {
                    return GenericResponseGenerator.response(OperationResult.Fail);
                }
                user.setAvatar(fileUrl);
            } else {
                byte[] data = documentService.readFile(FolderPath.DEFAULT_PROFILE_IMAGE);
                String path = documentService.saveImageFile(FolderPath.USER_PROFILE, user.getId() + ".jpg", data);
                user.setAvatar(path);
            }
            service.save(user, false);

            return GenericResponseGenerator.success(new UserDto(user));
        } catch (Exception e) {

            e.printStackTrace();
            return GenericResponseGenerator.error(e.getMessage());
        }
    }


    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAppUser(@PathVariable Long id) {
        try {
            AppUser user = service.findOne(id);
            return GenericResponseGenerator.success(new UserDto(user));
        } catch (Exception e) {

            e.printStackTrace();
            return GenericResponseGenerator.error();

        }


    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAppUser(@RequestBody AppUser user) {
        log.warn(user.toString());
        try {
            AppUser findUser = service.findByUsername(user.getUsername());
            findUser.setFullName(user.getFullName());
            findUser.setUsername(user.getUsername());
            if (!user.getPassword().trim().equals("")) {
                findUser.setPassword(user.getPassword());
            }
            findUser.setAdmin(user.getAdmin());
            if (user.getFile() != null) {
                byte[] data = documentService.Base64ToByte(user.getFile().data);
                String fileName = String.format("%s.%s",
                        new Random()
                                .nextInt(15000000),
                        FilenameUtils.getExtension(user.getFile().name));
                String fileUrl = documentService.saveImageFile(FolderPath.USER_PROFILE, fileName, data);
                if (fileUrl.equals("")) {
                    return GenericResponseGenerator.response(OperationResult.Fail);
                }
                findUser.setAvatar(fileUrl);
            }
            if (findUser.getPassword() == user.getPassword()) {
                service.save(findUser, true);
            } else {
                service.save(findUser, false);
            }
            return GenericResponseGenerator.success(new UserDto(findUser));
        } catch (Exception e) {

            e.printStackTrace();
            return GenericResponseGenerator.error();

        }
    }

    @GetMapping(value = "/room-members/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity roomMembers(@PathVariable Long id) {
        try {
            Room room = roomRepository.findOne(id);
            if (room == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            List<RoomUser> roomUsers = roomUserRepository.findAllByRoom(room);
            List<UserDto> users = new ArrayList<>();
            for (RoomUser roomUser : roomUsers) {
                users.add(new UserDto(roomUser.getUser()));
            }
            return GenericResponseGenerator.success(users);


        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponseGenerator.error();

        }
    }

    @GetMapping(value = "/user-not-in-room/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity roomNotMember(@PathVariable Long id) {
        try {
            Room room = roomRepository.findOne(id);
            if (room == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);

            List<RoomUser> roomUsers = roomUserRepository.findAllByRoom(room);
            List<AppUser> users = new ArrayList<>();
            for (RoomUser roomUser : roomUsers) {
                users.add(roomUser.getUser());
            }
            return GenericResponseGenerator.success(users);
        } catch (Exception e) {

            e.printStackTrace();
            return GenericResponseGenerator.error();

        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/login-history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getLoginHistory(Principal principal) {
        try {
            AppUser user = service.findByUsername(principal.getName());
            // get user login history as a list

            DateTime date = DateTime.now();

            // start of tomorrow = end of today
            Long end = getEndDayOfWeek(date).plusDays( 1 ).withTimeAtStartOfDay().getMillis();

            // start of day
            Long first = getFirstDayOfWeek(date).withTimeAtStartOfDay().getMillis();
            List<LoginHistory> list = loginHistoryService.findAllByUserLoginTime(user, end, first);

            // get this weeks history from login history
            return GenericResponseGenerator.success(list);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }

    DateTime getFirstDayOfWeek(DateTime other) {
        if(other.dayOfWeek().get() == 7)
            return other;
        else
            return other.minusWeeks(1).withDayOfWeek(7);
    }

    DateTime getEndDayOfWeek(DateTime other) {
        if(other.dayOfWeek().get() == 1)
            return other;
        else
            return other.plusWeeks(1).withDayOfWeek(1);
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity save(@RequestBody String name) {
        try {
            List<AppUser> list = service.searchByUsername(name.trim());
            List<UserDto> users = list.stream().map(x -> new UserDto(x)).collect(Collectors.toList());
            return GenericResponseGenerator.success(users);
        } catch (Exception e) {

            e.printStackTrace();
            return GenericResponseGenerator.error(e.getMessage());
        }
    }

}
