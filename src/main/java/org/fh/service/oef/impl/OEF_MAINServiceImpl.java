package org.fh.service.oef.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.oef.OEF_MAINMapper;
import org.fh.service.oef.OEF_MAINService;

/** 
 * 说明： 外来人员入厂登记信息主表接口实现类
 * 作者：f-sci
 * 时间：2021-08-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class OEF_MAINServiceImpl implements OEF_MAINService{

	@Autowired
	private OEF_MAINMapper oef_mainMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		oef_mainMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		oef_mainMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		oef_mainMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return oef_mainMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return oef_mainMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return oef_mainMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		oef_mainMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

