package org.fh.service.fhoa.impl;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.ChangeDepartmentMapper;
import org.fh.service.fhoa.ChangeDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * 说明： 部门变更
 * 创建人：f-sci
 * 授权：bsic
 */
@Service(value="ChangeDepartmentService")
@Transactional //开启事物
public class ChangeDepartmentServiceImpl implements ChangeDepartmentService{

	@Autowired
	private ChangeDepartmentMapper ChangeDepartmentMapper;
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		ChangeDepartmentMapper.save(pd);
	}
	/**保存修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception{
		ChangeDepartmentMapper.edit( pd);
	}
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(String id)throws Exception{
		return (PageData)ChangeDepartmentMapper.findById(id);
	}
	
	/**通过staffid获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findChangeHistoryByStaffId(String staffid)throws Exception{
		return (PageData)ChangeDepartmentMapper.findChangeHistoryByStaffId(staffid);
	}
	
	/**查询指定部门下未审批的申请
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> notApprovedChangelistPage(Page page) throws Exception {
		return	ChangeDepartmentMapper.notApprovedChangelistPage(page);
	}
}

