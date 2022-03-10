package org.fh.controller.fhoa;

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
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.fhoa.NoticeService;

/** 
 * 说明：通知公告
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/notice")
public class NoticeController extends BaseController {
	
	@Autowired
	private NoticeService noticeService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("notice:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("NOTICE_ID", this.get32UUID());							//主键
		pd.put("DEPARTMENT_ID", Jurisdiction.getDEPARTMENT_IDS());		//部门ID集合
		pd.put("USERNAME", Jurisdiction.getUsername());					//用户名
		pd.put("UNAME", Jurisdiction.getName());						//发布人
		pd.put("CTIME", DateUtil.date2Str(new Date()));					//发布时间
		noticeService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("notice:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		noticeService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("notice:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		noticeService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("notice:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		String USERNAME = Jurisdiction.getUsername();					//用户名
		String DEPARTMENT_ID = Jurisdiction.getDEPARTMENT_ID();
		if("".equals(DEPARTMENT_ID) || "无权".equals(DEPARTMENT_ID)){
			pd.put("DEPARTMENT_ID","");									//根据部门ID过滤
			if(!"admin".equals(USERNAME))pd.put("USERNAME", USERNAME);	//非admin用户时
		}else{
			pd.put("DEPARTMENT_ID", DEPARTMENT_ID);
			pd.put("ORUSERNAME", USERNAME);
		}
		page.setPd(pd);
		List<PageData>	varList = noticeService.list(page);	//列出Notice列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	/**读取最新的一条
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getNewest")
	@ResponseBody
	public Object getNewest(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String USERNAME = Jurisdiction.getUsername();					//用户名
		String DEPARTMENT_ID = Jurisdiction.getDEPARTMENT_ID();
		if("".equals(DEPARTMENT_ID) || "无权".equals(DEPARTMENT_ID)){
			pd.put("DEPARTMENT_ID","");									//根据部门ID过滤
			if(!"admin".equals(USERNAME))pd.put("USERNAME", USERNAME);	//非admin用户时
		}else{
			pd.put("DEPARTMENT_ID", DEPARTMENT_ID);
			pd.put("ORUSERNAME", USERNAME);
		}
		page.setPd(pd);
		page.setShowCount(1);
		List<PageData>	varList = noticeService.list(page);	//列出Notice列表
		String notice = "", content = "",uname = "",ctime = "", noticeid = "1";
		if(varList.size() > 0) {
			noticeid = varList.get(0).getString("NOTICE_ID");	//ID
			notice = varList.get(0).getString("SYNOPSIS");		//简介
			content = varList.get(0).getString("CONTENT");		//内容
			uname = varList.get(0).getString("UNAME");			//发布人
			ctime = varList.get(0).getString("CTIME");			//创建时间
		}
		map.put("noticeid", noticeid);
		map.put("notice", notice);
		map.put("content", content);
		map.put("uname", uname);
		map.put("ctime", ctime);
		map.put("result", errInfo);
		return map;
	}
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = noticeService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("notice:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			noticeService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	@RequiresPermissions("toExcel")
	public ModelAndView exportExcel() throws Exception{
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("部门ID");	//1
		titles.add("用户名");	//2
		titles.add("发布人");	//3
		titles.add("发布时间");	//4
		titles.add("公告简介");	//5
		titles.add("公告内容");	//6
		titles.add("是否全显");	//7
		dataMap.put("titles", titles);
		List<PageData> varOList = noticeService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("DEPARTMENT_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("USERNAME"));	    //2
			vpd.put("var3", varOList.get(i).getString("UNAME"));	    //3
			vpd.put("var4", varOList.get(i).getString("CTIME"));	    //4
			vpd.put("var5", varOList.get(i).getString("SYNOPSIS"));	    //5
			vpd.put("var6", varOList.get(i).getString("CONTENT"));	    //6
			vpd.put("var7", varOList.get(i).getString("ISALL"));	    //7
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
