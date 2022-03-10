package org.fh.service.fhoa.impl;

import java.util.ArrayList;
import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Department;
import org.fh.mapper.dsno1.fhoa.DepartmentMapper;
import org.fh.service.fhoa.DepartmentService;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * 说明： 组织机构
 * 创建人：f-sic
 * 授权：bsic
 */
@Service(value="departmentService")
@Transactional //开启事物
public class DepartmentServiceImpl implements DepartmentService{

	@Autowired
	private DepartmentMapper departmentMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		departmentMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		departmentMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		departmentMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)departmentMapper.datalistPage(page);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)departmentMapper.findById(pd);
	}
	
	/**通过编码获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByBianma(PageData pd)throws Exception{
		return (PageData)departmentMapper.findByBianma(pd);
	}
	
	/**
	 * 通过ID获取其子级列表
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	@Cacheable(key="#parentId",value="deptList")
	public List<Department> listSubDepartmentByParentId(String parentId) throws Exception {
		return (List<Department>)departmentMapper.listSubDepartmentByParentId(parentId);
	}
	
	/**
	 * 获取所有数据并填充每条数据的子级列表(递归处理)
	 * @param MENU_ID
	 * @return
	 * @throws Exception
	 */
	public List<Department> listAllDepartment(String parentId) throws Exception {
		List<Department> departmentList = this.listSubDepartmentByParentId(parentId);
		for(Department depar : departmentList){
			depar.setTreeurl("department_list.html?DEPARTMENT_ID="+depar.getDEPARTMENT_ID());
			depar.setSubDepartment(this.listAllDepartment(depar.getDEPARTMENT_ID()));
			depar.setTarget("treeFrame");
			depar.setIcon("../../../assets/images/user.gif");
		}
		return departmentList;
	}
	
	/**
	 * 获取所有数据并填充每条数据的子级列表(递归处理)下拉ztree用
	 * @param MENU_ID
	 * @return
	 * @throws Exception
	 */
	public List<PageData> listAllDepartmentToSelect(String parentId,List<PageData> zdepartmentPdList) throws Exception {
		List<PageData>[] arrayDep = this.listAllbyPd(parentId,zdepartmentPdList);
		List<PageData> departmentPdList = arrayDep[1];
		for(PageData pd : departmentPdList){
			this.listAllDepartmentToSelect(pd.getString("id"),arrayDep[0]);
		}
		return arrayDep[0];
	}
	
	/**下拉ztree用
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData>[] listAllbyPd(String parentId,List<PageData> zdepartmentPdList) throws Exception {
		List<Department> departmentList = this.listSubDepartmentByParentId(parentId);
		List<PageData> departmentPdList = new ArrayList<PageData>();
		for(Department depar : departmentList){
			PageData pd = new PageData();
			pd.put("id", depar.getDEPARTMENT_ID());
			pd.put("parentId", depar.getPARENT_ID());
			pd.put("name", depar.getNAME());
			pd.put("icon", "../../../assets/images/user.gif");
			departmentPdList.add(pd);
			zdepartmentPdList.add(pd);
		}
		List<PageData>[] arrayDep = new List[2];
		arrayDep[0] = zdepartmentPdList;
		arrayDep[1] = departmentPdList;
		return arrayDep;
	}

	/**
	 * 获取所有数据并填充每条数据的子级列表(递归处理)下拉ztree用
	 * @param MENU_ID
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getAllCompamyToSelect(String parentId,List<PageData> zdepartmentPdList) throws Exception {
		List<PageData>[] arrayDep = this.listCompamybyPd(parentId,zdepartmentPdList);
		List<PageData> departmentPdList = arrayDep[1];
		for(PageData pd : departmentPdList){
			this.getAllCompamyToSelect(pd.getString("id"),arrayDep[0]);
		}
		return arrayDep[0];
	}
	
	/**下拉ztree用
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData>[] listCompamybyPd(String parentId,List<PageData> zdepartmentPdList) throws Exception {
		List<Department> departmentList = this.listSubDepartmentByParentId(parentId);
		List<PageData> departmentPdList = new ArrayList<PageData>();
		for(Department depar : departmentList){
			PageData pd = new PageData();
			pd.put("id", depar.getDEPARTMENT_ID());
			pd.put("parentId", depar.getPARENT_ID());
			pd.put("name", depar.getNAME());
			pd.put("icon", "../assets/images/user.gif");
			departmentPdList.add(pd);
			zdepartmentPdList.add(pd);
		}
		List<PageData>[] arrayDep = new List[2];
		arrayDep[0] = zdepartmentPdList;
		arrayDep[1] = departmentPdList;
		return arrayDep;
	}
	/**获取某个部门所有下级部门ID(返回拼接字符串 in的形式， ('a','b','c'))
	 * @param DEPARTMENT_ID
	 * @return
	 * @throws Exception
	 */
	public String getDEPARTMENT_IDS(String DEPARTMENT_ID) throws Exception {
		DEPARTMENT_ID = Tools.notEmpty(DEPARTMENT_ID)?DEPARTMENT_ID:"0";
		List<PageData> zdepartmentPdList = new ArrayList<PageData>();
		zdepartmentPdList = this.listAllDepartmentToSelect(DEPARTMENT_ID,zdepartmentPdList);
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for(PageData dpd : zdepartmentPdList){
			sb.append("'");
			sb.append(dpd.getString("id"));
			sb.append("'");
			sb.append(",");
		}
		sb.append("'fh')");
		return sb.toString();
	}

	/**向上递归查询
	 * @param DEPARTMENT_ID,levelp[0,4]
	 * @throws Exception
	 */
	public PageData RecursionUpwardById(String DEPARTMENT_ID,int level) throws Exception {
		PageData pd = new PageData();
		pd = (PageData)departmentMapper.RecursionUpwardById(DEPARTMENT_ID);
		PageData result = new PageData();
		if(null != pd) {
			String c_level = pd.getString("C_LEVEL");
			String p_level = pd.getString("P_LEVEL");
			//System.out.println("孩子级别：" + pd.getString("C_LEVEL") + ";父亲级别：" + pd.getString("P_LEVEL"));
			switch (level) {
			case 0:	//根目录
				if(c_level.equals("0")){
					result.put("DEPARTMENT_ID", pd.getString("C_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("C_NAME"));
					result.put("PARENT_ID", pd.getString("C_PARENT_ID"));
					result.put("LEVEL", pd.getString("C_LEVEL"));
					break;
				}
				else if(p_level.equals("0")) {
					result.put("DEPARTMENT_ID", pd.getString("P_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("P_NAME"));
					result.put("PARENT_ID", pd.getString("P_PARENT_ID"));
					result.put("LEVEL", pd.getString("P_LEVEL"));
					break;
				}
				break;
			case 1:	//公司
				if(c_level.equals("00701")){
					result.put("DEPARTMENT_ID", pd.getString("C_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("C_NAME"));
					result.put("PARENT_ID", pd.getString("C_PARENT_ID"));
					result.put("LEVEL", pd.getString("C_LEVEL"));
					break;
				}
				else if(p_level.equals("00701")) {
					result.put("DEPARTMENT_ID", pd.getString("P_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("P_NAME"));
					result.put("PARENT_ID", pd.getString("P_PARENT_ID"));
					result.put("LEVEL", pd.getString("P_LEVEL"));
					break;
				}
				break;
			case 2:	//部门
				if(c_level.equals("00702")){
					result.put("DEPARTMENT_ID", pd.getString("C_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("C_NAME"));
					result.put("PARENT_ID", pd.getString("C_PARENT_ID"));
					result.put("LEVEL", pd.getString("C_LEVEL"));
					break;
				}
				else if(p_level.equals("00702")) {
					result.put("DEPARTMENT_ID", pd.getString("P_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("P_NAME"));
					result.put("PARENT_ID", pd.getString("P_PARENT_ID"));
					result.put("LEVEL", pd.getString("P_LEVEL"));
					break;
				}
				break;
			case 3:	//科室
				if(c_level.equals("00703")){
					result.put("DEPARTMENT_ID", pd.getString("C_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("C_NAME"));
					result.put("PARENT_ID", pd.getString("C_PARENT_ID"));
					result.put("LEVEL", pd.getString("C_LEVEL"));
					break;
				}
				else if(p_level.equals("00703")) {
					result.put("DEPARTMENT_ID", pd.getString("P_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("P_NAME"));
					result.put("PARENT_ID", pd.getString("P_PARENT_ID"));
					result.put("LEVEL", pd.getString("P_LEVEL"));
					break;
				}
				break;
			case 4:	//班组
				if(c_level.equals("00704")){
					result.put("DEPARTMENT_ID", pd.getString("C_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("C_NAME"));
					result.put("PARENT_ID", pd.getString("C_PARENT_ID"));
					result.put("LEVEL", pd.getString("C_LEVEL"));
					break;
				}
				else if(p_level.equals("00704")) {
					result.put("DEPARTMENT_ID", pd.getString("P_DEPARTMENT_ID"));
					result.put("NAME", pd.getString("P_NAME"));
					result.put("PARENT_ID", pd.getString("P_PARENT_ID"));
					result.put("LEVEL", pd.getString("P_LEVEL"));
					break;
				}
				break;
			default:
				break;
			}
		}
		return result;
	}
}

