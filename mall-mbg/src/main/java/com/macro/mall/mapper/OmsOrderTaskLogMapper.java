package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrderTaskLog;
import com.macro.mall.model.OmsOrderTaskLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsOrderTaskLogMapper {
    long countByExample(OmsOrderTaskLogExample example);

    int deleteByExample(OmsOrderTaskLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderTaskLog record);

    int insertSelective(OmsOrderTaskLog record);

    List<OmsOrderTaskLog> selectByExample(OmsOrderTaskLogExample example);

    OmsOrderTaskLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsOrderTaskLog record, @Param("example") OmsOrderTaskLogExample example);

    int updateByExample(@Param("record") OmsOrderTaskLog record, @Param("example") OmsOrderTaskLogExample example);

    int updateByPrimaryKeySelective(OmsOrderTaskLog record);

    int updateByPrimaryKey(OmsOrderTaskLog record);
}