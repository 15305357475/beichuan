package org.fh.service.fastpass;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;

/**
 * 说明：出门证媒体流接口
 * 作者：f-sci
 * 授权：bsic
 */
public interface FastPassMediaService {

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
	public List<PageData> findById(PageData pd)throws Exception;

}
