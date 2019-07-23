package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * ISPAY第三方微信支付参数
 * Create by zhuyong on 2019/7/22
 */
public class IsPayParam {
    @ApiModelProperty("订单id")
    private Long orderId;
    @ApiModelProperty("商户自定义订单标题")
    private String Subject; //            测试标题
    @ApiModelProperty("商户自主生成的订单号")
    private String orderNumber; //          201702080118441263011007
    @ApiModelProperty("商户自定义附加数据")
    private String attachData; //           test
    @ApiModelProperty("客户端同步跳转")
    private String Return_url; //     https://www.ispay.cn/return/

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getAttachData() {
        return attachData;
    }

    public void setAttachData(String attachData) {
        this.attachData = attachData;
    }

    public String getReturn_url() {
        return Return_url;
    }

    public void setReturn_url(String return_url) {
        Return_url = return_url;
    }
}
