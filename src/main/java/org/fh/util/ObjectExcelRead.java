package org.fh.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Registered;


/**
 * 说明：从EXCEL导入到系统
 * 作者：f-sci
 * 授权：bsic
 */
public class ObjectExcelRead {

	/**
	 * @param filepath //文件路径
	 * @param filename //文件名
	 * @param startrow //开始行号
	 * @param startcol //开始列号
	 * @param sheetnum //sheet
	 * @return list
	 */
	@SuppressWarnings("resource")
	public static List<Object> readExcel(String filepath, String filename, int startrow, int startcol, int sheetnum) {
		List<Object> varList = new ArrayList<Object>();
		try {
			File target = new File(filepath, filename);
			FileInputStream fi = new FileInputStream(target);
			HSSFWorkbook wb = new HSSFWorkbook(fi);
			HSSFSheet sheet = wb.getSheetAt(sheetnum); 					//sheet 从0开始
			int rowNum = sheet.getLastRowNum() + 1; 					//取得最后一行的行号
			for (int i = startrow; i < rowNum; i++) {					//行循环开始
				PageData varpd = new PageData();
				HSSFRow row = sheet.getRow(i); 							//行
				int cellNum = row.getLastCellNum(); 					//每行的最后一个单元格位置
				for (int j = startcol; j < cellNum; j++) {				//列循环开始
					HSSFCell cell = row.getCell(Short.parseShort(j + ""));
					varpd.put("var"+j, cell.toString());
				}
				varList.add(varpd);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return varList;
	}
	
	/**
	 * 读一卡通数据白名单专用
	 * @param filepath //文件路径
	 * @param filename //文件名
	 * @param startrow //开始行号
	 * @param startcol //开始列号
	 * @param sheetnum //sheet
	 * @return list
	 */
	@SuppressWarnings("resource")
	public static List<Registered> readExcelFromOneCardExcel(String filepath, String filename, int startrow, int startcol, int sheetnum) {
		List<Registered> varList = new ArrayList<Registered>();
		try {
			File target = new File(filepath, filename);
			FileInputStream fi = new FileInputStream(target);
			HSSFWorkbook wb = new HSSFWorkbook(fi);
			HSSFSheet sheet = wb.getSheetAt(sheetnum); 					//sheet 从0开始
			int rowNum = sheet.getLastRowNum() + 1; 					//取得最后一行的行号
			for (int i = startrow; i < rowNum; i++) {					//行循环开始
				Registered temp = new Registered();
				HSSFRow row = sheet.getRow(i); 							//行
				// int cellNum = row.getLastCellNum(); 					//每行的最后一个单元格位置
				// 列1 --> 身份证号
				HSSFCell cell = row.getCell(Short.parseShort(1 + ""));
				temp.setCARD(cell.toString());
				// 列2 --> 姓名
				cell = row.getCell(Short.parseShort(2 + ""));
				temp.setNAME(cell.toString());
				// 注册状态和电话不同步
				temp.setREGISTERED("0");
				temp.setPHONE("");
				// 列5 --> 签约单位
				cell = row.getCell(Short.parseShort(5 + ""));
				temp.setLaborRelation(cell.toString());
				// 列6 --> 服务部门
				cell = row.getCell(Short.parseShort(6 + ""));
				temp.setServicesDepartment(cell.toString());
				// 列7 --> 用工属性
				cell = row.getCell(Short.parseShort(7 + ""));
				temp.setEmployeeType(cell.toString());
				// 列8 --> 工号
				cell = row.getCell(Short.parseShort(8 + ""));
				temp.setUserNo(cell.toString());
				// 列9  --> 最初用工部门
				cell = row.getCell(Short.parseShort(9 + ""));
				temp.setEMPLOYER(cell.toString()=="null"?"":cell.toString());
				// 列10 --> 用工方式
				cell = row.getCell(Short.parseShort(10 + ""));
				temp.setWAYS(cell.toString()=="null"?"":cell.toString());
				// 列11 --> 专业
				cell = row.getCell(Short.parseShort(11 + ""));
				temp.setMAJOR(cell.toString()=="null"?"":cell.toString());
				// 列12 --> 岗位
				cell = row.getCell(Short.parseShort(12 + ""));
				temp.setSTATION(cell.toString()=="null"?"":cell.toString());
				// 列13 --> 性别
				cell = row.getCell(Short.parseShort(13 + ""));
				temp.setGENDER(cell.toString()=="null"?"":cell.toString());
				// 列14 --> 工种
				cell = row.getCell(Short.parseShort(14 + ""));
				temp.setJOB(cell.toString()=="null"?"":cell.toString());
				// 列15 --> 人像照片
				cell = row.getCell(Short.parseShort(15 + ""));
				temp.setPHOTO(cell.toString()=="null"?"":cell.toString());
				// 列16 --> 班组
				cell = row.getCell(Short.parseShort(16 + ""));
				temp.setTEAM(cell.toString()=="null"?"":cell.toString());
				// 列17 --> 0为在岗，1为不在岗
				cell = row.getCell(Short.parseShort(17 + ""));
				temp.setONJOB(cell.toString()=="null"?"":cell.toString());
				// 列18 --> http地址上的照片
				cell = row.getCell(Short.parseShort(18 + ""));
				temp.setPHOTO_URL(cell.toString()=="null"?"":Const.ONECARD_URL + cell.toString());
				// 加入列表
				varList.add(temp);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return varList;
	}
}
