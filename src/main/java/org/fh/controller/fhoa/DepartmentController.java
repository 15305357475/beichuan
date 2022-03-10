package org.fh.controller.fhoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Department;
import org.fh.service.fhoa.DepartmentService;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：组织机构
 * 作者：f-sci
 */
@Controller
@RequestMapping("/department")
public class DepartmentController extends BaseController {
	
	@Autowired
	private DepartmentService departmentService;
	
	/**新增
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("department:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("DEPARTMENT_ID", this.get32UUID());	//主键
		departmentService.save(pd);
		map.put("result", errInfo);					//返回结果
		return map;
	}
	
	/**
	 * 删除
	 * @param DEPARTMENT_ID
	 * @param
	 * @throws Exception 
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("department:del")
	@ResponseBody
	public Object delete(@RequestParam String DEPARTMENT_ID) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("DEPARTMENT_ID", DEPARTMENT_ID);
		if(departmentService.listSubDepartmentByParentId(DEPARTMENT_ID).size() > 0){//判断是否有子级，是：不允许删除
			errInfo = "error";
		}else{
			departmentService.delete(pd);			//执行删除
		}
		map.put("result", errInfo);					//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("department:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		departmentService.edit(pd);
		map.put("result", errInfo);					//返回结果
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("department:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");								//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("keywords", KEYWORDS.trim());
		String DEPARTMENT_ID = null == pd.get("DEPARTMENT_ID")?"":pd.get("DEPARTMENT_ID").toString();
		pd.put("DEPARTMENT_ID", DEPARTMENT_ID);					//当作上级ID
		page.setPd(pd);
		List<PageData>	varList = departmentService.list(page);			//列出Department列表
		if("".equals(DEPARTMENT_ID) || "0".equals(DEPARTMENT_ID)) {
			map.put("PARENT_ID", "0");											//上级ID
		}else {
			map.put("PARENT_ID", departmentService.findById(pd).getString("PARENT_ID"));	//上级ID
		}
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	/**
	 * 显示列表ztree
	 * @return
	 */
	@RequestMapping(value="/listTree")
	@RequiresPermissions("department:list")
	@ResponseBody
	public Object listTree()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		JSONArray arr = JSONArray.fromObject(departmentService.listAllDepartment("0"));
		String json = arr.toString();
		json = json.replaceAll("DEPARTMENT_ID", "id").replaceAll("PARENT_ID", "pId").replaceAll("NAME", "name").replaceAll("subDepartment", "nodes").replaceAll("hasDepartment", "checked").replaceAll("treeurl", "url");
		map.put("zTreeNodes", json);
		map.put("result", errInfo);
		return map;
	}
	
	/**去新增页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goAdd")
	@RequiresPermissions("department:add")
	@ResponseBody
	public Object goAdd()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DEPARTMENT_ID = null == pd.get("DEPARTMENT_ID")?"":pd.get("DEPARTMENT_ID").toString();
		pd.put("DEPARTMENT_ID", DEPARTMENT_ID);					//上级ID
		map.put("pds",departmentService.findById(pd));			//传入上级所有信息
		map.put("result", errInfo);
		return map;
	}	
	
	 /**去修改页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("department:edit")
	@ResponseBody
	public Object goEdit()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = departmentService.findById(pd);									//根据ID读取		
		map.put("pd", pd);																//放入视图容器
		pd.put("DEPARTMENT_ID",pd.get("PARENT_ID").toString());					//用作上级信息
		map.put("pds",departmentService.findById(pd));							//传入上级所有信息
		map.put("result", errInfo);
		return map;
	}	

	/**判断编码是否存在
	 * @return
	 */
	@RequestMapping(value="/hasBianma")
	@ResponseBody
	public Object hasBianma() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if(departmentService.findByBianma(pd) != null){
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}

	
	/**获取指定父亲ID下的所有一级孩子列表
	 * @return
	 */
	@RequestMapping(value="/getChildrenByParentId")
	@ResponseBody
	public Object getChildrenByParentId() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String parentId = pd.getString("PARENT_ID");
		List<Department> departmentList= new ArrayList<Department>();
		departmentList = departmentService.listSubDepartmentByParentId(parentId);
		map.put("departmentList", departmentList);
		map.put("result", errInfo);				//返回结果
		return map;
	}
}
