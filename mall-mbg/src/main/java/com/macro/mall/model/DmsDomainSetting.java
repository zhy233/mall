package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class DmsDomainSetting implements Serializable {
    private Long id;

    @ApiModelProperty(value = "入口域名")
    private String mainDomain;

    @ApiModelProperty(value = "落地域名")
    private String landingDomain;

    @ApiModelProperty(value = "操作时间")
    private Date oprationTime;

    @ApiModelProperty(value = "客服电话")
    private String customerServicePhone;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMainDomain() {
        return mainDomain;
    }

    public void setMainDomain(String mainDomain) {
        this.mainDomain = mainDomain;
    }

    public String getLandingDomain() {
        return landingDomain;
    }

    public void setLandingDomain(String landingDomain) {
        this.landingDomain = landingDomain;
    }

    public Date getOprationTime() {
        return oprationTime;
    }

    public void setOprationTime(Date oprationTime) {
        this.oprationTime = oprationTime;
    }

    public String getCustomerServicePhone() {
        return customerServicePhone;
    }

    public void setCustomerServicePhone(String customerServicePhone) {
        this.customerServicePhone = customerServicePhone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", mainDomain=").append(mainDomain);
        sb.append(", landingDomain=").append(landingDomain);
        sb.append(", oprationTime=").append(oprationTime);
        sb.append(", customerServicePhone=").append(customerServicePhone);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}