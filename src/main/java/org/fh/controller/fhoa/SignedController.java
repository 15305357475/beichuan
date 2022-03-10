package org.fh.controller.fhoa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.fh.service.fhoa.SignedService;

/** 
 * 说明：签到记录
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/signed")
public class SignedController extends BaseController {
	
	@Autowired
	private SignedService signedService;
	
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
		HttpServletRequest request = this.getRequest();
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			ip = request.getRemoteAddr();  
	    }else{
	    	ip = request.getHeader("x-forwarded-for");  
	    }
		pd.put("PLACE", pd.getString("PLACE")+" IP:"+ip);	//签到地址和IP
		pd.put("SIGNED_ID", this.get32UUID());				//主键
		pd.put("USERNAME", Jurisdiction.getUsername());		//用户名
		pd.put("UNAME", Jurisdiction.getName());			//姓名
		pd.put("CTIME", DateUtil.date2Str(new Date()));		//时间
		pd.put("CDATA", DateUtil.getDay());					//日期
		pd.put("STATE", "等待核查");
		pd.put("REMARKS", "无");
		int nowtime = Integer.parseInt((new SimpleDateFormat("HHmmss")).format(new Date()));
		PageData spd = new PageData();
		spd.put("SIGNEDTIME_ID", "1");
		spd = signedService.findByIdSet(spd);	//读取设定的时间规则
		int ms = Integer.parseInt(spd.getString("MSTIME").replaceAll(":", ""));
		int me = Integer.parseInt(spd.getString("METIME").replaceAll(":", ""));
		int as = Integer.parseInt(spd.getString("ASTIME").replaceAll(":", ""));
		int ae = Integer.parseInt(spd.getString("AETIME").replaceAll(":", ""));
		if(nowtime > ms && nowtime < me) pd.put("REMARKS", "上午打卡时间异常");
		if(nowtime > as && nowtime < ae) pd.put("REMARKS", "下午打卡时间异常");
		signedService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("signed:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		signedService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("signed:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		signedService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("signed:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData>	varList = signedService.list(page);	//列出Signed列表
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
	@RequiresPermissions("signed:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = signedService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("signed:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			signedService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**去修改页面获取时间设定数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEditSet")
	@RequiresPermissions("signed:edit")
	@ResponseBody
	public Object goEditSet() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = signedService.findByIdSet(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	/**修改时间设定
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/editSet")
	@RequiresPermissions("signed:edit")
	@ResponseBody
	public Object editSet() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		signedService.editSet(pd);
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
		titles.add("用户名");	//1
		titles.add("姓名");	//2
		titles.add("时间");	//3
		titles.add("地点");	//4
		titles.add("经度");	//5
		titles.add("纬度");	//6
		titles.add("状态");	//7
		titles.add("备注");	//8
		titles.add("日期");	//9
		dataMap.put("titles", titles);
		List<PageData> varOList = signedService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("USERNAME"));	    //1
			vpd.put("var2", varOList.get(i).getString("UNAME"));	    //2
			vpd.put("var3", varOList.get(i).getString("CTIME"));	    //3
			vpd.put("var4", varOList.get(i).getString("PLACE"));	    //4
			vpd.put("var5", varOList.get(i).getString("LONGITUDE"));	    //5
			vpd.put("var6", varOList.get(i).getString("LATITUDE"));	    //6
			vpd.put("var7", varOList.get(i).getString("STATE"));	    //7
			vpd.put("var8", varOList.get(i).getString("REMARKS"));	    //8
			vpd.put("var9", varOList.get(i).getString("CDATA"));	    //9
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
