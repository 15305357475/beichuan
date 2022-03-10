package org.fh.controller.fhoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.fhoa.ReimbursementService;

/** 
 * 说明：报销申请
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/reimbursement")
public class ReimbursementController extends AcStartController {
	
	@Autowired
	private ReimbursementService reimbursementService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("reimbursement:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> zmap = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("REIMBURSEMENT_ID", this.get32UUID());	//主键
		pd.put("USERNAME", Jurisdiction.getUsername());	//用户名
		pd.put("UNAME", Jurisdiction.getName());		//姓名
		try {
			/** 工作流的操作 **/
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("申请人员", Jurisdiction.getName());			//当前用户的姓名
			map.put("报销金额", pd.getString("SUMMONEY"));
			map.put("报销类型", pd.getString("TYPE"));
			map.put("报销备注", pd.getString("REMARKS"));
			String USERNAME = Jurisdiction.getUsername();
			map.put("USERNAME", USERNAME);										//指派代理人为当前用户
			startProcessInstanceByKeyHasVariables("KEY_finance",map,USERNAME);	//启动流程实例(报销申请流程)通过KEY
			reimbursementService.save(pd);										//记录存入数据库
			zmap.put("ASSIGNEE_",Jurisdiction.getUsername());					//用于给待办人发送新任务消息
		} catch (Exception e) {
			errInfo = "errer";
		}
		zmap.put("result", errInfo);
		return zmap;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("reimbursement:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		reimbursementService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("reimbursement:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		reimbursementService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("reimbursement:list")
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
		List<PageData>	varList = reimbursementService.list(page);	//列出Reimbursement列表
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
	@RequiresPermissions("reimbursement:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = reimbursementService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("reimbursement:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			reimbursementService.deleteAll(ArrayDATA_IDS);
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
		titles.add("用户名");	//1
		titles.add("姓名");	//2
		titles.add("报销类型");	//3
		titles.add("总金额");	//4
		titles.add("备注");	//5
		dataMap.put("titles", titles);
		List<PageData> varOList = reimbursementService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("USERNAME"));	    //1
			vpd.put("var2", varOList.get(i).getString("UNAME"));	    //2
			vpd.put("var3", varOList.get(i).getString("TYPE"));	    //3
			vpd.put("var4", varOList.get(i).get("SUMMONEY").toString());	//4
			vpd.put("var5", varOList.get(i).getString("REMARKS"));	    //5
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
