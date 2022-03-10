package org.fh.service.system;

import org.fh.entity.PageData;

/**
 * 说明：头像编辑服务接口
 * 作者：fsci
 * 授权：bsic
 */
public interface PhotoService {
	
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
	
}
