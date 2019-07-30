package com.macro.mall.dto;

/**
 * 订单统计结果
 * Create by zhuyong on 2019/7/23
 */
public class OrderStatisticResult {
     private Long employeeId;//员工id
     private String employeeName; //员工姓名
     private Long todayOrderNum; //今日订单总数
     private Integer todayProductNum; //今日商品总数
     private Long allOrderNum; //员工订单总数
     private Integer todayVisitTimes; //今天被访问次数

    public Integer getTodayVisitTimes() {
        return todayVisitTimes;
    }

    public void setTodayVisitTimes(Integer todayVisitTimes) {
        this.todayVisitTimes = todayVisitTimes;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getTodayOrderNum() {
        return todayOrderNum;
    }

    public void setTodayOrderNum(Long todayOrderNum) {
        this.todayOrderNum = todayOrderNum;
    }

    public Integer getTodayProductNum() {
        return todayProductNum;
    }

    public void setTodayProductNum(Integer todayProductNum) {
        this.todayProductNum = todayProductNum;
    }

    public Long getAllOrderNum() {
        return allOrderNum;
    }

    public void setAllOrderNum(Long allOrderNum) {
        this.allOrderNum = allOrderNum;
    }
}
