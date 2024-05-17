package com.jvpars.codetip.service.api;

import com.jvpars.codetip.domain.AdminMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AdminMessageService {
    @Transactional(readOnly = false)
    public abstract AdminMessage save(AdminMessage message);

    @Transactional(readOnly = false)
    public abstract void delete(Long id);

    public abstract AdminMessage findOne(Long id);

    public abstract long count();

    public abstract Page<AdminMessage> findAllPageable(Pageable pageable);

    public abstract List<AdminMessage> findTop5();
}
