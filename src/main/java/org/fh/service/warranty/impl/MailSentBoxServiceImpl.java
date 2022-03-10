package org.fh.service.warranty.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.warranty.MailSentBoxMapper;
import org.fh.service.warranty.MailSentBoxService;

/** 
 * 说明： 保修管理模块邮件发件箱接口实现类
 * 作者：f-sci
 * 时间：2021-01-04
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class MailSentBoxServiceImpl implements MailSentBoxService{

	@Autowired
	private MailSentBoxMapper mailsentboxMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		mailsentboxMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		mailsentboxMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		mailsentboxMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return mailsentboxMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return mailsentboxMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return mailsentboxMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		mailsentboxMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

