package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductQrcode;
import com.macro.mall.model.PmsProductQrcodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductQrcodeMapper {
    long countByExample(PmsProductQrcodeExample example);

    int deleteByExample(PmsProductQrcodeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductQrcode record);

    int insertSelective(PmsProductQrcode record);

    List<PmsProductQrcode> selectByExample(PmsProductQrcodeExample example);

    PmsProductQrcode selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsProductQrcode record, @Param("example") PmsProductQrcodeExample example);

    int updateByExample(@Param("record") PmsProductQrcode record, @Param("example") PmsProductQrcodeExample example);

    int updateByPrimaryKeySelective(PmsProductQrcode record);

    int updateByPrimaryKey(PmsProductQrcode record);
}