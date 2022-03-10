package org.fh.util.exportUtil;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.fh.util.DateUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.document.AbstractXlsView;

/**
 * 说明：导出部门隐患分类汇总表Excel对象 作者：f-sci 授权：bsic
 */
public class InsExportToExcelObj2 extends AbstractXlsView {

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Date date = new Date();
		String filename = DateUtil.date2Str(date, "yyyyMMddHHmmss");
		filename = encodeFilename("部门隐患分类汇总表_" + filename + ".xls",request);
		HSSFSheet sheet;
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename="+filename);
		HSSFWorkbook book = (HSSFWorkbook) workbook;
		sheet = book.createSheet("部门隐患分类汇总表");
		List<String> uniqueInsDept = (List<String>) model.get("uniqueInsDept");
		List<String> uniqueInsType = (List<String>) model.get("uniqueInsType");
		List<List<String>> rows = (List<List<String>>) model.get("rows");
		LoadData(book,sheet,uniqueInsDept,uniqueInsType,rows);
	}

	public void LoadData(HSSFWorkbook wb, HSSFSheet sheet,List<String> uniqueInsDept,List<String> uniqueInsType,List<List<String>> rows) {
		HSSFRow row = sheet.createRow(0);
		// 装入隐患类型
		for (int i = 0; i < uniqueInsType.size(); i++) {
			row.createCell(i+1).setCellValue(uniqueInsType.get(i));
		}
		// 装载部门和隐患数量
		for (int i = 0; i < uniqueInsDept.size(); i++) {
			List<String> oneRow = rows.get(i);
			HSSFRow rowOne = sheet.createRow(i+1);
			rowOne.createCell(0).setCellValue(uniqueInsDept.get(i));
			for (int j = 0; j < oneRow.size(); j++) {
				int index = j+1;
				rowOne.createCell(index).setCellValue(oneRow.get(j));
			}
		}
	}

	/**
	 * 设置下载文件中文件的名称
	 * 
	 * @param filename
	 * @param request
	 * @return
	 */
	public static String encodeFilename(String filename, HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT");
		try {
			if ((agent != null) && (-1 != agent.indexOf("MSIE"))) {
				String newFileName = URLEncoder.encode(filename, "UTF-8");
				newFileName = StringUtils.replace(newFileName, "+", "%20");
				if (newFileName.length() > 150) {
					newFileName = new String(filename.getBytes("GB2312"), "ISO8859-1");
					newFileName = StringUtils.replace(newFileName, " ", "%20");
				}
				return newFileName;
			}
			if ((agent != null) && (-1 != agent.indexOf("Mozilla")))
				return MimeUtility.encodeText(filename, "UTF-8", "B");

			return filename;
		} catch (Exception ex) {
			return filename;
		}
	}
}
