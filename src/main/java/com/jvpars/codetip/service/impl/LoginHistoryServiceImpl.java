package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.LoginHistory;
import com.jvpars.codetip.repository.LoginHistoryRepository;
import com.jvpars.codetip.service.api.LoginHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoginHistoryServiceImpl implements LoginHistoryService {
    private LoginHistoryRepository repository;

    @Autowired
    public LoginHistoryServiceImpl(LoginHistoryRepository repository) {
        this.repository = repository;
    }



    @Transactional(readOnly = false)
    @Override
    public LoginHistory save(LoginHistory loginHistory) {
        return repository.save(loginHistory);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<LoginHistory> findAllByUserLoginTime(AppUser user, Long lessThan, Long greateThan ) {
        return repository.findConditional(user, lessThan, greateThan);
    }

    @Override
    public long count() {
        return repository.count();
    }
}
