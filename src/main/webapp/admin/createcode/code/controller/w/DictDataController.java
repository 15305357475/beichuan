package org.fh.controller.w;

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
import org.fh.service.w.DictDataService;

/** 
 * 说明：w
 * 作者：fsci
 * 时间：2022-01-18
 * 授权：bsic
 */
@Controller
@RequestMapping("/dictdata")
public class DictDataController extends BaseController {
	
	@Autowired
	private DictDataService dictdataService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("dictdata:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("DICTDATA_ID", this.get32UUID());	//主键
		dictdataService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("dictdata:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		dictdataService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("dictdata:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		dictdataService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("dictdata:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData>	varList = dictdataService.list(page);	//列出DictData列表
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
	@RequiresPermissions("dictdata:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = dictdataService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("dictdata:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			dictdataService.deleteAll(ArrayDATA_IDS);
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
		titles.add("字典编码");	//1
		titles.add("父级编号");	//2
		titles.add("所有父级编号");	//3
		titles.add("字典标签");	//4
		titles.add("字典键值");	//5
		titles.add("字典类型");	//6
		titles.add("字典描述");	//7
		titles.add("状态（0正常 1删除 2停用）");	//8
		titles.add("创建者");	//9
		titles.add("创建时间");	//10
		titles.add("更新者");	//11
		titles.add("更新时间");	//12
		titles.add("备注信息");	//13
		dataMap.put("titles", titles);
		List<PageData> varOList = dictdataService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("DICT_CODE"));	    //1
			vpd.put("var2", varOList.get(i).getString("PARENT_CODE"));	    //2
			vpd.put("var3", varOList.get(i).getString("PARENT_CODES"));	    //3
			vpd.put("var4", varOList.get(i).getString("DICT_LABEL"));	    //4
			vpd.put("var5", varOList.get(i).getString("DICT_VALUE"));	    //5
			vpd.put("var6", varOList.get(i).getString("DICT_TYPE"));	    //6
			vpd.put("var7", varOList.get(i).getString("DESCRIPTION"));	    //7
			vpd.put("var8", varOList.get(i).getString("STATUS"));	    //8
			vpd.put("var9", varOList.get(i).getString("CREATE_BY"));	    //9
			vpd.put("var10", varOList.get(i).getString("CREATE_DATE"));	    //10
			vpd.put("var11", varOList.get(i).getString("UPDATE_BY"));	    //11
			vpd.put("var12", varOList.get(i).getString("UPDATE_DATE"));	    //12
			vpd.put("var13", varOList.get(i).getString("REMARKS"));	    //13
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
