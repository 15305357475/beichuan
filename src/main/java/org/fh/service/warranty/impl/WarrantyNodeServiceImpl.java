package org.fh.service.warranty.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.warranty.WarrantyNodeMapper;
import org.fh.service.warranty.WarrantyNodeService;

/** 
 * 说明： 流程-节点表接口实现类
 * 作者：f-sci
 * 时间：2021-01-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class WarrantyNodeServiceImpl implements WarrantyNodeService{

	@Autowired
	private WarrantyNodeMapper warrantynodeMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		warrantynodeMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		warrantynodeMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		warrantynodeMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return warrantynodeMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return warrantynodeMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return warrantynodeMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		warrantynodeMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

