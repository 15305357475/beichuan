package org.fh.mapper.dsno1.fastpass;



import java.util.List;

import org.fh.entity.PageData;

/** 
 * 说明： 出门证Mapper

 * 授权：bsic
 * @version
 */
public interface FastPassMediaMapper{
	
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
	
	
	/**查询
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findById(PageData pd)throws Exception;
	
	
	
}

