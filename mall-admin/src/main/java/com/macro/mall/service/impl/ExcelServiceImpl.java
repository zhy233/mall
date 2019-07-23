package com.macro.mall.service.impl;

import com.macro.mall.dto.OmsOrderExportResult;
import com.macro.mall.service.ExcelService;
import com.macro.mall.util.ExcelUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ExcelService实现类
 * Create by zhuyong on 2019/7/21
 */
@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public SXSSFWorkbook exportOrder(List<OmsOrderExportResult> orderList) {
        SXSSFWorkbook workbook = ExcelUtils.createExcelOfStandardWithNum(4, "订单报表", ExcelUtils.orderTitlescheme, orderList, ExcelUtils.orderPropertiescheme,null);
        return workbook;
    }
}
