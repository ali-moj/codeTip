package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ReminderEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReminderService {
    @Transactional(readOnly = false)
    ReminderEvent save(ReminderEvent event);

    @Transactional(readOnly = false)
    void delete(Long id);

    Iterable<ReminderEvent> findAll();

    ReminderEvent findOne(Long id);

    long count();

    List<ReminderEvent> findAllByUserAndDateBetween(AppUser user , long from , long to);
}
