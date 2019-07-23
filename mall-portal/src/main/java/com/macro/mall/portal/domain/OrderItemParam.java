package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * 提交的订单项
 * Create by zhuyong on 2019/7/18
 */
public class OrderItemParam{
    @ApiModelProperty("商品id")
    private Long productId; //商品id
    @ApiModelProperty("购买数量")
    private Integer productQuantity; //购买数量
    @ApiModelProperty("商品规格")
    private String productAttr;//商品规格
    @ApiModelProperty("库存id")
    private Long skuId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }
}
