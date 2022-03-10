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
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.fhoa.CustomerService;

/** 
 * 说明：客户管理
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/customer")
public class CustomerController extends BaseController {
	
	@Autowired
	private CustomerService customerService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("customer:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("CUSTOMER_ID", this.get32UUID());					//主键
		pd.put("USERNAME", Jurisdiction.getUsername());				//用户名
		customerService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("customer:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		customerService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("customer:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		customerService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("customer:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		pd.put("USERNAME", "admin".equals(Jurisdiction.getUsername())?"":Jurisdiction.getUsername()); //除admin用户外，只能查看自己的数据
		page.setPd(pd);
		List<PageData>	varList = customerService.list(page);	//列出Customer列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		map.put("isadmin", "admin".equals(Jurisdiction.getUsername())?"yes":"no");
		return map;
	}
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("customer:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = customerService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("customer:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			customerService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**批量复制
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/copyAll")
	@RequiresPermissions("customer:edit")
	@ResponseBody
	public Object copyAll()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String USERNAME = pd.getString("USERNAME");
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			List<PageData> varList = customerService.findListByIDs(ArrayDATA_IDS);
			for(int i=0;i<varList.size();i++){
				PageData npd = new PageData();
				npd = varList.get(i);
				npd.put("CUSTOMER_ID", this.get32UUID());	//主键
				npd.put("USERNAME", USERNAME);
				customerService.save(npd);
			}
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);			//返回结果
		return map;
	}
	
	 /**批量转移
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/transferAll")
	@RequiresPermissions("customer:del")
	@ResponseBody
	public Object transferAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			pd.put("IDS", ArrayDATA_IDS);
			customerService.editByIDs(pd);		//批量修改
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
		titles.add("姓名");	//1
		titles.add("手机");	//2
		titles.add("地址");	//3
		titles.add("邮箱");	//4
		titles.add("微信");	//5
		titles.add("级别");	//6
		titles.add("备注");	//7
		titles.add("归属用户");	//8
		dataMap.put("titles", titles);
		List<PageData> varOList = customerService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("NAME"));	    //1
			vpd.put("var2", varOList.get(i).getString("TEL"));	    //2
			vpd.put("var3", varOList.get(i).getString("ADDRESS"));	    //3
			vpd.put("var4", varOList.get(i).getString("EMAIL"));	    //4
			vpd.put("var5", varOList.get(i).getString("WEIXIN"));	    //5
			vpd.put("var6", varOList.get(i).getString("LEVEL"));	    //6
			vpd.put("var7", varOList.get(i).getString("REMARKS"));	    //7
			vpd.put("var8", varOList.get(i).getString("USERNAME"));	    //8
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
