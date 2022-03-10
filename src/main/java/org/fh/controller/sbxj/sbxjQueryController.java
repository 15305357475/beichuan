package org.fh.controller.sbxj;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.fh.service.sbxj.SbxjMediaService;
import org.fh.service.sbxj.sbxjQueryService;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.ImageAnd64Binary;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.exportUtil.sbxjExportToExcelObj;
import org.fh.util.exportUtil.sbxjExportToExcelObj2;

@Controller

@RequestMapping("/sbxjQuery")
public class sbxjQueryController extends AcStartController {
	@Autowired
	private sbxjQueryService sbxjqueryService;
	@Autowired
	private SbxjMediaService SbxjMediaService;
	/**
	 * 统计与导出 
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/statistics")
	@ResponseBody
	public Object Statistics(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		page.setPd(pd);
		List<PageData> varList = sbxjqueryService.DataStatisticslist(page);
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	
	/**
	 * 统计与导出
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/insExport")
	public ModelAndView insExport() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 构造Excel
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("序号"); // 0
		titles.add("设备编码"); // 1
		titles.add("设备名"); // 2
		titles.add("检查人"); // 3
		titles.add("检查方式"); // 4
		titles.add("检查时间"); // 5
		titles.add("部门"); // 6
		titles.add("检查清单合格率"); // 7
//		titles.add("图片1"); // 8
//		titles.add("图片2"); // 9
//		titles.add("图片3"); // 10
//		titles.add("图片4"); // 11
		dataMap.put("titles", titles);
		List<PageData> varOList = sbxjqueryService.DataStatisticsExport(pd);
		List<PageData> varList = new ArrayList<PageData>();
		List<List<String>> Img = new ArrayList<List<String>>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", Integer.toString(i + 1)); // 0
			vpd.put("var2", varOList.get(i).getString("sb_no")); // 1
			vpd.put("var3", varOList.get(i).getString("EQUIPMENT_NAME")); // 2
			vpd.put("var4", varOList.get(i).getString("user_name")); // 3
			vpd.put("var5", varOList.get(i).getString("type_name")); // 4
			try {
				DateFormat df = DateUtil.createDateFormat();
				String f = df.format(varOList.get(i).get("CHECK_DATE"));
				vpd.put("var6",f); // 5
			} catch (Exception e) {
				vpd.put("var6", "");
			}
			vpd.put("var7", varOList.get(i).getString("dept_name")); // 6
			vpd.put("var8", varOList.get(i).getString("BZ")); // 7
			varList.add(vpd);	
			PageData imgTempPd = new PageData();
			imgTempPd.put("XJ_ID", varOList.get(i).getString("ID"));
			List<PageData> BAIMGS = new ArrayList<PageData>();
			BAIMGS = sbxjqueryService.getImgByID(imgTempPd);
			List<String> AImg = new ArrayList<String>();
			for(int j = 0 ; j<BAIMGS.size() ; j++) {
				AImg.add(BAIMGS.get(j).getString("MEDIA_PATH"));
			}
			Img.add(AImg);
//			if(BAIMGS.size() == 0) {// 没有照片
//				ImgOne.add("NONE");
//				ImgTwo.add("NONE");
//				ImgThree.add("NONE");
//				ImgFour.add("NONE");
//			}
//			else if (BAIMGS.size() == 1) {
//				PageData one = BAIMGS.get(0);
//				ImgOne.add(one.getString("MEDIA_PATH"));
//				ImgTwo.add("NONE");
//				ImgThree.add("NONE");
//				ImgFour.add("NONE");
//			}
		}
		dataMap.put("varList", varList);
		dataMap.put("filename", pd.getString("FILENAME"));
		dataMap.put("Img",Img);
		// 返回Excel实例
		sbxjExportToExcelObj erv = new sbxjExportToExcelObj();
		mv = new ModelAndView(erv, dataMap);
		return mv;
		}
	
	/**
	 * 按设备编号查
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		page.setPd(pd);
		List<PageData> varList = sbxjqueryService.listBySbNolistPage(page); // 列出巡检列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/findById")
	@ResponseBody
	public Object findById(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		page.setPd(pd);
		List<PageData> varList = sbxjqueryService.findById(page); // 列出巡检列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/findByUserId")
	@ResponseBody
	public Object findByUserId(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		page.setPd(pd);
		pd.put("CHECK_USER", Jurisdiction.getUsername()); // 发起人，当前系统用户
		List<PageData> varList = sbxjqueryService.findByUserId(page); // 列出巡检列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/listBySbNo")
	@ResponseBody
	public Object listBySbNo() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varList = sbxjqueryService.listBySbNo(pd); // 列出巡检列表
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 查询问题
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getAllQu")
	@ResponseBody
	public Object getAllQu() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varList = sbxjqueryService.getAllQu(pd);
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 检查维修人是否为自己
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/loadingOverBtn")
	@ResponseBody
	public Object loadingOverBtn() throws Exception {
		String this_user = Jurisdiction.getUsername(); //获取当前登录人
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		if(pd.getString("NEXT_USER").equals(this_user)) {
			map.put("result", "1");
		}
		else {
			map.put("result", "0");
		}
		return map;
	}
	/**
	 * 封闭问题
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/OverQst")
	@ResponseBody
	public Object OverQst() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("END_DATE", DateUtil.getTime());// 封闭时间
		sbxjqueryService.OverQst(pd); // 记录存入数据库
		
		if (!pd.getString("LIVEPHOTO").equals("NONE")) {
			String PATHS[] = pd.getString("LIVEPHOTO").split(",");
			List<String> INSMEDIA_IDs = new ArrayList<String>();
			int i = 0;
			for (String onePath : PATHS) {
				PageData tempPd = new PageData();
				String INSMEDIA_ID = this.get32UUID();
				tempPd.put("ID", INSMEDIA_ID);
				tempPd.put("XJ_ID", pd.getString("ID"));
				tempPd.put("PROC_INST_ID_", "null");
				tempPd.put("NODE", "封闭");
				tempPd.put("CREATE_USER", Jurisdiction.getUsername());
				tempPd.put("MEDIA_TYPE", "image/*");
				tempPd.put("MEDIA_NAME", ".jpg");
				tempPd.put("MEDIA_PATH", onePath);
				tempPd.put("STATUS", "1");
				i++;
			try {
				SbxjMediaService.save(tempPd);
				INSMEDIA_IDs.add(INSMEDIA_ID);
				} catch (Exception e) {
					map.put("errorMsg", e.getMessage());
					map.put("result", "error"); // 返回结果
					return map;
				}
			}
		}
		map.put("result", "success");
		return map;
	}
	
	/**
	 * 上传现场照片(手机端)
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/LivePhoto")
	@ResponseBody
	public Object LivePhoto() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String UUID = this.get32UUID();

		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		String pathimg = PathUtil.getProjectpath() + Const.SBXJ_IMGS + year + "//" + month + "//" + UUID + ".jpg";
		if (pd.getString("PHOTODATA").indexOf("data:image/") != -1) {
			String imgData = pd.getString("PHOTODATA").split(",")[1];
			boolean flag = ImageAnd64Binary.generateImage(imgData, pathimg);
			if (!flag) {
				errInfo = "error";
			}
			map.put("img64", imgData);
		} else {
			errInfo = "error";
		}
		map.put("imgpath", Const.SBXJ_IMGS + year + "//" + month + "//" + UUID + ".jpg");
		map.put("result", errInfo);
		return map;
	}
	
	/**
	 * 问题清单 
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/QuestionlistPage")
	@ResponseBody
	public Object QuestionlistPage(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		page.setPd(pd);
		List<PageData> varList = sbxjqueryService.QuestionlistPage(page);
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 查询问题
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getAllQuBySbNo")
	@ResponseBody
	public Object getAllQuBySbNo() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varList = sbxjqueryService.getAllQuBySbNo(pd); 
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 删除问题
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/DelQst")
	@ResponseBody
	public Object DelQst() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("END_DATE", DateUtil.getTime());// 封闭时间
		sbxjqueryService.DelQst(pd); // 记录存入数据库
		map.put("result", "success");
		return map;
	}
	
	/**
	 * 统计与导出
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/ExportXj")
	public ModelAndView ExportXj() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String xj_id = pd.getString("XJ_ID");
		PageData pd1 = new PageData();
			pd1.put("XJ_ID", xj_id);
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<PageData> varList1 = sbxjqueryService.exportXjMain(pd1); // 设备名称、设备编号、巡检类型、巡检日期、巡检人、巡检id
			List<PageData> varList2 = sbxjqueryService.exportXjCpAll(pd1); // 检查点
			List<PageData> varList3 = sbxjqueryService.exportXjCpSelect(pd1); // 选择的检查点
			String checkedCps = varList3.get(0).get("CHECK_POINT").toString();
			String[] checkedCp = checkedCps.split(",");
			outfor:for(int i=0;i<varList2.size();i++) {
				String allCps = varList2.get(i).get("DICTIONARIES_ID").toString();
				// 检查合格项打钩，不合格打叉
				for(int j=0;j<checkedCp.length;j++) {
					if(allCps.equals(checkedCp[j])) {
						varList2.get(i).put("CHECKED", "√");
						continue outfor;
					}else {
						varList2.get(i).put("CHECKED", "×");
						
					}
				}
			}
			for(int j=0;j<varList2.size();j++) {
				String allCp = varList2.get(j).get("DICTIONARIES_ID").toString();
				String DIC_ID = varList2.get(j).get("DICTIONARIES_ID").toString();// 问题id
				PageData dp = new PageData();
				dp.put("CHECK_POINT", allCp);
				dp.put("XJ_ID", xj_id);
				List<PageData> varList4 = sbxjqueryService.exportXjQuestion(dp); // 问题清单
				if(varList4.size() == 0) {
					varList2.get(j).put("QUESTION", "—");
				}else {
					varList2.get(j).put("QUESTION",varList4.get(0).get("NOTE").toString()); //问题描述插入varlist2
					for(int k=0;k<varList4.size();k++) {
						String WT_ID = varList4.get(k).getString("ID").toString();
						PageData dp2 = new PageData();
						dp2.put("WT_ID", WT_ID);
						List<PageData> varList5 = SbxjMediaService.getImgByProc(dp2); // 图片清单
						if(varList5.size() == 0) {
							varList2.get(j).put("START_IMG", "NONE");
							varList2.get(j).put("END_IMG", "NONE");
						}else if(varList5.size() == 1) {
							PageData one = varList5.get(0);
							if (one.getString("NODE").equals("创建")) {
								varList2.get(j).put("START_IMG", one.getString("MEDIA_PATH"));
								varList2.get(j).put("END_IMG", "NONE");
							}
							if (one.getString("NODE").equals("封闭")) {
								varList2.get(j).put("START_IMG", "NONE");
								varList2.get(j).put("END_IMG", one.getString("MEDIA_PATH"));
							}
						}else if(varList5.size() == 2) {
							PageData one = varList5.get(0);
							if (one.getString("NODE").equals("创建")) {
								varList2.get(j).put("START_IMG", one.getString("MEDIA_PATH"));
							}
							if (one.getString("NODE").equals("封闭")) {
								varList2.get(j).put("END_IMG", one.getString("MEDIA_PATH"));
							}
							PageData two = varList5.get(1);
							if (two.getString("NODE").equals("创建")) {
								varList2.get(j).put("START_IMG", two.getString("MEDIA_PATH"));
							}
							if (two.getString("NODE").equals("封闭")) {
								varList2.get(j).put("END_IMG", two.getString("MEDIA_PATH"));
							}
						}
					}
				}
			}
			
			dataMap.put("xjMain", varList1);
			dataMap.put("xjAllCp", varList2);
//			dataMap.put("varList", varList3);
			// 返回Excel实例
			sbxjExportToExcelObj2 erv = new sbxjExportToExcelObj2();
			mv = new ModelAndView(erv, dataMap);
		
		return mv;
		}
	
	}