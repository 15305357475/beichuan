package org.fh.mapper.dsno1.ins;

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
public interface InsSysMapper{

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
	
	/**未进行数据摆渡的数据源
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> listDataForFerry(PageData pd);
	
	/**统计与导出
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> DataStatisticslistPage(Page page);
	
	/**统计与导出--不分页
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> DataStatisticsExport(PageData pd);
	
	/**部门隐患分类汇总 
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> DeptInsSubtotals(PageData pd);
	
	/**批量将状态更新为已摆渡 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	void UpdateSync(String[] ArrayDATA_IDS);
}

