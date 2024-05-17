package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ReminderEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReminderRepository extends CrudRepository<ReminderEvent,Long> {

    List<ReminderEvent> findAllByUserAndDateBetween(AppUser user , long from , long to);

}
