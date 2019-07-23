package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsProductShortChain;

import java.util.List;

/**
 * 短链列表查询结果
 * Create by zhuyong on 2019/7/21
 */
public class PmsProductShortChainResult {
    private List<PmsProductShortChain> shortChainList;

    public List<PmsProductShortChain> getShortChainList() {
        return shortChainList;
    }

    public void setShortChainList(List<PmsProductShortChain> shortChainList) {
        this.shortChainList = shortChainList;
    }
}
