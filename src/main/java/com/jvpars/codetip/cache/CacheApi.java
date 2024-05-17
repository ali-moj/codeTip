package com.jvpars.codetip.cache;

import java.util.List;

public interface CacheApi {

    CacheMem save(CacheMem s);
    Iterable<CacheMem>  saveAll(Iterable<CacheMem> l);
    CacheMem findBySsid(String ssid);
    void deleteBySsid(String ssid);
    void deleteByRoomId(Long roomId);
    void deleteBySsidAndRoomId(String ssid,Long roomId);
    void deleteAllBySsid(String ssid);
    List<CacheMem> findBySsidAndRoomId(String ssid, Long roomId);
    List<CacheMem> findByNameAndRoomId(String ssid,Long roomId);
    long count();
    void delete(CacheMem c);
    List<CacheMem> findByName(String userName);
    List<CacheMem> findByRoomId(Long roomId);
}
