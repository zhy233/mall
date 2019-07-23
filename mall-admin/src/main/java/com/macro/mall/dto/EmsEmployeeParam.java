package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 员工管理参数
 * Created by zhuyong on 2019/7/13.
 */
@Getter
@Setter
public class EmsEmployeeParam {
    @ApiModelProperty(value = "员工Id")
    private Long employeeId;
    @ApiModelProperty(value = "登录名",required = true)
    @NotNull(message = "登录名不能为空")
    private String loginName;
    @ApiModelProperty(value = "昵称",required = true)
    @NotNull(message = "昵称不能为空")
    private String nickName;
    @ApiModelProperty(value = "员工级别",required = true)
    @NotNull(message = "请选择员工级别")
    private Integer level;
    @ApiModelProperty("组长id")
    private Long leaderId;
}
