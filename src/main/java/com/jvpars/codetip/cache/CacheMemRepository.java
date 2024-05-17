package com.jvpars.codetip.cache;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CacheMemRepository extends CrudRepository<CacheMem, Long> {
    CacheMem findBySsid(String ssid);
    void deleteBySsid(String ssid);
    void deleteByRoomId(Long roomId);
    void deleteBySsidAndRoomId(String ssid,Long roomId);
    void deleteAllBySsid(String ssid);
    List<CacheMem> findBySsidAndRoomId(String ssid, Long roomId);
    List<CacheMem> findByNameAndRoomId(String name, Long roomId);
    List<CacheMem> findByName(String userName);
    List<CacheMem> findByRoomId(Long roomId);
}