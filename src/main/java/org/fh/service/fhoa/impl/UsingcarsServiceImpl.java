package org.fh.service.fhoa.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.UsingcarsMapper;
import org.fh.service.fhoa.UsingcarsService;

/** 
 * 说明： 用车申请接口实现类
 * 作者：f-sci
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class UsingcarsServiceImpl implements UsingcarsService{

	@Autowired
	private UsingcarsMapper usingcarsMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		usingcarsMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		usingcarsMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		usingcarsMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return usingcarsMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return usingcarsMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return usingcarsMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		usingcarsMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

