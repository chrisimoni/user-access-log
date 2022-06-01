package com.chrisreal.useraccesslog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity(name = "USER_ACCESS_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime date;
    private String ip;
    private String request;
    private String status;
    private String userAgent;
}
