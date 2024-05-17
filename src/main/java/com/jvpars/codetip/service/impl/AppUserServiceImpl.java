package com.jvpars.codetip.service.impl;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.domain.Room;
import com.jvpars.codetip.repository.AppUserRepository;
import com.jvpars.codetip.service.api.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;


@Service
@Transactional(transactionManager = "h2Transaction", readOnly = true)
public class AppUserServiceImpl implements AppUserService {


    private AppUserRepository repository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AppUserServiceImpl(AppUserRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.repository = repository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional(readOnly = false)
    @Override
    public AppUser save(AppUser user, boolean changePassword) {

        try {
            // if user is editing, encrypting password twice cause invalid password.
            if (changePassword == true) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
            return repository.save(user);
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional(readOnly = false)
    @Override
    public AppUser update(AppUser user) {
        return repository.save(user);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Iterable<AppUser> findAll() {
        return repository.findAll();
    }


    @Override
    public AppUser findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public long count() {
        return repository.count();
    }

    public AppUser Login(String username, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getEncoder().encodeToString(hash);
            AppUser user = repository.findFirstByUsername(username);
            if (user.getPassword().equals(encoded)) {
                return user;
            }
            return null;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }

    }

    public AppUser findByUsername(String username) {
        return repository.findFirstByUsername(username);
    }

    @Override
    public List<AppUser> finAllByRoom(Room room) {
        //ToDo : find all user by room
        //return repository.findAllByRooms(room);
        return null;
    }

    @Override
    public AppUser findPrivateChatOtherSide(Long roomId, Long userId) {
        return repository.findPrivateChatOtherSide(roomId, userId);
    }

    @Override
    public List<AppUser> searchByUsername(String name) {
        return repository.searchByUsername(name);
    }

    @Override
    public Page<AppUser> findAllPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
