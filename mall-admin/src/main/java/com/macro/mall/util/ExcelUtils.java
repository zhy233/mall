package com.macro.mall.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by zzqno on 2016-12-14.
 */
public class ExcelUtils {
    public static String orderTitlescheme[]={"序号","编号","订单编号","提交时间","用户账号","订单金额","支付方式","订单来源","订单状态","支付时间"};
    public static String orderPropertiescheme[]={"id","orderSn","submitTime","memberUsername","payAmount","payTypeValue","sourceTypeValue","orderStatus","payTime"};
    public static String orderDigit[]={"#,#0","#,#0","","","","#,#0.00","","","",""};

    private static HSSFWorkbook workbook = null;

    private static SXSSFWorkbook sxssfWorkbook =null;    // xlsx


    public static Logger logger= LoggerFactory.getLogger(ExcelUtils.class);

    public static <E> HSSFWorkbook createExcel(String fileDir, String sheetName, String titleRow[], List<E> data, String properties[]) {
        //创建workbook
        workbook = new HSSFWorkbook();
        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet = workbook.createSheet(sheetName);

        sheet.autoSizeColumn((short)2); //调整第三列宽度
        sheet.autoSizeColumn((short)3); //调整第四列宽度
        //新建文件
        FileOutputStream out = null;
        boolean isschemeflag = false;
        if ("方案".equals(sheetName)) {
            isschemeflag = true;
        }
        try {
            //添加表头
            Row row = workbook.getSheet(sheetName).createRow(0);    //创建第一行
            for (int i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            int columnCount = sheet.getRow(0).getLastCellNum();

            if (!Objects.isNull(data) && data.size() > 0) {
                int schemeindex = 0;
                for (int columnIndex = 0; columnIndex < data.size(); columnIndex++) {
                    Object object = data.get(columnIndex);
                    Row newrow = sheet.createRow(columnIndex + 1);
                    newrow.createCell(0).setCellValue(columnIndex + 1);
                    for (int i = 0; i < columnCount - 1; i++) {  //columnCount-1，因为第一个序号，没有properties设置
                        String title = properties[i];
//                            String UTitle = Character.toUpperCase(title.charAt(0)) + title.substring(1, title.length()); // 使其首字母大写;
//                            String methodName = "get" + UTitle;
//                            Method method = class_.getDeclaredMethod(methodName); // 设置要执行的方法
//                            String datavalue = method.invoke(object) == null ? "" : method.invoke(object).toString(); // 执行该get方法,即要插入的数据
                        String datavalue = BeanUtils.getProperty(object, title);
                        newrow.createCell(i + 1).setCellValue(datavalue);
                    }
                    if (isschemeflag) { //方案导出，根据方案名称生成序号，因为该方案多车型，除第一行外，后面行的名称都是空，序号不进行累加
                        if (!StringUtils.isEmpty(newrow.getCell(1).getStringCellValue())) {
                            newrow.getCell(0).setCellValue(++schemeindex);
                        } else {
                            newrow.getCell(0).setCellValue("");
                        }
                    }
                }


            }

            return workbook;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

//    public List readFromExcel(String fileDir, String sheetName, Class class_) {
//        //创建workbook
//        File file = new File(fileDir);
//        try {
//            workbook = new HSSFWorkbook(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            logger.error(e.toString());
//        } catch (IOException e) {
//            logger.error(e.toString());
//        }
//
//        List result = new ArrayList();
//        // 获取该对象的class对象
//        // 获得该类的所有属性
//        Field[] fields = class_.getDeclaredFields();
//
//        // 读取excel数据
//        // 获得指定的excel表
//        HSSFSheet sheet = workbook.getSheet(sheetName);
//        // 获取表格的总行数
//        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
//        logger.info("rowCount:" + rowCount);
//        if (rowCount < 1) {
//            return result;
//        }
//        // 获取表头的列数
//        int columnCount = sheet.getRow(0).getLastCellNum();
//        // 读取表头信息,确定需要用的方法名---set方法
//        // 用于存储方法名
//        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数
//        // 用于存储属性类型
//        String[] fieldTypes = new String[columnCount];
//        // 获得表头行对象
//        HSSFRow titleRow = sheet.getRow(0);
//        // 遍历
//        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列
//            String data = partsPropertis[columnIndex];
//            String Udata = Character.toUpperCase(data.charAt(0))
//                    + data.substring(1, data.length()); // 使其首字母大写
//            methodNames[columnIndex] = "set" + Udata;
//            for (int i = 0; i < fields.length; i++) { // 遍历属性数组
//                if (data.equals(fields[i].getName())) { // 属性与表头相等
//                    fieldTypes[columnIndex] = fields[i].getType().getName(); // 将属性类型放到数组中
//                }
//            }
//        }
//        // 逐行读取数据 从1开始 忽略表头
//        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
//            // 获得行对象
//            HSSFRow row = sheet.getRow(rowIndex);
//            if (row != null) {
//                Object obj = null;
//                // 实例化该泛型类的对象一个对象
//                try {
//                    obj = class_.newInstance();
//                } catch (Exception e1) {
//                    logger.error(e1.toString());
//                }
//                // 获得本行中各单元格中的数据
//                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
//                    String data = row.getCell(columnIndex).toString();
//                    // 获取要调用方法的方法名
//                    String methodName = methodNames[columnIndex];
//                    Method method = null;
//                    try {
//                        // 这部分可自己扩展
//                        if (fieldTypes[columnIndex].equals("java.lang.String")) {
//                            method = class_.getDeclaredMethod(methodName,
//                                    String.class); // 设置要执行的方法--set方法参数为String
//                            method.invoke(obj, data); // 执行该方法
//                        } else if (fieldTypes[columnIndex].equals("int")) {
//                            method = class_.getDeclaredMethod(methodName,
//                                    int.class); // 设置要执行的方法--set方法参数为int
//                            double data_double = Double.parseDouble(data);
//                            int data_int = (int) data_double;
//                            method.invoke(obj, data_int); // 执行该方法
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.toString());
//                    }
//                }
//                result.add(obj);
//            }
//        }
//        return result;
//    }


//    public <E> List<E> parseExcel(CommonsMultipartFile excelFile, HttpServletRequest request, List<T> datas, Class clazz) {
//        String updatePath = request.getServletContext().getRealPath("upload");
//        String fileName = excelFile.getOriginalFilename();
//        File file = new File(updatePath, fileName);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        try {
//            excelFile.transferTo(file);
//            return readFromExcel(fileName, "sheet", clazz);
//        } catch (IOException e) {
//            logger.error(e.toString());
//        }
//        return null;
//    }

    public static <E> SXSSFWorkbook createExcelOfStandard(int sheetType, String sheetName, String titleRow[], List<E> data, String properties[]) {
        //创建workbook
        //workbook = new HSSFWorkbook();
        sxssfWorkbook = new SXSSFWorkbook(-1);
        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        //Sheet sheet = workbook.createSheet(sheetName);
        SXSSFSheet sheet = sxssfWorkbook.createSheet(sheetName);
        try {

            //添加表头
            Row row = sxssfWorkbook.getSheet(sheetName).createRow(0);    //创建第一行
            for (int i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            if (!Objects.isNull(data) && !data.isEmpty()) {
                for (int columnIndex = 0; columnIndex < data.size(); columnIndex++) {
                    Object object = data.get(columnIndex);
                    Row newrow = sheet.createRow(columnIndex + 1);
                    newrow.createCell(0).setCellValue(columnIndex + 1);
                    int colindex = 0;
                    for (String columnid : properties) {  //columnCount-1，因为第一个序号，没有properties设置
                        String datavalue = BeanUtils.getProperty(object, columnid);
                        newrow.createCell(colindex + 1).setCellValue(datavalue);

                        colindex++;
                    }
                }
            }
            setColumnWidthByType(sheet,titleRow.length);
            //合计
            return sxssfWorkbook;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

    public static <E> SXSSFWorkbook createExcelOfStandardWithNum(int sheetType, String sheetName, String titleRow[], List<E> data, String properties[],String[] reservedDigit) {
        //创建workbook
        //workbook = new HSSFWorkbook();
        sxssfWorkbook = new SXSSFWorkbook(-1);
        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        //Sheet sheet = workbook.createSheet(sheetName);
        SXSSFSheet sheet = sxssfWorkbook.createSheet(sheetName);

        // 保存cell style，重复使用，避免创建太多
        Map<String,CellStyle> cellStyleMap = new HashMap<>();

        try {
            //添加表头
            Row row = sxssfWorkbook.getSheet(sheetName).createRow(0);    //创建第一行
            for (int i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            if (!Objects.isNull(data) && !data.isEmpty()) {
                if(reservedDigit!=null){

                    for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                        Object object = data.get(rowIndex);
                        Row newrow = sheet.createRow(rowIndex + 1);    // 索引0对应表头行，索引1对应第一个数据行
                        newrow.createCell(0).setCellValue(rowIndex + 1);    // 为该行创建第0列（序号列，该行是第几个数据行就填几）
                        int colindex = 0;
                        for (String columnname : properties) {  //columnCount-1，因为第一个序号，没有properties设置
                            String datavalue = BeanUtils.getProperty(object, columnname);
                            //如果单元格内容是数值类型，则设置cell的类型为数值型，设置data的类型为数值类型
                            //CellStyle contextstyle =sxssfWorkbook.createCellStyle();
                            Cell contentCell = newrow.createCell(colindex + 1);
                            if (!reservedDigit[colindex+1].equals("")) {

                                contentCell.setCellStyle(determinCellStyle(reservedDigit[colindex+1],sxssfWorkbook,cellStyleMap));

                                // 设置单元格内容为double类型
                                contentCell.setCellValue(StringUtils.isEmpty(datavalue)||datavalue.equals("null") ? 0 : Double.parseDouble(datavalue));
                            } else {
                                // contentCell.setCellStyle(contextstyle);
                                // 设置单元格内容为字符型
                                contentCell.setCellValue(datavalue);
                            }
                            colindex++;
                        }
                    }
                }
                else{
                    for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                        Object object = data.get(rowIndex);
                        Row newrow = sheet.createRow(rowIndex + 1);
                        newrow.createCell(0).setCellValue(rowIndex + 1);
                        int colindex = 0;
                        for (String columnname : properties) {  //columnCount-1，因为第一个序号，没有properties设置
                            String datavalue = BeanUtils.getProperty(object, columnname);
                            newrow.createCell(colindex + 1).setCellValue(datavalue);
                            colindex++;
                        }
                    }
                }
            }
            setColumnWidthByType(sheet,titleRow.length);
            //合计
            return sxssfWorkbook;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

    public static <E> HSSFWorkbook createExcelOfStandard2(int sheetType, String sheetName, String titleRow[], List<E> data, String properties[]) {
        //创建workbook
        workbook = new HSSFWorkbook();
        //sxssfWorkbook = new SXSSFWorkbook();
        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //SXSSFSheet sheet = sxssfWorkbook.createSheet(sheetName);
        try {
            setSizeColumn(sheet);
            //添加表头
            Row row = workbook.getSheet(sheetName).createRow(0);    //创建第一行
            for (int i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            if (!Objects.isNull(data) && !data.isEmpty()) {
                for (int columnIndex = 0; columnIndex < data.size(); columnIndex++) {
                    Object object = data.get(columnIndex);
                    Row newrow = sheet.createRow(columnIndex + 1);
                    newrow.createCell(0).setCellValue(columnIndex + 1);
                    int colindex = 0;
                    for (String columnid : properties) {  //columnCount-1，因为第一个序号，没有properties设置
                        String datavalue = BeanUtils.getProperty(object, columnid);
                        newrow.createCell(colindex + 1).setCellValue(datavalue);
                        colindex++;
                    }
                }
            }

            //合计
            return workbook;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }
    //根据类型指定excel文件的列宽
    private static void setColumnWidthByType(SXSSFSheet sheet,int titleLength) {
        sheet.trackAllColumnsForAutoSizing();
        for(int i=0;i<titleLength;i++){
            int columnWidth = sheet.getRow(0).getCell(i).getStringCellValue().length();//获取表头的宽度
            int autowidth=(int)SheetUtil.getColumnWidth(sheet,i,false,1,sheet.getLastRowNum());
            if(columnWidth>autowidth){
                sheet.setColumnWidth(i, (int)256.0D*(columnWidth+1));
            }else{
                sheet.autoSizeColumn(i);
            }
        }
    }

    //将数字转为三位
    public static String getDataWith000(int count){
        count++;
        String scount="";
        try{
            DecimalFormat df=new DecimalFormat("000");
            scount = df.format(count);
        }catch (NumberFormatException e){
            logger.error(e.toString());
        }
        return scount;
    }

    // 自适应宽度(中文支持)
    private static void setSizeColumn(HSSFSheet sheet) {
        for (int columnNum = 0; columnNum <= 8; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                HSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    HSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }


    public static CellStyle determinCellStyle(String digitalType,SXSSFWorkbook sxssfWorkbook,Map<String,CellStyle> cellStyleMap) {
        CellStyle cellStyle = null;
        if(sxssfWorkbook!=null && !StringUtils.isEmpty(digitalType)) {
            if(cellStyleMap.keySet().contains(digitalType)){
                cellStyle = cellStyleMap.get(digitalType);
            }else {
                DataFormat df = sxssfWorkbook.createDataFormat(); // 此处设置数据格式
                cellStyle = sxssfWorkbook.createCellStyle();
                if (cellStyle != null && df != null) {
                    cellStyle.setDataFormat(df.getFormat(digitalType));
                }
                cellStyleMap.put(digitalType,cellStyle);
            }
        }
        return cellStyle;
    }
}
