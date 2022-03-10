package org.fh.mapper.dsno1.ins;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 隐患排查系统媒体表Mapper
 * 作者：fsci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
public interface InsMediaMapper{

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
	
	/**根据流程实例ID查询图片,用于导出到excel，仅两张图
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> getImgByProc(PageData pd);
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	PageData findById(PageData pd);
	
	/**通过PROC_INST_ID_获取数据
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> findByProc(PageData pd);
	
	/**通过INS_ID获取数据
	 * @param pd
	 * @throws Exception
	 */
	List<PageData> findByINS_ID(PageData pd);
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	void deleteAll(String[] ArrayDATA_IDS);
	
}

