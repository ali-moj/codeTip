package com.jvpars.codetip.repository;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.LoginHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoginHistoryRepository extends CrudRepository<LoginHistory, Long> {
    @Query("SELECT hst FROM LoginHistory hst WHERE hst.user=:user AND hst.loginTime<=:less AND hst.loginTime>=:greate")
    List<LoginHistory> findConditional(@Param("user") AppUser user, @Param("less") Long lessThan, @Param("greate") Long greateThan);
}