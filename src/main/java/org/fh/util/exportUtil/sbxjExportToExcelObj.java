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
public class sbxjExportToExcelObj extends AbstractXlsView {

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		// 整改前后照片路径
		List<List<String>> Img = (List<List<String>>) model.get("Img");
		// 文件名
		String name = (String) model.get("filename");
		Date date = new Date();
		String filename = name + "_巡检表_" + DateUtil.date2Str(date, "yyyyMMddHHmmss");
		filename = encodeFilename(filename + ".xls", request);
		// 创建Excel文件的输入流对象
		FileInputStream fis = new FileInputStream(PathUtil.getProjectpath() + "uploadFiles/file/sbxjExportTemplate/sbxjExportTemplate.xls");
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
		// 给指定的sheet命名
		HSSFWorkbook.setSheetName(0, name + "巡检台账");
		// 表头样式
		// 11、12列加宽
		// sheet.setColumnWidth(11, 30 * 256);
		// sheet.setColumnWidth(12, 30 * 256);
		List<String> titles = (List<String>) model.get("titles");
		int len = titles.size();
		HSSFCellStyle cellStyle = HSSFWorkbook.createCellStyle();
		HSSFDataFormat format = HSSFWorkbook.createDataFormat();
		cellStyle.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
		HSSFCellStyle headerStyle = HSSFWorkbook.createCellStyle(); // 标题样式
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		HSSFFont headerFont = HSSFWorkbook.createFont(); // 标题字体 
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 13);
		headerFont.setFontName("黑体");
		headerStyle.setFont(headerFont);
		short height = 25 * 20;
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < len; i++) {
			String title = titles.get(i);
			row.setRowStyle(headerStyle);
			row.createCell(i).setCellValue(title);
		}
		sheet.getRow(0).setHeight(height);
		HSSFCellStyle contentStyle = HSSFWorkbook.createCellStyle(); // 内容样式
		contentStyle.setAlignment(HorizontalAlignment.CENTER);
		contentStyle.setWrapText(true);
		List<PageData> varList = (List<PageData>) model.get("varList");
		int varCount = varList.size();
		for (int i = 0; i < varCount; i++) {
			PageData vpd = varList.get(i);
			List<String> vImg = Img.get(i);
			HSSFRow rows = sheet.createRow(i + 1);
			for (int j = 0; j < len; j++) {
				String varstr = "";
				if (vpd.getString("var" + (j + 1)) != null && !vpd.getString("var" + (j + 1)).equals("null")) {
					varstr = vpd.getString("var" + (j + 1));
				}
				rows.setHeight((short) (height * 1));
				rows.setRowStyle(contentStyle);
				rows.createCell(j).setCellValue(varstr);
			}
			// 写入图片
			for(int z = 0;z<vImg.size();z++){
				ExcelImage(HSSFWorkbook, sheet, i + 1, 8 + z, PathUtil.getProjectpath() + vImg.get(z));
			}
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
