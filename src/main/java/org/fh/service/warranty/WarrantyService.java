package org.fh.service.warranty;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/**
 * 说明： 保修流程实例接口 作者：fsci 时间：2021-01-08 授权：bsic
 * 
 * @version
 */
public interface WarrantyService {

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception;

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception;

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception;

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page) throws Exception;

	/**
	 * 列表--正在处理的流程列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> listOnRun(Page page) throws Exception;

	/**
	 * 列表(全部)
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd) throws Exception;

	/**
	 * 获取节点邮件数据
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> ListNodeMail(PageData pd) throws Exception;

	/**
	 * 根据保单号获取节点邮件数据
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> ListNodeMailByWarrantyId(PageData pd) throws Exception;

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception;

	/**
	 * 通过流程实例id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByPROC_INST_ID_(PageData pd) throws Exception;

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception;
	
	/**
	 * 未进行数据摆渡的数据源
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listDataForFerry(PageData pd) throws Exception;

	/**
	 * 批量将状态更新为已摆渡 
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void UpdateSync(String[] ArrayDATA_IDS) throws Exception;
}
