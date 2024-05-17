package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.UserMessage;
import com.jvpars.codetip.repository.UserMessageRepository;
import com.jvpars.codetip.service.api.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserMessageServiceImpl implements UserMessageService {
    private UserMessageRepository repository;

    @Autowired
    public UserMessageServiceImpl(UserMessageRepository repository) {
        this.repository = repository;

    }

    @Transactional(readOnly = false)
    @Override
    public UserMessage save(UserMessage userMessage) {

        try {

            return repository.save(userMessage);
        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public List<UserMessage> findVisitors(Long messageId) {
        return repository.findVisitors(messageId);
    }
}
