package com.macro.mall.portal.domain;

import java.util.List;

/**
 * tree 规格组件
 * Create by zhuyong on 2019/7/18
 */
public class AttrTree {
    private String k;
    private List<VNode> v;
    private String k_s;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public List<VNode> getV() {
        return v;
    }

    public void setV(List<VNode> v) {
        this.v = v;
    }

    public String getK_s() {
        return k_s;
    }

    public void setK_s(String k_s) {
        this.k_s = k_s;
    }
}
