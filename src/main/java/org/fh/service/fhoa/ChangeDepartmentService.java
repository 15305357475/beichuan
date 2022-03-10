package org.fh.service.fhoa;
import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 部门变更接口
 * 创建人：f-sci
 * 授权：bsic
 */
public interface ChangeDepartmentService{
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	/**保存修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception;
	/**通过id获取数据
	 * @param card
	 * @throws Exception
	 */
	public PageData findById(String id)throws Exception;
	
	/**通过staff_id获取部门变更历史
	 * @param card
	 * @throws Exception
	 */
	public PageData findChangeHistoryByStaffId(String staffid)throws Exception;
	
	/**查询指定部门下未审批的申请
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> notApprovedChangelistPage(Page page)throws Exception;
	
}

