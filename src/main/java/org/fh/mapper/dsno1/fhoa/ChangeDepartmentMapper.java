package org.fh.mapper.dsno1.fhoa;
import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 部门变更Mapper
 * 创建人：f-sci
 * 授权：bsic
 */
public interface ChangeDepartmentMapper{
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	/**保存修改
	 * @param pd
	 */
	void edit(PageData pd);
	/**通过ID获取数据
	 * @param
	 * @throws Exception
	 */
	public PageData findById(String id)throws Exception;
	
	/**通过STAFF_ID查询该员工部门变更历史
	 * @param
	 * @throws Exception
	 */
	public PageData findChangeHistoryByStaffId(String staffid)throws Exception;
	
	/**查询指定部门下未审批的申请
	 * @param page
	 * @return
	 */
	List<PageData> notApprovedChangelistPage(Page page);
}

