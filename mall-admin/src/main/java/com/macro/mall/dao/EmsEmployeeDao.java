package com.macro.mall.dao;

import com.macro.mall.dto.EmsEmployeeQueryParam;
import com.macro.mall.model.EmsEmployee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmsEmployeeDao {
    /**
     * 条件查询订单
     */
    List<EmsEmployee> getList(@Param("queryParam") EmsEmployeeQueryParam queryParam);
}
