package com.chrisreal.useraccesslog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chrisreal.useraccesslog.entity.UserAccessLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public  interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {
    @Query("SELECT DISTINCT ip from USER_ACCESS_LOG ")
    List<String> findDistinctIp();

    @Query(value = "SELECT ip FROM USER_ACCESS_LOG where date >= :startDate AND date <= :endDate group by user_access_log.ip HAVING COUNT(ip)> :limit ",nativeQuery = true)
    List<String>getAllIpByDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit);


    @Query(value = "SELECT Count(ip) FROM USER_ACCESS_LOG where date >= :startDate AND date <= :endDate group by user_access_log.ip having ip =:ip",nativeQuery = true)
    Integer findCountByIp(LocalDateTime startDate , LocalDateTime endDate, String ip);



}
