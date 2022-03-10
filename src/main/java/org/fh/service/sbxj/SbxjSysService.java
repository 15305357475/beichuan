package org.fh.service.sbxj;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 隐患排查主表接口
 * 作者：fsci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
public interface SbxjSysService{

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
	public List<PageData> list(Page page)throws Exception;
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**通过PROC_INST_ID_获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByPROC_INST_ID_(PageData pd)throws Exception;	
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
	/**根据设备编号查设备类型
	 * @param pd
	 * @throws Exception
	 */
	public PageData getTypeBySbno(PageData pd)throws Exception;
	
	/**设备待修状态设置
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatus(PageData pd)throws Exception;
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void addSb(PageData pd)throws Exception;
	/**
	 * 
	 *  设备列表
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> getAllSblistPage(Page page) throws Exception;
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void addQuestion(PageData pd)throws Exception;
	/**待办提醒
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getSbxjTaskCount(PageData pd) throws Exception;
}

