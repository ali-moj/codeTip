package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AdminMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdminMessageRepository extends CrudRepository<AdminMessage, Long> {
    Page<AdminMessage> findAll(Pageable pageable);
    List<AdminMessage> findTop5ByOrderByIdDesc();
}