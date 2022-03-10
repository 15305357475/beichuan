package org.fh.service.sbxj;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

/** 
 * 说明： 隐患排查系统媒体表接口
 * 作者：fsci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
public interface SbxjMediaService{

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
	
	/**通过XJ_ID_获取媒体数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByProc(PageData pd)throws Exception;
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	/**根据ID查询图片,用于导出到excel，仅两张图
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getImgByProc(PageData pd)throws Exception;
}

