package com.sunmax.webapp.dto;

import lombok.Data;

@Data
public class AccessTokenDto {

    private String token;

    private Integer expiresIn;
}
