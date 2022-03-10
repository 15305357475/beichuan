package org.fh.service.act;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 历史流程任务接口
 * 创建人：FH fsci
 * 授权：bsic
 */
public interface HiprocdefService {
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception;
	
	/**历史流程变量列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> hivarList(PageData pd)throws Exception;
	
	/**历史参与者列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> hiidentitylink(PageData pd)throws Exception;
	
	/**根据流程实例ID获取伴随表表名
	 * @param page
	 * @throws Exception
	 */
	public PageData getTableNameById(PageData pd)throws Exception;

}
