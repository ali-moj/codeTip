package com.jvpars.codetip.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.LoginHistory;
import com.jvpars.codetip.dto.GenericResponse;
import com.jvpars.codetip.dto.OperationResult;
import com.jvpars.codetip.dto.UserDto;
import com.jvpars.codetip.dto.requests.LoginRequest;
import com.jvpars.codetip.security.jwt.JwtTokenProvider;
import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.service.api.LoginHistoryService;
import com.jvpars.codetip.service.api.MessageService;
import com.jvpars.codetip.service.api.RoomService;
import com.jvpars.codetip.utils.DocumentService;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/account")
@Slf4j
public class AccountApi {

    final AppUserService service;
    private final DocumentService documentService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginHistoryService loginHistoryService;
    private MessageService messageRepository;
    private RoomService roomService;


    @Autowired
    public AccountApi(AppUserService service,
                      DocumentService documentService,
                      AuthenticationManager authenticationManager,
                      JwtTokenProvider jwtTokenProvider,
                      LoginHistoryService loginHistoryService,
                      MessageService messageRepository,
                      RoomService roomService) {
        this.service = service;
        this.documentService = documentService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginHistoryService = loginHistoryService;
        this.messageRepository = messageRepository;
        this.roomService = roomService;

    }

    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody LoginRequest request) {
        log.info("LoginRequest " + request);
        try {
            AppUser user = service.findByUsername(request.username);
            if (user == null)
                return GenericResponseGenerator.response(OperationResult.NotFound);
            List<String> roles = user.getRoles();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username, request.password));
            String token = jwtTokenProvider.createToken(request.username, roles);

            LoginHistory history = new LoginHistory();
            history.setLoginTime(DateTime.now().getMillis());
            history.setUserId(user.getId());
            loginHistoryService.save(history);

            UserDto res = new UserDto(user);
            res.setToken(token);
            return GenericResponseGenerator.success(res);

        } catch (Exception ex) {
            ex.printStackTrace();
            return GenericResponseGenerator.response(OperationResult.Fail , ex.getMessage() );
        }


    }

    @GetMapping(value = "/install", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity install() {

        AppUser user = new AppUser();
        user.setPassword("123456");
        user.setFullName("bahman");
        user.setAdmin(true);
        user.setUsername("araazz");
        user.setAvatar("/dl/image/user-profiles/01.jpg");
        user.setRoles(Collections.singletonList("ROLE_ADMIN"));

        AppUser user1 = new AppUser();
        user1.setPassword("123456");
        user1.setFullName("hassan");
        user1.setAdmin(true);
        user1.setUsername("hassan");
        user1.setAvatar("/dl/image/user-profiles/01.jpg");
        user1.setRoles(Collections.singletonList("ROLE_ADMIN"));

        AppUser user2 = new AppUser();
        user2.setPassword("123456");
        user2.setFullName("alireza");
        user2.setAdmin(false);
        user2.setUsername("user");
        user2.setAvatar("/dl/image/user-profiles/01.jpg");
        user2.setRoles(Collections.singletonList("ROLE_USER"));

        AppUser user4 = new AppUser();
        user4.setPassword("123456");
        user4.setFullName("aidin");
        user4.setAdmin(true);
        user4.setUsername("ali");
        user4.setAvatar("/dl/image/user-profiles/01.jpg");
        user4.setRoles(Collections.singletonList("ROLE_ADMIN"));


        service.save(user, false);
        service.save(user1, false);
        service.save(user2, false);
        service.save(user4, false);

        return new ResponseEntity<>("its ok", HttpStatus.OK);
    }

    //.............
    @RequestMapping(value = "/is-online", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity isOnline() {
        return GenericResponseGenerator.success(true);
    }
}
