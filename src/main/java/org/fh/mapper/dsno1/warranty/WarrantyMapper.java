package org.fh.mapper.dsno1.warranty;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 保修流程实例Mapper
 * 作者：fsci
 * 时间：2021-01-08
 * 授权：bsic
 * @version
 */
public interface WarrantyMapper{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	void save(PageData pd);
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	void delete(PageData pd);
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	void edit(PageData pd);
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	List<PageData> datalistPage(Page page);
	
	/**列表--正在处理的流程
	 * @param page
	 * @throws Exception
	 */
	List<PageData> datalistPageOnRun(Page page);
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> listAll(PageData pd);
	
	/**获取节点邮件数据
	 * @param page
	 * @throws Exception
	 */
	List<PageData> ListNodeMail(PageData pd);
	
	/**根据保单号获取节点邮件数据
	 * @param page
	 * @throws Exception
	 */
	List<PageData> ListNodeMailByWarrantyId(PageData pd);
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	PageData findById(PageData pd);
	
	/**通过流程实例id获取数据
	 * @param pd
	 * @throws Exception
	 */
	PageData findByPROC_INST_ID_(PageData pd);
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	void deleteAll(String[] ArrayDATA_IDS);
	
	/**未进行数据摆渡的数据源
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> listDataForFerry(PageData pd);
	
	/**批量将状态更新为已摆渡 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	void UpdateSync(String[] ArrayDATA_IDS);
}

