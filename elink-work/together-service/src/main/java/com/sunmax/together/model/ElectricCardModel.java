package com.sunmax.together.model;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 电卡信息表
 */
@Data
public class ElectricCardModel {


    private String id;

    private Integer cardType;


    private String cardNumber;

    private String physicalCard;


    private String licenseNumber;


    private String cardHolder;

    private Integer state;

    private double currentBalance;

    private LocalDateTime createTime;


}