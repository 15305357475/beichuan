package org.fh.controller.fhoa;

import java.util.ArrayList;
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
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.fhoa.VehicleService;

/** 
 * 说明：车辆管理
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/vehicle")
public class VehicleController extends BaseController {
	
	@Autowired
	private VehicleService vehicleService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("vehicle:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("VEHICLE_ID", this.get32UUID());	//主键
		vehicleService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("vehicle:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		vehicleService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("vehicle:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		vehicleService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("vehicle:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");	//关键词检索条件
		String REORDER = pd.getString("REORDER");	//排序
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		if(Tools.notEmpty(REORDER)) {
			if("1".equals(REORDER))pd.put("REORDER1", "ok");	//车险到期逆序
			if("2".equals(REORDER))pd.put("REORDER2", "ok");	//年检到期逆序
			if("3".equals(REORDER))pd.put("REORDER3", "ok");	//保养时间正序
		}
		page.setPd(pd);
		List<PageData>	varList = vehicleService.list(page);	//列出Vehicle列表
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
	@RequiresPermissions("vehicle:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = vehicleService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("vehicle:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			vehicleService.deleteAll(ArrayDATA_IDS);
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
		titles.add("车牌号");	//1
		titles.add("核载");	//2
		titles.add("类型");	//3
		titles.add("品牌颜色");	//4
		titles.add("车险到期");	//5
		titles.add("年检到期");	//6
		titles.add("公里数");	//7
		titles.add("保养时间");	//8
		titles.add("备注");	//9
		titles.add("状态");	//10
		dataMap.put("titles", titles);
		List<PageData> varOList = vehicleService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("CARNUMBER"));	    //1
			vpd.put("var2", varOList.get(i).getString("NUCLEARLOAD"));	    //2
			vpd.put("var3", varOList.get(i).getString("TYPE"));	    //3
			vpd.put("var4", varOList.get(i).getString("BRAND"));	    //4
			vpd.put("var5", varOList.get(i).getString("INSURANCETIME"));	    //5
			vpd.put("var6", varOList.get(i).getString("YEARLYINSPECTION"));	    //6
			vpd.put("var7", varOList.get(i).get("KILOMETRE").toString());	//7
			vpd.put("var8", varOList.get(i).getString("MAINTENANCE"));	    //8
			vpd.put("var9", varOList.get(i).getString("REMARKS"));	    //9
			vpd.put("var10", varOList.get(i).getString("STATE"));	    //10
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
