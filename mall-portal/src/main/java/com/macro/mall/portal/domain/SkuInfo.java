package com.macro.mall.portal.domain;

import java.math.BigDecimal;

/**
 * 前端组件的sku 的组合列表参数
 * Create by zhuyong on 2019/7/18
 */
public class SkuInfo {
    private Long id;
    private BigDecimal price;
    private String s1;
    private String s2;
    private String s3;
    private Integer stock_num;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }

    public Integer getStock_num() {
        return stock_num;
    }

    public void setStock_num(Integer stock_num) {
        this.stock_num = stock_num;
    }
}
