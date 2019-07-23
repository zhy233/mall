package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductShortChain;
import com.macro.mall.model.PmsProductShortChainExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductShortChainMapper {
    long countByExample(PmsProductShortChainExample example);

    int deleteByExample(PmsProductShortChainExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductShortChain record);

    int insertSelective(PmsProductShortChain record);

    List<PmsProductShortChain> selectByExample(PmsProductShortChainExample example);

    PmsProductShortChain selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsProductShortChain record, @Param("example") PmsProductShortChainExample example);

    int updateByExample(@Param("record") PmsProductShortChain record, @Param("example") PmsProductShortChainExample example);

    int updateByPrimaryKeySelective(PmsProductShortChain record);

    int updateByPrimaryKey(PmsProductShortChain record);
}