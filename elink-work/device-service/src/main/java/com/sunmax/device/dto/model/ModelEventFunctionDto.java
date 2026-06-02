package com.sunmax.device.dto.model;

import com.sunmax.device.entity.model.FunctionEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ModelEventFunctionDto", description = "模型事件添加功能点列表返回实体类")
public class ModelEventFunctionDto {

    /**
     * 功能点id
     */
    @ApiModelProperty(value = "功能点id")
    private String id;

    /**
     * 功能点名称
     */
    @ApiModelProperty(value = "功能点名称")
    private String functionName;

    /**
     * 功能点标识
     */
    @ApiModelProperty(value = "功能点标识")
    private String functionLogo;

    public ModelEventFunctionDto(FunctionEntity functionEntity) {
        BeanUtils.copyProperties(functionEntity, this);
    }

}
