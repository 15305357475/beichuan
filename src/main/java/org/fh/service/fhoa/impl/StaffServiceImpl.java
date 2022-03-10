package org.fh.service.fhoa.impl;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.StaffMapper;
import org.fh.service.fhoa.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * 说明： 员工管理
 * 创建人：fsci
 * 授权：bsic
 */
@Service(value="staffService")
@Transactional //开启事物
public class StaffServiceImpl implements StaffService{

	@Autowired
	private StaffMapper staffMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		staffMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		staffMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		staffMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)staffMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)staffMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)staffMapper.findById(pd);
	}
	
	/**通过UserId获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByUserId(PageData pd)throws Exception{
		return (PageData)staffMapper.findByUserId(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		staffMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**绑定用户
	 * @param pd
	 * @throws Exception
	 */
	public void userBinding(PageData pd)throws Exception{
		staffMapper.userBinding(pd);
	}
	
	/**通过身份证号批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAllBySFID(String[] ArrayDATA_IDS)throws Exception{
		staffMapper.deleteAllBySFID(ArrayDATA_IDS);
	}
}

