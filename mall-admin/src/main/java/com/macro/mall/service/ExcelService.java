package com.macro.mall.service;

import com.macro.mall.dto.OmsOrderExportResult;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;

/**
 * Excel报表Service
 * Create by zhuyong on 2019/7/21
 */
public interface ExcelService {

    SXSSFWorkbook exportOrder(List<OmsOrderExportResult> orderList);
}
