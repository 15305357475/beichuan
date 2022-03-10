package org.fh.service.fhoa.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.CustomerMapper;
import org.fh.service.fhoa.CustomerService;

/** 
 * 说明： 客户管理接口实现类
 * 作者：f-sci
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	private CustomerMapper customerMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		customerMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		customerMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		customerMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return customerMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return customerMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return customerMapper.findById(pd);
	}
	
	/**根据ID组修改数据
	 * @param pd
	 * @throws Exception
	 */
	public void editByIDs(PageData pd)throws Exception{
		customerMapper.editByIDs(pd);
	}
	
	/**根据ID组获取数据
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public List<PageData> findListByIDs(String[] ArrayDATA_IDS)throws Exception{
		return customerMapper.findListByIDs(ArrayDATA_IDS);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		customerMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

