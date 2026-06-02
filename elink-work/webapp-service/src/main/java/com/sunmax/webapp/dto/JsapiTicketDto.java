package com.sunmax.webapp.dto;

import lombok.Data;

@Data
public class JsapiTicketDto {

    private String ticket;

    private Integer expiresIn;
}
