package org.fh.service.fhoa.impl;

import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.DatajurMapper;
import org.fh.service.fhoa.DatajurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * 说明： 组织数据权限表
 * 创建人：FH fsci
 * 授权：bsic
 */
@Service(value="datajurService")
@Transactional //开启事物
public class DatajurServiceImpl implements DatajurService{
	
	@Autowired
	private DatajurMapper datajurMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		datajurMapper.save( pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		datajurMapper.edit(pd);
	}
	
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)datajurMapper.findById(pd);
	}
	
	/**取出某用户的组织数据权限
	 * @param pd
	 * @throws Exception
	 */
	public PageData getDEPARTMENT_IDS(String USERNAME)throws Exception{
		return (PageData)datajurMapper.getDEPARTMENT_IDS(USERNAME);
	}
	
	/**通过STAFF_ID删除
	 * @param ID
	 * @throws Exception
	 */
	public void delete(String STAFF_ID)throws Exception{
		datajurMapper.delete(STAFF_ID);
	}
}

