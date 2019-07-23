package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * ISPAY回调参数
 * Create by zhuyong on 2019/7/22
 */
public class IsPayCallBackParam {
    @ApiModelProperty("返回 支付通道 代码")
    private String payChannel;
    @ApiModelProperty("用户付款金额（单位分）")
    private Integer Money;
    @ApiModelProperty("商户自主生成的订单号")
    private String orderNumber;
    @ApiModelProperty("商户自定义附加数据")
    private String attachData;
    @ApiModelProperty("回调签名 查看算法")
    private String callbackSign;


    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public Integer getMoney() {
        return Money;
    }

    public void setMoney(Integer money) {
        Money = money;
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

    public String getCallbackSign() {
        return callbackSign;
    }

    public void setCallbackSign(String callbackSign) {
        this.callbackSign = callbackSign;
    }
}
