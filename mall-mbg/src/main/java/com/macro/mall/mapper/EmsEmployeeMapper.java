package com.macro.mall.mapper;

import com.macro.mall.model.EmsEmployee;
import com.macro.mall.model.EmsEmployeeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmsEmployeeMapper {
    long countByExample(EmsEmployeeExample example);

    int deleteByExample(EmsEmployeeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmsEmployee record);

    int insertSelective(EmsEmployee record);

    List<EmsEmployee> selectByExample(EmsEmployeeExample example);

    EmsEmployee selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmsEmployee record, @Param("example") EmsEmployeeExample example);

    int updateByExample(@Param("record") EmsEmployee record, @Param("example") EmsEmployeeExample example);

    int updateByPrimaryKeySelective(EmsEmployee record);

    int updateByPrimaryKey(EmsEmployee record);
}