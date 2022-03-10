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
import org.fh.service.fhoa.WorklogService;

/** 
 * 说明：工作日志
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/worklog")
public class WorklogController extends BaseController {
	
	@Autowired
	private WorklogService worklogService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("worklog:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("WORKLOG_ID", this.get32UUID());						//主键
		pd.put("UNAME", Jurisdiction.getName());					//姓名
		pd.put("CTIME", DateUtil.date2Str(new Date()));				//填写时间
		pd.put("USERNAME", Jurisdiction.getUsername());				//用户名
		pd.put("DEPARTMENT_ID", Jurisdiction.getDEPARTMENT_ID());	//用有的最高数据权限的部门ID
		worklogService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("worklog:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		worklogService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("worklog:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		worklogService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("worklog:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String isMy = pd.getString("isMy");
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		String lastStart = pd.getString("lastStart");					//开始时间
		String lastEnd = pd.getString("lastEnd");						//结束时间
		if(Tools.notEmpty(lastStart))pd.put("lastStart", lastStart+" 00:00:00");
		if(Tools.notEmpty(lastEnd))pd.put("lastEnd", lastEnd+" 00:00:00");
		String USERNAME = Jurisdiction.getUsername();					//用户名
		String item = Jurisdiction.getDEPARTMENT_IDS();
		if("false".equals(isMy)){
			if("".equals(item) || "无权".equals(item)){
				pd.put("item","");											//根据部门ID过滤
				if(!"admin".equals(USERNAME))pd.put("USERNAME", USERNAME);	//非admin用户时
			}else{
				pd.put("ORUSERNAME", USERNAME);
				pd.put("item", item.replaceFirst("\\(", "\\('"+Jurisdiction.getDEPARTMENT_ID()+"',"));
			}
		}else {
			pd.put("USERNAME", USERNAME);
		}
		page.setPd(pd);
		List<PageData>	varList = worklogService.list(page);			//列出Worklog列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("USERNAME", USERNAME);
		map.put("result", errInfo);
		return map;
	}
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("worklog:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = worklogService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
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
		titles.add("姓名");	//1
		titles.add("工作内容");	//2
		titles.add("填写时间");	//3
		titles.add("备注");	//4
		titles.add("用户名");	//5
		titles.add("部门ID");	//6
		dataMap.put("titles", titles);
		List<PageData> varOList = worklogService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("UNAME"));	    //1
			vpd.put("var2", varOList.get(i).getString("CONTENT"));	    //2
			vpd.put("var3", varOList.get(i).getString("CTIME"));	    //3
			vpd.put("var4", varOList.get(i).getString("BZ"));	    //4
			vpd.put("var5", varOList.get(i).getString("USERNAME"));	    //5
			vpd.put("var6", varOList.get(i).getString("DEPARTMENT_ID"));	    //6
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
