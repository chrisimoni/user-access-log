package com.chrisreal.useraccesslog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chrisreal.useraccesslog.entity.BlockedIpTable;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIpTable, Long> {
}
