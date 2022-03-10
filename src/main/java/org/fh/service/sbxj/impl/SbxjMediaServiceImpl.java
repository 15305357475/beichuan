package org.fh.service.sbxj.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.sbxj.SbxjMediaMapper;
import org.fh.service.sbxj.SbxjMediaService;

/** 
 * 说明： 隐患排查系统媒体表接口实现类
 * 作者：f-sci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class SbxjMediaServiceImpl implements SbxjMediaService{

	@Autowired
	private SbxjMediaMapper sbxjmediaMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		sbxjmediaMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		sbxjmediaMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		sbxjmediaMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return sbxjmediaMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return sbxjmediaMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return sbxjmediaMapper.findById(pd);
	}
	
	/**通过PROC_INST_ID_获取媒体数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByProc(PageData pd)throws Exception{
		return sbxjmediaMapper.findByProc(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		sbxjmediaMapper.deleteAll(ArrayDATA_IDS);
	}
	/**根据流程实例ID查询图片,用于导出到excel，仅两张图
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getImgByProc(PageData pd)throws Exception{
		return sbxjmediaMapper.getImgByProc(pd);
	}
}

