package org.fh.service.warranty.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.warranty.WarrantyMailRelaMapper;
import org.fh.service.warranty.WarrantyMailRelaService;

/** 
 * 说明： 流程-邮件关系表接口实现类
 * 作者：f-sci
 * 时间：2021-01-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class WarrantyMailRelaServiceImpl implements WarrantyMailRelaService{

	@Autowired
	private WarrantyMailRelaMapper warrantymailrelaMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		warrantymailrelaMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		warrantymailrelaMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		warrantymailrelaMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return warrantymailrelaMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return warrantymailrelaMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return warrantymailrelaMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		warrantymailrelaMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

