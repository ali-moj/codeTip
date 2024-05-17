package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.ReminderEvent;
import com.jvpars.codetip.repository.ReminderRepository;
import com.jvpars.codetip.repository.RoomRepository;
import com.jvpars.codetip.service.api.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReminderServiceImpl implements ReminderService {

    private ReminderRepository repository;


    @Autowired
    public ReminderServiceImpl(ReminderRepository repository ) {
        this.repository = repository;

    }

    @Transactional(readOnly = false)
    @Override
    public ReminderEvent save(ReminderEvent event) {
        return repository.save(event);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
      repository.deleteById(id);
    }

    @Override
    public Iterable<ReminderEvent> findAll() {
        return repository.findAll();
    }

    @Override
    public ReminderEvent findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<ReminderEvent> findAllByUserAndDateBetween(AppUser user, long from, long to) {
        return repository.findAllByUserAndDateBetween(user,from,to);
    }
}
