package org.fh.service.fhoa;

import org.fh.entity.PageData;

/** 
 * 说明： 组织数据权限接口
 * 创建人：FH fsci
 * 授权：bsic
 */
public interface DatajurService{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**取出某用户的组织数据权限
	 * @param pd
	 * @throws Exception
	 */
	public PageData getDEPARTMENT_IDS(String USERNAME)throws Exception;
	
	/**通过STAFF_ID删除
	 * @param ID
	 * @throws Exception
	 */
	public void delete(String STAFF_ID)throws Exception;
	
}

