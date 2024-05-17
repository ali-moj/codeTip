package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.LoginHistory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoginHistoryService {
    @Transactional(readOnly = false)
    LoginHistory save(LoginHistory loginHistory);

    @Transactional(readOnly = false)
    void delete(Long id);

    List<LoginHistory> findAllByUserLoginTime(AppUser user, Long lessThan, Long greateThan);

    long count();
}
