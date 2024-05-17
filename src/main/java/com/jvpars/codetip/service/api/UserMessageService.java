package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.UserMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserMessageService {
    @Transactional(readOnly = false)
    UserMessage save(UserMessage userMessage);

    List<UserMessage> findVisitors(Long messageId);
}
