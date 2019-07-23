package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 重定向参数
 * Create by zhuyong on 2019/7/23
 */
public class RedirectParam {
    @ApiModelProperty("员工Id")
    private Long employeeId;
    @ApiModelProperty("产品id")
    private Long productId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
