package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class EmsEmployee implements Serializable {
    @ApiModelProperty(value = "å‘˜å·¥id")
    private Long id;

    private String nickName;

    private String loginName;

    @ApiModelProperty(value = "å‘˜å·¥çº§åˆ«: 1: ç»„å‘˜ 2:ç»„é•¿")
    private Integer employeeLevel;

    @ApiModelProperty(value = "ç»„é•¿id")
    private Long groupLeaderId;

    @ApiModelProperty(value = "åˆ›å»ºæ—¶é—´")
    private Date createTime;

    @ApiModelProperty(value = "çŠ¶æ€: 0ï¼šæ­£å¸¸ 1: åˆ é™¤")
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getEmployeeLevel() {
        return employeeLevel;
    }

    public void setEmployeeLevel(Integer employeeLevel) {
        this.employeeLevel = employeeLevel;
    }

    public Long getGroupLeaderId() {
        return groupLeaderId;
    }

    public void setGroupLeaderId(Long groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", nickName=").append(nickName);
        sb.append(", loginName=").append(loginName);
        sb.append(", employeeLevel=").append(employeeLevel);
        sb.append(", groupLeaderId=").append(groupLeaderId);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}