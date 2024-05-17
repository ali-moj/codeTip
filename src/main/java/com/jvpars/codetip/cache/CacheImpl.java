package com.jvpars.codetip.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CacheImpl implements CacheApi {
    CacheMemRepository repository;

    @Autowired
    public CacheImpl(CacheMemRepository repository) {
        this.repository = repository;
    }

    public CacheMem save(CacheMem s){
        return  repository.save(s);
    }

    public Iterable<CacheMem>  saveAll(Iterable<CacheMem> l){
        return  repository.saveAll(l);
    }

    public CacheMem findBySsid(String ssid){
     return  repository.findBySsid(ssid);
    }
    public void deleteBySsid(String ssid){
        repository.deleteBySsid(ssid);
    }
    public void deleteByRoomId(Long roomId){
        repository.deleteByRoomId(roomId);
    }
    public void deleteBySsidAndRoomId(String ssid,Long roomId){
        repository.deleteBySsidAndRoomId(ssid,roomId);
    }
    public void deleteAllBySsid(String ssid){
        repository.deleteAllBySsid(ssid);
    }

    @Override
    public List<CacheMem> findBySsidAndRoomId(String ssid, Long roomId) {
     return   repository.findBySsidAndRoomId( ssid,  roomId);
    }

    @Override
    public List<CacheMem> findByNameAndRoomId(String name, Long roomId) {
       return  repository.findByNameAndRoomId( name,  roomId);
    }

    public long count(){
       return repository.count();
    }

    @Override
    public void delete(CacheMem c) {
         repository.delete(c);
    }

    @Override
    public List<CacheMem> findByName(String userName) {
        return repository.findByName(userName);
    }

    @Override
    public List<CacheMem> findByRoomId(Long roomId) {
        return repository.findByRoomId(roomId);
    }


}
