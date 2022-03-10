package org.fh.mapper.dsno1.fhoa;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 签到记录Mapper
 * 作者：fsci
 * 授权：bsic
 * @version
 */
public interface SignedMapper{

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
	
	/**修改时间设定
	 * @param pd
	 * @throws Exception
	 */
	void editSet(PageData pd);
	
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
	
	/**通过id获取时间设定数据
	 * @param pd
	 * @throws Exception
	 */
	PageData findByIdSet(PageData pd);
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	void deleteAll(String[] ArrayDATA_IDS);
	
}

