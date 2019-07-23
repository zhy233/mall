package com.macro.mall.portal.domain;

/**
 * tree组件规格值封装对象
 * Create by zhuyong on 2019/7/18
 */
public class VNode{
    private String id;
    private String name;
    private String imgUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
