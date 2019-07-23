package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 支付参数
 * Create by zhuyong on 2019/7/21
 */
public class WxPayParam {
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("订单id")
    private Long orderId;
    @ApiModelProperty("前端跳转地址")
    private String frontUrl;
    @ApiModelProperty("支付类型: 1:默认微信支付")
    private String type;
    @ApiModelProperty("商品内容")
    private String body;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
