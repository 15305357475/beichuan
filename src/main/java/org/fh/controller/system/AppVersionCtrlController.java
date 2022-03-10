package org.fh.controller.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.system.AppVersionCtrlService;

/**
 * 说明：移动端App版本控制 作者：fsci 时间：2021-03-26 授权：bsic
 */
@Controller
@RequestMapping("/appversionctrl")
public class AppVersionCtrlController extends BaseController {

	@Autowired
	private AppVersionCtrlService appversionctrlService;

	/**
	 * 获取最新版本
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getLatest")
	@ResponseBody
	public Object getLatest() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = appversionctrlService.getLatest(pd); // 根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("appversionctrl:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("APPVERSIONCTRL_ID", this.get32UUID()); // 主键
		pd.put("VERSION_NAME", "1.0.0"); // 主版本名：1.0.0
		pd.put("VERSION_CODE", "0000"); // 子版本号：0326
		pd.put("PUBLISH", "1"); // 是否发布:1:发布；0：未发布
		pd.put("PUBLISH_DATE", DateUtil.date2Str(new Date())); // 发布日期
		pd.put("MANDATORY", "0"); // 是否是强制更新：1：强制；0：非强制
		pd.put("DOWNLOAD_PATH", ""); // 下载路径，磁盘绝对路径
		appversionctrlService.save(pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("appversionctrl:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		appversionctrlService.delete(pd);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	@RequiresPermissions("appversionctrl:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		appversionctrlService.edit(pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions("appversionctrl:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData> varList = appversionctrlService.list(page); // 列出AppVersionCtrl列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去修改页面获取数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	@RequiresPermissions("appversionctrl:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = appversionctrlService.findById(pd); // 根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@RequiresPermissions("appversionctrl:del")
	@ResponseBody
	public Object deleteAll() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (Tools.notEmpty(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			appversionctrlService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	@RequiresPermissions("toExcel")
	public ModelAndView exportExcel() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("主版本名：1.0.0"); // 1
		titles.add("子版本号：0326"); // 2
		titles.add("是否发布:1:发布；0：未发布"); // 3
		titles.add("发布日期"); // 4
		titles.add("是否是强制更新：1：强制；0：非强制"); // 5
		titles.add("下载路径，磁盘绝对路径"); // 6
		dataMap.put("titles", titles);
		List<PageData> varOList = appversionctrlService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("VERSION_NAME")); // 1
			vpd.put("var2", varOList.get(i).getString("VERSION_CODE")); // 2
			vpd.put("var3", varOList.get(i).getString("PUBLISH")); // 3
			vpd.put("var4", varOList.get(i).getString("PUBLISH_DATE")); // 4
			vpd.put("var5", varOList.get(i).getString("MANDATORY")); // 5
			vpd.put("var6", varOList.get(i).getString("DOWNLOAD_PATH")); // 6
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

}
