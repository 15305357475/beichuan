package org.fh.service.ins.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.ins.InsMediaMapper;
import org.fh.service.ins.InsMediaService;

/** 
 * 说明： 隐患排查系统媒体表接口实现类
 * 作者：f-sci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class InsMediaServiceImpl implements InsMediaService{

	@Autowired
	private InsMediaMapper insmediaMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		insmediaMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		insmediaMapper.delete(pd);
	}
	
	/**修改,根据隐患INSSYS_ID更新媒体表中的隐患流程实例PROC_INST_ID_
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		insmediaMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return insmediaMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return insmediaMapper.listAll(pd);
	}
	
	/**根据流程实例ID查询图片,用于导出到excel，仅两张图
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getImgByProc(PageData pd)throws Exception{
		return insmediaMapper.getImgByProc(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return insmediaMapper.findById(pd);
	}
	
	/**通过PROC_INST_ID_获取媒体数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByProc(PageData pd)throws Exception{
		return insmediaMapper.findByProc(pd);
	}
	
	/**通过INSSYS_ID获取媒体数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByINS_ID(PageData pd)throws Exception{
		return insmediaMapper.findByINS_ID(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		insmediaMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

