package org.fh.controller.fhoa;

import java.text.SimpleDateFormat;
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
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.fhoa.PayrollService;
import org.fh.service.fhoa.StaffService;

/** 
 * 说明：员工工资
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/payroll")
public class PayrollController extends BaseController {
	
	@Autowired
	private PayrollService payrollService;
	@Autowired
	private StaffService staffService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("payroll:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		PageData countPd = new PageData();
		pd.put("YEARMONTH", pd.getString("YEAR")+"-"+pd.getString("MONTH"));	//年月份
		countPd = payrollService.findCount(pd);	//查询某月份的数据量
		int count = Integer.parseInt(countPd.get("DATACOUNT").toString());
		if(0 == count) {	//此月份下有数据，生成失败 否则正常进行
			pd.put("PAYROLL_ID", this.get32UUID());	//主键
			payrollService.save(pd);
		}else {
			errInfo = "fail";
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**批量生成
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/creat")
	@RequiresPermissions("payroll:add")
	@ResponseBody
	public Object creat() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("YEARMONTH", pd.getString("YEAR")+"-"+pd.getString("MONTH"));	//年月份
		Double WAGESPAYABLE = Double.parseDouble(pd.get("WAGESPAYABLE").toString());
		Double REALWAGES = Double.parseDouble(pd.get("REALWAGES").toString());
		PageData countPd = new PageData();
		countPd = payrollService.findCount(pd);	//查询某月份的数据量
		int count = Integer.parseInt(countPd.get("DATACOUNT").toString());
		if(0 == count) {	//此月份下有数据，生成失败 否则正常进行
			List<PageData> varList = staffService.listAll(pd);
			for(int i=0;i<varList.size();i++){
				pd.put("PAYROLL_ID", this.get32UUID());	//主键
				pd.put("NAME", varList.get(i).getString("NAME"));					//姓名
				pd.put("DEPARTMENT", varList.get(i).getString("DNAME"));			//部门
				pd.put("STAFF_ID", varList.get(i).getString("STAFF_ID"));			//员工ID
				pd.put("BIANMA", varList.get(i).getString("BIANMA"));				//编码
				pd.put("BASESALARY", varList.get(i).get("BASESALARY").toString());	//底薪
				pd.put("REMARKS", "无");												//备注
				pd.put("WAGESPAYABLE", WAGESPAYABLE + Double.parseDouble(varList.get(i).get("BASESALARY").toString()));	//应发工资
				pd.put("REALWAGES", REALWAGES + Double.parseDouble(varList.get(i).get("BASESALARY").toString()));		//实发工资	
				payrollService.save(pd);
			}
		}else {
			errInfo = "fail";
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("payroll:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		payrollService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("payroll:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		payrollService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("payroll:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String isp = pd.getString("isp");			//当从打印按钮进入时是yes
		String KEYWORDS = pd.getString("KEYWORDS");	//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		String YEAR = pd.getString("YEAR");			//年
		String MONTH = pd.getString("MONTH");		//月
		if(Tools.isEmpty(YEAR)&&Tools.notEmpty(MONTH))pd.put("YEAR", new SimpleDateFormat("yyyy").format(new Date()));
		PageData sumPd = new PageData();
		sumPd = payrollService.sumData(pd);			//金额合计
		if("yes".equals(isp))page.setShowCount(50);
		page.setPd(pd);
		List<PageData>	varList = payrollService.list(page);	//列出Payroll列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("sumPd", sumPd);
		map.put("result", errInfo);
		return map;
	}
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("payroll:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = payrollService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("payroll:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			payrollService.deleteAll(ArrayDATA_IDS);
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
	public ModelAndView exportExcel(Page page) throws Exception{
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");	//关键词检索条件
		//if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", new String(KEYWORDS.trim().getBytes("iso-8859-1"), "UTF-8"));  
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS); 
		String YEAR = pd.getString("YEAR");			//年
		String MONTH = pd.getString("MONTH");		//月
		if(Tools.isEmpty(YEAR)&&Tools.notEmpty(MONTH))pd.put("YEAR", new SimpleDateFormat("yyyy").format(new Date()));
		page.setPd(pd);
		page.setShowCount(60000);
		List<PageData>	varOList = payrollService.list(page);	//列出Payroll列表
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("姓名");	//1
		titles.add("部门");	//2
		titles.add("底薪");	//3
		titles.add("绩效");	//4
		titles.add("全勤奖");	//5
		titles.add("其它奖金");	//6
		titles.add("食宿补助");	//7
		titles.add("交通补助");	//8
		titles.add("通讯补助");	//9
		titles.add("出差补助");	//10
		titles.add("其它补助");	//11
		titles.add("加班费");	//12
		titles.add("请假扣除");	//13
		titles.add("迟到旷工");	//14
		titles.add("违纪罚款");	//15
		titles.add("五险一金");	//16
		titles.add("个税");	//17
		titles.add("其它扣款");	//18
		titles.add("应发工资");	//19
		titles.add("实发工资");	//20
		titles.add("月份");	//21
		titles.add("备注");	//22
		titles.add("员工编码");	//23
		dataMap.put("titles", titles);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("NAME"));	    //1
			vpd.put("var2", varOList.get(i).getString("DEPARTMENT"));	    //2
			vpd.put("var3", varOList.get(i).get("BASESALARY").toString());	//3
			vpd.put("var4", varOList.get(i).get("ACHIEVEMENTS").toString());	//4
			vpd.put("var5", varOList.get(i).get("FULLATTENDANCE").toString());	//5
			vpd.put("var6", varOList.get(i).get("BONUS").toString());	//6
			vpd.put("var7", varOList.get(i).get("ACCOMMODATIONSUBSIDY").toString());	//7
			vpd.put("var8", varOList.get(i).get("TRAFFICSUBSIDY").toString());	//8
			vpd.put("var9", varOList.get(i).get("PHONEALLOWANCE").toString());	//9
			vpd.put("var10", varOList.get(i).get("TRAVELALLOWANCE").toString());	//10
			vpd.put("var11", varOList.get(i).get("OTHERSUBSIDIES").toString());	//11
			vpd.put("var12", varOList.get(i).get("OVERTIMEPAY").toString());	//12
			vpd.put("var13", varOList.get(i).get("LEAVEDEDUCTION").toString());	//13
			vpd.put("var14", varOList.get(i).get("LATEABSENTEEISM").toString());	//14
			vpd.put("var15", varOList.get(i).get("FINE").toString());	//15
			vpd.put("var16", varOList.get(i).get("INSURANCE").toString());	//16
			vpd.put("var17", varOList.get(i).get("TAX").toString());	//17
			vpd.put("var18", varOList.get(i).get("OTHERDEDUCTIONS").toString());	//18
			vpd.put("var19", varOList.get(i).get("WAGESPAYABLE").toString());	//19
			vpd.put("var20", varOList.get(i).get("REALWAGES").toString());	//20
			vpd.put("var21", varOList.get(i).getString("YEARMONTH"));	    //21
			vpd.put("var22", varOList.get(i).getString("REMARKS"));	    //22
			vpd.put("var23", varOList.get(i).getString("BIANMA"));	    //23
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
