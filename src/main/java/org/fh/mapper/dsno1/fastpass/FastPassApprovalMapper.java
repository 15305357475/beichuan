package org.fh.mapper.dsno1.fastpass;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 出门证Mapper

 * 授权：bsic
 * @version
 */
public interface FastPassApprovalMapper{
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception;
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData>       datalistPage              (Page page)throws Exception;
/*              返回值                 这个接口名要和xml里的id保持一致                        传入值
 * 
 */
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**通过process_id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByProcessId(PageData pd)throws Exception;
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
}

