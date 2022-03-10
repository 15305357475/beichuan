package org.fh.service.ins.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.ins.GrindConfigMapper;
import org.fh.service.ins.GrindConfigService;

/** 
 * 说明： 现场区域网格化管理接口实现类
 * 作者：f-sci
 * 时间：2021-11-23
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class GrindConfigServiceImpl implements GrindConfigService{

	@Autowired
	private GrindConfigMapper grindconfigMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		grindconfigMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		grindconfigMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		grindconfigMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return grindconfigMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return grindconfigMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return grindconfigMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		grindconfigMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

