package com.sunrun.etool.tool.excel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class ExcelTool {

    /**
     * 判断当前文件是否为Excel文件
     * @param filePath
     * @return
     */
    public static boolean isExcel(String filePath){
        if(filePath.endsWith(".xls") || filePath.endsWith(".xlsx")){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 读取excel指定单元格的信息，注意可能有多张sheet
     * @param filePath 文件路径
     * @param row 行标
     * @param col 列表
     * @return 单元格信息
     */
    public static List<String> readExcelSingleCell(String filePath, ExcelTypeEnum type, int row, int col) {
        try {
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener('7'-'1','B' - 'A');
            ExcelReader excelReader = new ExcelReader(new FileInputStream(new File(filePath)), type,null, listener);
            excelReader.read();
            return listener.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * 读取excel指定单元格的信息，注意可能有多张sheet
     * @param file 文件
     * @param row 行标
     * @param col 列表
     * @return 单元格信息
     */
    public static List<String> readExcelSingleCell(File file, ExcelTypeEnum type, int row, int col) {
        try {
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener('7'-'1','B' - 'A');
            ExcelReader excelReader = new ExcelReader(new FileInputStream(file), type,null, listener);
            excelReader.read();
            return listener.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

}
