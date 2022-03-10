package org.fh.service.system.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.system.AppVersionCtrlMapper;
import org.fh.service.system.AppVersionCtrlService;

/** 
 * 说明： 移动端App版本控制接口实现类
 * 作者：f-sci
 * 时间：2021-03-26
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class AppVersionCtrlServiceImpl implements AppVersionCtrlService{

	@Autowired
	private AppVersionCtrlMapper appversionctrlMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		appversionctrlMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		appversionctrlMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		appversionctrlMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return appversionctrlMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return appversionctrlMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return appversionctrlMapper.findById(pd);
	}
	
	/**获取最新版本
	 * @param pd
	 * @throws Exception
	 */
	public PageData getLatest(PageData pd)throws Exception{
		return appversionctrlMapper.getLatest(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		appversionctrlMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

