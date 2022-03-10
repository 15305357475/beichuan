package org.fh.service.warranty.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.warranty.MailConfigMapper;
import org.fh.service.warranty.MailConfigService;

/** 
 * 说明： 邮箱基本配置接口实现类
 * 作者：f-sci
 * 时间：2020-12-28
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class MailConfigServiceImpl implements MailConfigService{

	@Autowired
	private MailConfigMapper mailconfigMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		mailconfigMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		mailconfigMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		mailconfigMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return mailconfigMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return mailconfigMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return mailconfigMapper.findById(pd);
	}
	
	/**通过邮箱地址获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByAddress(PageData pd)throws Exception{
		return mailconfigMapper.findByAddress(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		mailconfigMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

