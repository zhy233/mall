package com.macro.mall.mapper;

import com.macro.mall.model.EmsEmployeePerformance;
import com.macro.mall.model.EmsEmployeePerformanceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEmployeePerformanceMapper {
    long countByExample(EmsEmployeePerformanceExample example);

    int deleteByExample(EmsEmployeePerformanceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEmployeePerformance record);

    int insertSelective(EmsEmployeePerformance record);

    List<EmsEmployeePerformance> selectByExample(EmsEmployeePerformanceExample example);

    EmsEmployeePerformance selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEmployeePerformance record, @Param("example") EmsEmployeePerformanceExample example);

    int updateByExample(@Param("record") EmsEmployeePerformance record, @Param("example") EmsEmployeePerformanceExample example);

    int updateByPrimaryKeySelective(EmsEmployeePerformance record);

    int updateByPrimaryKey(EmsEmployeePerformance record);
}