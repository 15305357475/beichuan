package org.fh.service.fhoa.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.SignedMapper;
import org.fh.service.fhoa.SignedService;

/** 
 * 说明： 签到记录接口实现类
 * 作者：f-sci
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class SignedServiceImpl implements SignedService{

	@Autowired
	private SignedMapper signedMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		signedMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		signedMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		signedMapper.edit(pd);
	}
	
	/**修改时间设定
	 * @param pd
	 * @throws Exception
	 */
	public void editSet(PageData pd)throws Exception{
		signedMapper.editSet(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return signedMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return signedMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return signedMapper.findById(pd);
	}
	
	/**通过id获取时间设定数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByIdSet(PageData pd)throws Exception{
		return signedMapper.findByIdSet(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		signedMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

