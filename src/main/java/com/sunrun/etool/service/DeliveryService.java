package com.sunrun.etool.service;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.sunrun.etool.data.request.DeliveryNumberData;
import com.sunrun.etool.message.ReturnMessage;
import com.sunrun.etool.tool.excel.ExcelTool;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryService {

    private final String TOTAL_FILE_PARAM = "total";

    private final String TOTAL_EXCEL_PARAM = "totalExcel";

    private final String KEY_STATE = "state";

    private final String KEY_MESSAGE = "message";


    /**
     * 三层结构的处理方式
     * @param data
     * @param isTreeStruct
     * @return
     */
    public HashMap<String,Object> start(DeliveryNumberData data, boolean isTreeStruct) {
        // 返回结果
        int totalFile = 0; //总文件数
        int totalCompany = 0; // 公司总数
        int totalExcel = 0; // excel文件总数
        HashMap<String, Object> result = new HashMap<>();
        result.put(KEY_STATE, false);

        // == 获取第一层结构
        File topFolder = new File(data.getFilePath());

        // 检测是否存在
        if (!topFolder.exists()) {
            result.put(KEY_MESSAGE, ReturnMessage.FILE_IS_NOT_EXIST);
            return result;
        }

        // 如果不是一个文件夹
        if (!topFolder.isDirectory()) {
            result.put(KEY_MESSAGE, ReturnMessage.FILE_IS_NOT_TOP_DIECTORY);
            return result;
        }

        // == 获取目录下的所有子文件
        File[] companyFolders = topFolder.listFiles();
        // 若没有子文件夹，该层文件夹是公司文件夹
        if (companyFolders.length == 0) {
            result.put(KEY_MESSAGE, ReturnMessage.FOLDER_HAS_NO_CHILD_FILE);
            return result;
        }

        // == 删除输出文件，如果存在的话
        String resultFile = "delivery_number.xls";
        if(!data.getResultPath().isEmpty()){
            resultFile = data.getResultPath() + "/" + "delivery_number.xls";
        }

        try {
            Files.delete(Paths.get(resultFile));
        } catch (Exception e) {
            System.out.println("no result file can delete...");
        }

        // ======== 依次分析每个公司的数据
        // 保存公司的名字，以后会作为表头
        List<String> tableHeader = new ArrayList<>();
        List<List<String>> analysisData = new ArrayList<>();
        for (File companyFolder : companyFolders) {
            // 存放公司的提单数组，会作为表的行输出，不对，列输出，之后我们会转换
            List<String> tableColumnItem = new ArrayList<>();
            // 若是文件，不予考虑
            if (!companyFolder.isDirectory()) {
                continue;
            }
            // 进一步解析底层的excel文件，请注意，我们现在是基于三层架构进行分析的
            String companyName = companyFolder.getName();

            totalCompany++;

            tableHeader.add(companyName);

            File[] companyChildFiles = companyFolder.listFiles();
            // 依次分析每个文件
            for (File companyChildFile : companyChildFiles) {
                totalFile++;
                System.out.println("============================================================");
                String fileName = companyChildFile.getName();
                String filePath = companyChildFile.getAbsolutePath();
                System.out.println("发现文件【" + filePath + "】，开始分析：");
                System.out.println("当前分析的文件总数为：" + totalFile);
                if (!ExcelTool.isExcel(companyChildFile.getName())) {
                    System.out.println("不是Excel类型文件，不予分析，继续查找下一个分析文件...");
                    continue;
                }
                // 当前解析的Excel总数+1
                totalExcel ++;

                // 获取解析结果
                ExcelTypeEnum type = fileName.endsWith(".xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX;
                List<String> list = ExcelTool.readExcelSingleCell(companyChildFile, type, '7' - '1', 'B' - 'A');
                System.out.println("得到结果：" + list);
                // 将结果写入
                if (list != null) {
                    List<String> filterContent = new ArrayList<>();
                    // 过滤内容
                    for (String s : list) {
                        // 替换掉多余的信息
                        // 新时代17批  403-47706514
                        // 烽火05批   '774159153300
                        String regexContent = cleanDelivery(s);
                        if(!regexContent.isEmpty()){
                            // 去重
                            if(!tableColumnItem.contains(regexContent)){
                                tableColumnItem.add(regexContent);
                            }

                        }
                    }
                }
            }
            // 汇总结果
            analysisData.add(tableColumnItem);
        }

        // ============ 分析并写入文件
        try (OutputStream out = new FileOutputStream(resultFile);) {
            // 创建输入工具
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLS);
            Sheet sheet1 = new Sheet(1, 0);
            // 设置表名
            sheet1.setSheetName("提单号");

            // 表头，由于api是双重List，需要转换一下
            List<List<String>> head = new ArrayList<List<String>>();
            for (String headerStr : tableHeader) {
                List<String> h = new ArrayList<String>();
                h.add(headerStr);
                head.add(h);
            }

            int cols = head.size();
            System.out.println("表格列数: " + cols);
            System.out.println("公司总数：" + totalCompany);

            // cols = analysisData.size()
            // 翻转二重列表，将行转换为列
            int maxValue = Integer.MAX_VALUE;
            // 保存越界数组的索引数组
            List<Integer> nowEnoughCompanyIndex =  new ArrayList<>();
            // 最终写入API的数据
            List<List<String>> finalTableData = new ArrayList<>();
            for (int i = 0; i < maxValue && nowEnoughCompanyIndex.size() < totalCompany; i++) {
                List<String> rowList = new ArrayList<>();
                for (int j = 0; j < analysisData.size(); j++) {
                    // 若已经有一个公司的提单号到达尾声，nowEnoughCompanyList + 1
                    if (i >= analysisData.get(j).size()) {
                        // 若该越界是该公司第一次发生，则增加长度
                        if(!nowEnoughCompanyIndex.contains(j)){
                            // 将该越界公司索引添加到集合中，下次不对他进行考察了
                            nowEnoughCompanyIndex.add(j);
                        }
                        rowList.add("");
                    } else {
                        // 翻转数据
                        rowList.add(analysisData.get(j).get(i));
                    }
                }
                // 设置api所需的第i行数据
                finalTableData.add(rowList);
            }

            // 调用api，写入writer
            Table table = new Table(1);
            table.setHead(head);
            writer.write0(finalTableData, sheet1, table);
            writer.finish();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        // 总文件数量
        result.put(TOTAL_FILE_PARAM, totalFile);
        // 总Excel文件数量
        result.put(TOTAL_EXCEL_PARAM, totalExcel);

        result.put(KEY_MESSAGE, "分析成功，输出结果已经保存在:" + resultFile);
        return result;
    }

    // 烽火06批   '774217704739
    // 新时代30批  403-47913762
    public String cleanDelivery(String content){
        return content.replaceAll("^[\\s]*[\\u4E00-\\u9FA5]*[\\d]*[\\u4E00-\\u9FA5]*[\\s]*[']*", "");
    }



}
