package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 单品营销订单提交
 * Create by zhuyong on 2019/7/16
 */
public class OrderSubmitParam {
    @ApiModelProperty("员工id")
    private Long employeeId;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("省")
    private String province;
    @ApiModelProperty("市")
    private String city;
    @ApiModelProperty("区")
    private String region;
    @ApiModelProperty("详细地址")
    private String detailAddress;
    @ApiModelProperty("支付方式：0->未支付；1->支付宝；2->微信")
    private Integer payType;   //支付方式：0->未支付；1->支付宝；2->微信

    private List<OrderItemParam> orderItemList;
    private Long couponId;
    private Integer useIntegration;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public List<OrderItemParam> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItemParam> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Integer getUseIntegration() {
        return useIntegration;
    }

    public void setUseIntegration(Integer useIntegration) {
        this.useIntegration = useIntegration;
    }
}

