package com.sunrun.etool.tool.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 对excel中解析出来的内容做处理
 * @author 王业权
 * @date 2018-12-4
 */
public class ExcelListener extends AnalysisEventListener<List<String>> {

	//自定义用于获取某行某列数据
	private List<String> result;

	private final int row;

	private final int col;

	public ExcelListener(int row, int col) {
		this.row = row;
		this.col = col;
		result = new ArrayList<>();
	}

	public void invoke(List<String> rowContent, AnalysisContext context) {
		//System.out.println("当前行：" + context.getCurrentRowNum());
		if(context.getCurrentRowNum().equals(row)){
			System.out.println(rowContent.size());
			System.out.println(rowContent.get(col));
			String content = rowContent.get(col);
			if(content != null && !content.isEmpty()){
				result.add(content);
			}
		}
		//datas.add(object);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
		//doSomething(object);//根据自己业务做处理
	}
	private void doSomething(Object object) {
		//1、入库调用接口
	}
	public void doAfterAllAnalysed(AnalysisContext context) {
		// datas.clear();//解析结束销毁不用的资源
	}

	public List<String> getResult() {
		return result;
	}
}
