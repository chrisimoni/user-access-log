package com.chrisreal.useraccesslog.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity(name = "BLOCKED_IP_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockedIpTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String ip;
    private int requestNumber;
    private String comment ;

}
