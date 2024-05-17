package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AdminMessage;
import com.jvpars.codetip.repository.AdminMessageRepository;
import com.jvpars.codetip.service.api.AdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminMessageServiceImpl implements AdminMessageService {
    private AdminMessageRepository repository;

    @Autowired
    AdminMessageServiceImpl(AdminMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    @Override
    public AdminMessage save(AdminMessage message) {
        return repository.save(message);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public AdminMessage findOne(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Page<AdminMessage> findAllPageable(Pageable pageable){
        return repository.findAll(pageable);
    }

    @Override
    public List<AdminMessage> findTop5(){
        return repository.findTop5ByOrderByIdDesc();
    }


}
