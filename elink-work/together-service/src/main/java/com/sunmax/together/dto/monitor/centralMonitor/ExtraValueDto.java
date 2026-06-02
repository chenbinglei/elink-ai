package com.sunmax.together.dto.monitor.centralMonitor;

import lombok.Data;

import java.util.List;

@Data
public class ExtraValueDto {

    private Boolean multiple;

    private List<EnumDto> enumArray;

}
