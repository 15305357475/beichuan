package org.fh.controller.fhoa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.fhoa.ScheduleService;

/** 
 * 说明：日程安排
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/schedule")
public class ScheduleController extends BaseController {
	
	@Autowired
	private ScheduleService scheduleService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		//pd.put("SCHEDULE_ID", this.get32UUID());		//主键
		pd.put("USERNAME", Jurisdiction.getUsername());	//用户名
		scheduleService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		scheduleService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		scheduleService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		pd.put("USERNAME", Jurisdiction.getUsername()); 				//只能查看自己的数据
		page.setPd(pd);
		List<PageData>	varList = scheduleService.list(page);			//列出Schedule列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	/**获取数据
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getData")
	@ResponseBody
	public Object getData() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("USERNAME", Jurisdiction.getUsername()); 				//只能查看自己的数据
		List<PageData>	varList = scheduleService.listAll(pd);			//列出Schedule列表
		for(PageData spd : varList) {
			spd.put("id", spd.getString("SCHEDULE_ID"));
			spd.put("title", spd.getString("TITLE"));
			spd.put("desc", spd.getString("FHDESC"));
			spd.put("date", spd.getString("CDATA"));
			spd.remove("SCHEDULE_ID");
			spd.remove("USERNAME");
			spd.remove("TITLE");
			spd.remove("CDATA");
			spd.remove("FHDESC");
		}
		map.put("varList", varList);
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
		pd = scheduleService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			scheduleService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
}
