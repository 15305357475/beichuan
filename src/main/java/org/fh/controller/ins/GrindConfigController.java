package org.fh.controller.ins;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fh.service.system.UsersService;
import org.fh.util.Jurisdiction;
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
import org.fh.service.ins.GrindConfigService;

/** 
 * 说明：现场区域网格化管理
 * 作者：fsci
 * 时间：2021-11-23
 * 授权：bsic
 */
@Controller
@RequestMapping("/grindconfig")
public class GrindConfigController extends BaseController {
	
	@Autowired
	private GrindConfigService grindconfigService;
	@Autowired
	private UsersService usersService;

	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("grindconfig:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		//pd.put("GRINDCONFIG_ID", this.get32UUID());	//主键
		pd.put("CONFIG_USER", Jurisdiction.getUsername());	//配置人
		pd.put("CONFIG_DATE", DateUtil.date2Str(new Date()));	//配置日期
		grindconfigService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("grindconfig:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		grindconfigService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("grindconfig:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		grindconfigService.edit(pd);
		map.put("result", errInfo);
		return map;
	}

	/** 查询部门下的所有网格化区域
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getGrindAreaByDeptNo")
	@ResponseBody
	public Object getGrindAreaByDeptNo() throws Exception{
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> map = new HashMap<String,Object>();
		List<PageData> varOList = grindconfigService.listAll(pd);
		map.put("result", errInfo);
		map.put("grindList", varOList);
		return map;
	}

	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("grindconfig:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");	//关键词检索条件
		String LEVEL = pd.getString("LEVEL");
		if(Tools.notEmpty(KEYWORDS)){
			pd.put("KEYWORDS", KEYWORDS.trim());
		};
		if(LEVEL.equals("ALL")){
			pd.remove("LEVEL");
		}
		page.setPd(pd);
		List<PageData>	varList = grindconfigService.list(page);	//列出GrindConfig列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("grindconfig:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = grindconfigService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("grindconfig:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			grindconfigService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}

	/**通过用户名查询用户
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/queryUserInfoByName")
	@ResponseBody
	public Object queryUserInfoByName() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		pdList = usersService.findByEmail(pd);
		map.put("userList",pdList);
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
		titles.add("网格级别");	//1
		titles.add("部门");	//2
		titles.add("网格责任范围");	//3
		titles.add("网格责任人姓名");	//4
		titles.add("绑定账号，外键");	//5
		titles.add("职务");	//6
		titles.add("联系方式");	//7
		titles.add("配置人");	//8
		titles.add("配置日期");	//9
		dataMap.put("titles", titles);
		List<PageData> varOList = grindconfigService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("LEVEL"));	    //1
			vpd.put("var2", varOList.get(i).getString("DEPT"));	    //2
			vpd.put("var3", varOList.get(i).getString("AREA"));	    //3
			vpd.put("var4", varOList.get(i).getString("RESPONSIBLE_NAME"));	    //4
			vpd.put("var5", varOList.get(i).getString("USER_ID"));	    //5
			vpd.put("var6", varOList.get(i).getString("POSITION"));	    //6
			vpd.put("var7", varOList.get(i).getString("PHONE"));	    //7
			vpd.put("var8", varOList.get(i).getString("CONFIG_USER"));	    //8
			vpd.put("var9", varOList.get(i).getString("CONFIG_DATE"));	    //9
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
