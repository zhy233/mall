package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrderReturnApply;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 单品订单项
 * Create by zhuyong on 2019/7/15
 */
public class OnePageOrderItem {

    private Long productId;

    private String productPic;

    private String productName;

    @ApiModelProperty("商品品牌")
    private String productBrand;

    @ApiModelProperty(value = "商品销售属性:[{'key':'颜色','value':'颜色'},{'key':'容量','value':'4G'}]")
    private String productAttr;

    @ApiModelProperty(value = "销售价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "购买数量")
    private Integer productQuantity;

    //private List<OmsOrderReturnApply> orderReturnApplies;

//    public List<OmsOrderReturnApply> getOrderReturnApplies() {
//        return orderReturnApplies;
//    }
//
//    public void setOrderReturnApplies(List<OmsOrderReturnApply> orderReturnApplies) {
//        this.orderReturnApplies = orderReturnApplies;
//    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }
}
