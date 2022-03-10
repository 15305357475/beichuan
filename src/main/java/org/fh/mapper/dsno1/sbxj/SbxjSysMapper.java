package org.fh.mapper.dsno1.sbxj;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 隐患排查主表Mapper
 * 作者：fsci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
public interface SbxjSysMapper{

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
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> listAll(PageData pd);
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	PageData findById(PageData pd);
	
	/**通过PROC_INST_ID_获取数据
	 * @param pd
	 * @throws Exception
	 */
	PageData findByPROC_INST_ID_(PageData pd);
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	void deleteAll(String[] ArrayDATA_IDS);
	
	/**根据设备编号查设备类型
	 * @param pd
	 * @throws Exception
	 */
	PageData getTypeBySbno(PageData pd);
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	void updateStatus(PageData pd);
	/**新增设备
	 * @param pd
	 * @throws Exception
	 */
	void addSb(PageData pd);
	/**
	 * 设备列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> getAllSblistPage(Page page) ;
	 /**新增
		 * @param pd
		 * @throws Exception
		 */
		void addQuestion(PageData pd);
		/**
		 * 待办提醒
		 * 
		 * @param pd
		 * @throws Exception
		 */
		List<PageData> getSbxjTaskCount(PageData pd) ;
}

