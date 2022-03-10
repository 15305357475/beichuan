package org.fh.util.exportUtil;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.fh.entity.PageData;
import org.fh.util.DateUtil;
import org.fh.util.PathUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.document.AbstractXlsView;

/**
 * 说明：设备巡检导出Excel对象 作者：f-sci 授权：bsic
 */
public class sbxjExportToExcelObj2 extends AbstractXlsView {

	@SuppressWarnings({ "unchecked", "resource" })
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<PageData> xjMain = (List<PageData>) model.get("xjMain");
		short height = 25 * 20;
		// 整改前后照片路径
		List<String> beforeImg = (List<String>) model.get("beforeImg");
		List<String> afterImg = (List<String>) model.get("afterImg");
		// 文件名
		String name =  xjMain.get(0).getString("SB_NAME");
		Date date = new Date();
		String filename = name + "_巡检表_" + DateUtil.date2Str(date, "yyyyMMddHHmmss");
		filename = encodeFilename(filename + ".xls", request);
		// 创建Excel文件的输入流对象
		FileInputStream fis = new FileInputStream(PathUtil.getProjectpath() + "uploadFiles/file/sbxjExportTemplate/sbxjExportFile.xls");
		// 根据模板创建Excel工作簿
		workbook = new HSSFWorkbook(fis);
		// 创建Excel文件输出流对象
		OutputStream out = response.getOutputStream();
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		// 将workbook强转成需要的HSSFBook
		HSSFWorkbook HSSFWorkbook = (HSSFWorkbook) workbook;
		// 获取创建工作簿的第一页
		HSSFSheet sheet = HSSFWorkbook.getSheetAt(0);
		sheet.getRow(0).setHeight(height);
		// 给指定的sheet命名
		HSSFWorkbook.setSheetName(0, name + "巡检表");
		// 第一行（题目）
		HSSFRow row0 = sheet.getRow(0);
		row0.getCell(0).setCellValue(name+"设备"+xjMain.get(0).getString("TYPE_NAME")+"表");
		// 第二行内容赋值
		HSSFRow row1 = sheet.getRow(1);
		String a = xjMain.get(0).getString("SB_NAME"); // 设备名称
		String b = xjMain.get(0).getString("EQUIPMENT_ID"); // 设备名称
		String c = xjMain.get(0).getString("TYPE_NAME"); // 设备名称
		String d = xjMain.get(0).get("CHECK_DATE").toString(); // 设备名称
		String f = xjMain.get(0).getString("CHECK_USER"); // 设备名称
		try {
			HSSFRow row2 = sheet.getRow(1);
			row2.createCell(3).setCellValue(a); // 设备名称
		}catch(Exception e){
			
		}
		try {
			HSSFRow row2 = sheet.getRow(1);
			row2.createCell(1).setCellValue(b); // 设备编号
		}catch(Exception e){
			
		}
		
		try {
			HSSFRow row2 = sheet.getRow(1);
			row2.createCell(5).setCellValue(c); // 巡检类型
		}catch(Exception e){
			
		}
		try {
			HSSFRow row2 = sheet.getRow(1);
			row2.createCell(7).setCellValue(d); // 检查时间
		}catch(Exception e){
			
		}
		try {
			HSSFRow row2 = sheet.getRow(1);
			row2.createCell(10).setCellValue(f); // 检查人
		}catch(Exception e){
			
		}
		
		// 检查点赋值
		List<PageData> xjAllCp = (List<PageData>) model.get("xjAllCp");
		int alkdfj = xjAllCp.size();
		for(int i=0;i<xjAllCp.size();i++) {
			HSSFRow row = sheet.getRow(i+3);
			HSSFRow row3 = sheet.getRow(i+3);
			try {
				row3.createCell(0).setCellValue((i+1)+"");
			}catch(Exception e){
				
			}
			try {
				row3.createCell(1).setCellValue(xjAllCp.get(i).getString("BZ"));
			}catch(Exception e){
				
			}
			try {
				row3.createCell(7).setCellValue(xjAllCp.get(i).getString("CHECKED"));
			}catch(Exception e){
				
			}
			try {
				row3.createCell(8).setCellValue(xjAllCp.get(i).getString("QUESTION"));
			}catch(Exception e){
				
			}
			// 写入图片
			String tempBefore = xjAllCp.get(i).getString("START_IMG");
			String tempAfter = xjAllCp.get(i).getString("END_IMG");
			if (!tempBefore.equals("NONE")) {
				ExcelImage(HSSFWorkbook, sheet, i+3, 10, PathUtil.getProjectpath() + tempBefore);
			}
			if (!tempAfter.equals("NONE")) {
				ExcelImage(HSSFWorkbook, sheet, i+3, 11, PathUtil.getProjectpath() + tempAfter);
			}
				row.setHeight((short) (height * 6));
			
		}
		HSSFWorkbook.write(out);
		fis.close();
		out.flush();
		out.close();
	}

	public boolean ExcelImage(HSSFWorkbook wb, HSSFSheet sheet, int rowIndex, int columnIndex, String pictureUrl) {
		BufferedImage bufferImg = null;
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			bufferImg = ImageIO.read(new File(pictureUrl));
			ImageIO.write(bufferImg, "jpg", byteArrayOut);
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 250, (short) columnIndex, rowIndex,
					(short) columnIndex, rowIndex);
			patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("图片不存在：" + pictureUrl);
			return false;
		}
		return true;
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
